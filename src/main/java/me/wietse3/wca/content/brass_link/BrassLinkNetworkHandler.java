package me.wietse3.wca.content.brass_link;

import com.simibubi.create.infrastructure.config.AllConfigs;
import me.wietse3.wca.WietseCreateAdditions;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.joml.Vector3d;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BrassLinkNetworkHandler {

    static final Map<LevelAccessor, Map<String, Set<IBrassLinkable>>> connections =
            new IdentityHashMap<>();

    public final AtomicInteger globalPowerVersion = new AtomicInteger();

    public void onLoadWorld(LevelAccessor world) {
        connections.put(world, new HashMap<>());
        WietseCreateAdditions.LOGGER.debug("Prepared Redstone Network Space for " + WorldHelper.getDimensionID(world));
    }

    public void onUnloadWorld(LevelAccessor world) {
        connections.remove(world);
        WietseCreateAdditions.LOGGER.debug("Removed Redstone Network Space for " + WorldHelper.getDimensionID(world));
    }

    public Set<IBrassLinkable> getNetworkOf(LevelAccessor world, IBrassLinkable actor) {
        Map<String, Set<IBrassLinkable>> networksInWorld = networksIn(world);
        String key = actor.getNetworkKey();
        if (!networksInWorld.containsKey(key))
            networksInWorld.put(key, new LinkedHashSet<>());
        return networksInWorld.get(key);
    }

    public void addToNetwork(LevelAccessor world, IBrassLinkable actor) {
        getNetworkOf(world, actor).add(actor);
        updateNetworkOf(world, actor);
    }

    public void removeFromNetwork(LevelAccessor world, IBrassLinkable actor) {
        Set<IBrassLinkable> network = getNetworkOf(world, actor);
        network.remove(actor);
        if (network.isEmpty()) {
            networksIn(world).remove(actor.getNetworkKey());
            return;
        }
        updateNetworkOf(world, actor);
    }

    public void updateNetworkOf(LevelAccessor world, IBrassLinkable actor) {
        Set<IBrassLinkable> network = getNetworkOf(world, actor);
        globalPowerVersion.incrementAndGet();
        int power = 0;

        for (Iterator<IBrassLinkable> iterator = network.iterator(); iterator.hasNext(); ) {
            IBrassLinkable other = iterator.next();
            if (!other.isAlive()) {
                iterator.remove();
                continue;
            }

            if (!withinRange(actor, other))
                continue;

            if (power < 15)
                power = Math.max(other.getTransmittedStrength(), power);
        }

        if (actor instanceof BrassLinkBehavior linkBehaviour) {
            // fix one-to-one loading order problem
            if (linkBehaviour.isListening()) {
                linkBehaviour.newPosition = true;
                linkBehaviour.setReceivedStrength(power);
            }
        }

        for (IBrassLinkable other : network) {
            if (other != actor && other.isListening() && withinRange(actor, other))
                other.setReceivedStrength(power);
        }
    }

    public static boolean withinRange(IBrassLinkable from, IBrassLinkable to) {
        if (from == to)
            return true;
        return from.getLocation()
                .closerThan(to.getLocation(), AllConfigs.server().logistics.linkRange.get());
    }

    public Map<String, Set<IBrassLinkable>> networksIn(LevelAccessor world) {
        if (!connections.containsKey(world)) {
            WietseCreateAdditions.LOGGER.warn("Tried to Access unprepared network space of " + WorldHelper.getDimensionID(world));
            return new HashMap<>();
        }
        return connections.get(world);
    }

    public boolean hasAnyLoadedPower(String frequency) {
        for (Map<String, Set<IBrassLinkable>> map : connections.values()) {
            Set<IBrassLinkable> set = map.get(frequency);
            if (set == null || set.isEmpty())
                continue;
            for (IBrassLinkable link : set)
                if (link.getTransmittedStrength() > 0)
                    return true;
        }
        return false;
    }

}
