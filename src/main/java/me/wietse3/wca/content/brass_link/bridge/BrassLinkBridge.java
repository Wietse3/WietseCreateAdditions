package me.wietse3.wca.content.brass_link.bridge;

import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import me.wietse3.wca.config.WCAConfigs;
import me.wietse3.wca.content.brass_link.IBrassLinkable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;

public class BrassLinkBridge implements IBrassLinkable {

    private final IRedstoneLinkable link;

    public BrassLinkBridge(IRedstoneLinkable link) {
        this.link = link;
    }

    public static boolean canBridge(LevelAccessor world, IRedstoneLinkable link) {
        Block block = world.getBlockState(link.getLocation()).getBlock();
        if (WCAConfigs.server().links.bridgeBlacklist.get().contains(BuiltInRegistries.BLOCK.getKey(block).toString()))
            return false;

        return getItemFreq(link.getNetworkKey().getFirst().getStack()) != null
                || getItemFreq(link.getNetworkKey().getSecond().getStack()) != null;
    }

    public static String getFrequencyFrom(IRedstoneLinkable link) {
        if (link instanceof IBrassLinkable brassLink)
            return brassLink.getFrequency();

        String frequency = getItemFreq(link.getNetworkKey().getFirst().getStack());
        if (frequency != null)
            return frequency;

        frequency = getItemFreq(link.getNetworkKey().getSecond().getStack());
        return frequency != null ? frequency : "";
    }

    @Override
    public int getTransmittedStrength() {
        return link.getTransmittedStrength();
    }

    @Override
    public void setReceivedStrength(int power) {
        link.setReceivedStrength(power);
    }

    @Override
    public boolean isListening() {
        return link.isListening();
    }

    @Override
    public boolean isAlive() {
        return link.isAlive();
    }

    @Override
    public String getFrequency() {
        return getFrequencyFrom(link);
    }

    @Override
    public BlockPos getLocation() {
        return link.getLocation();
    }

    private static String getItemFreq(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof BrassLinkBridgeItem)
            return BrassLinkBridgeItem.getFrequency(stack);
        return null;
    }

}
