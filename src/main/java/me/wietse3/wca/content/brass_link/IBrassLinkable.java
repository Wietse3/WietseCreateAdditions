package me.wietse3.wca.content.brass_link;

import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import me.wietse3.wca.content.brass_link.bridge.BrassLinkBridgeItem;
import me.wietse3.wca.registry.WCAItems;
import net.createmod.catnip.data.Couple;
import net.minecraft.world.item.ItemStack;

public interface IBrassLinkable extends IRedstoneLinkable {

    String getFrequency();

    @Override
    default Couple<Frequency> getNetworkKey() {
        ItemStack bridgeItem = WCAItems.BRASS_LINK_BRIDGE.asStack();
        BrassLinkBridgeItem.setFrequency(bridgeItem, getFrequency());

        return Couple.create(Frequency.of(bridgeItem), Frequency.EMPTY);
    }
}
