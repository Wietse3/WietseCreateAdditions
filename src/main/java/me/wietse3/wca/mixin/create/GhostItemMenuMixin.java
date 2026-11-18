package me.wietse3.wca.mixin.create;

import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import me.wietse3.wca.content.brass_link.bridge.BrassLinkBridgeItem;
import me.wietse3.wca.registry.WCAItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GhostItemMenu.class)
public class GhostItemMenuMixin {

    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void wca$copyFrequency(int slotId, int dragType, ClickType clickTypeIn, Player player, CallbackInfo ci) {
        if (clickTypeIn != ClickType.PICKUP || dragType != 1 || slotId < 36)
            return;

        GhostItemMenu<?> menu = (GhostItemMenu<?>) (Object) this;

        ItemStack ghost = menu.ghostInventory.getStackInSlot(slotId - 36);
        ItemStack held = menu.getCarried();

        if (!ghost.is(WCAItems.BRASS_LINK_BRIDGE.get()) || !held.is(WCAItems.BRASS_LINK_BRIDGE.get()))
            return;

        String frequency = BrassLinkBridgeItem.getFrequency(ghost);
        BrassLinkBridgeItem.setFrequency(menu.getCarried(), frequency);

        ci.cancel();
    }
}
