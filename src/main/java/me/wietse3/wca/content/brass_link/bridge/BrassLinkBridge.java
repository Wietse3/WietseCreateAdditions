package me.wietse3.wca.content.brass_link.bridge;

import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import me.wietse3.wca.content.brass_link.IBrassLinkable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public class BrassLinkBridge implements IBrassLinkable {

    private final IRedstoneLinkable link;

    public BrassLinkBridge(IRedstoneLinkable link) {
        this.link = link;
    }

    public static boolean canBridge(IRedstoneLinkable link) {
        return getFrequency(link.getNetworkKey().getFirst().getStack()) != null
                || getFrequency(link.getNetworkKey().getSecond().getStack()) != null;
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
    public String getNetworkKey() {
        String frequency = getFrequency(link.getNetworkKey().getFirst().getStack());
        if (frequency != null)
            return frequency;

        frequency = getFrequency(link.getNetworkKey().getSecond().getStack());
        return frequency != null ? frequency : "";
    }

    @Override
    public BlockPos getLocation() {
        return link.getLocation();
    }

    private static String getFrequency(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof BrassLinkBridgeItem) {
            String frequency = BrassLinkBridgeItem.getFrequency(stack);
            if (frequency.isBlank())
                return null;
            return frequency;
        }
        return null;
    }

}
