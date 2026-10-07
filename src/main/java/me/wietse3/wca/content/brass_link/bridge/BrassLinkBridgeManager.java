package me.wietse3.wca.content.brass_link.bridge;

import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import me.wietse3.wca.WietseCreateAdditions;
import net.minecraft.world.level.LevelAccessor;

import java.util.IdentityHashMap;
import java.util.Map;

public class BrassLinkBridgeManager {

    private static final Map<IRedstoneLinkable, BrassLinkBridge> BRIDGES = new IdentityHashMap<>();

    public static boolean add(LevelAccessor world, IRedstoneLinkable link) {
        if (!BrassLinkBridge.canBridge(link))
            return false;

        BrassLinkBridge bridge = BRIDGES.computeIfAbsent(link, BrassLinkBridge::new);
        WietseCreateAdditions.BRASS_LINK_NETWORK_HANDLER.addToNetwork(world, bridge);

        return true;
    }

    public static void remove(LevelAccessor world, IRedstoneLinkable link) {
        BrassLinkBridge bridge = BRIDGES.remove(link);
        if (bridge == null)
            return;

        WietseCreateAdditions.BRASS_LINK_NETWORK_HANDLER.removeFromNetwork(world, bridge);
    }

    public static BrassLinkBridge get(IRedstoneLinkable link) {
        return BRIDGES.get(link);
    }

    public static boolean contains(IRedstoneLinkable link) {
        return BRIDGES.containsKey(link);
    }

}
