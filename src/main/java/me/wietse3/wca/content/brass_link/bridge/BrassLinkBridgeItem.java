package me.wietse3.wca.content.brass_link.bridge;

import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.recipe.ItemCopyingRecipe.SupportsItemCopying;
import com.simibubi.create.foundation.utility.CreateLang;
import me.wietse3.wca.content.brass_link.BrassLinkBehavior;
import me.wietse3.wca.registry.WCADataComponents;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class BrassLinkBridgeItem extends Item implements SupportsItemCopying {

    public BrassLinkBridgeItem(Properties properties) {
        super(properties);
    }

    public static String getFrequency(ItemStack stack) {
        return stack.getOrDefault(WCADataComponents.LINK_BRIDGE_FREQUENCY, "");
    }

    public static void setFrequency(ItemStack stack, String frequency) {
        if (frequency.isBlank())
            stack.remove(WCADataComponents.LINK_BRIDGE_FREQUENCY);
        else
            stack.set(WCADataComponents.LINK_BRIDGE_FREQUENCY, frequency.strip());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return !getFrequency(stack).isBlank();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (AllKeys.shiftDown())
            return;

        String frequency = getFrequency(stack);
        if (!frequency.isBlank()) {
            tooltip.add(CommonComponents.SPACE);
            tooltip.add(CreateLang.text("-> ")
                    .style(ChatFormatting.GRAY)
                    .add(CreateLang.text(frequency)
                            .style(ChatFormatting.GOLD))
                    .component());
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BrassLinkBehavior link = BlockEntityBehaviour.get(context.getLevel(), context.getClickedPos(), BrassLinkBehavior.TYPE);
        if (link == null)
            return InteractionResult.PASS;

        ItemStack stack = context.getItemInHand();
        setFrequency(stack, link.getNetworkKey());

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (world.isClientSide)
                CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> openScreen(heldItem));
            return InteractionResultHolder.success(heldItem);
        }
        return InteractionResultHolder.pass(heldItem);
    }

    @OnlyIn(Dist.CLIENT)
    private void openScreen(ItemStack stack) {
        ScreenOpener.open(new BrassLinkBridgeScreen(stack));
    }

    @Override
    public DataComponentType<?> getComponentType() {
        return WCADataComponents.LINK_BRIDGE_FREQUENCY;
    }
}
