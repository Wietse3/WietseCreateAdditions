package me.wietse3.wca.content.brass_link;

import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import me.wietse3.wca.config.WCAConfigs;
import me.wietse3.wca.content.brass_link.bridge.BrassLinkBridge;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

import java.util.*;

public class BrassLinkClientHandler {

    private static final Set<IRedstoneLinkable> links = new HashSet<>();

    public static void add(Level level, IRedstoneLinkable link) {
        if (!BrassLinkBridge.canBridge(level, link))
            return;
        links.add(link);
    }

    public static void remove(IRedstoneLinkable link) {
        links.remove(link);
    }

    public static void clear() {
        links.clear();
    }

    public static List<IntAttached<String>> createSuggestions(Level level, BlockPos blockPos) {
        List<IntAttached<String>> frequencies = new ArrayList<>();
        Map<String, Integer> nearest = new HashMap<>();

        Vector3d pos = JOMLConversion.atCenterOf(blockPos);
        int linkRange = WCAConfigs.server().links.linkRange.get();

        for (IRedstoneLinkable link : links) {
            String frequency = BrassLinkBridge.getFrequencyFrom(link);
            if (frequency.isBlank())
                continue;

            Vector3d linkPos = JOMLConversion.atCenterOf(link.getLocation());
            double distance = SableCompanion.INSTANCE.distanceSquaredWithSubLevels(level, pos, linkPos);

            if (distance >= (double) linkRange * linkRange)
                continue;

            nearest.merge(frequency, (int) distance, Math::min);
        }

        for (Map.Entry<String, Integer> entry : nearest.entrySet())
            frequencies.add(IntAttached.with(entry.getValue(), entry.getKey()));

        return frequencies;
    }

}
