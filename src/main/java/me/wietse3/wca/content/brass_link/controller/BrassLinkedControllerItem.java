package me.wietse3.wca.content.brass_link.controller;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import me.wietse3.wca.registry.WCABlocks;
import me.wietse3.wca.registry.WCAItems;
import me.wietse3.wca.util.ExtendedControlsUtil;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class BrassLinkedControllerItem extends Item {

    public BrassLinkedControllerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null)
            return InteractionResult.PASS;
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState hitState = world.getBlockState(pos);

        if (player.mayBuild()) {
            if (player.isShiftKeyDown()) {
                if (WCABlocks.LECTERN_BRASS_CONTROLLER.has(hitState)) {
                    if (!world.isClientSide)
                        WCABlocks.LECTERN_BRASS_CONTROLLER.get().withBlockEntityDo(world, pos, be ->
                                be.swapControllers(stack, player, ctx.getHand(), hitState));
                    return InteractionResult.SUCCESS;
                }
            } else {
                if (WCABlocks.BRASS_REDSTONE_LINK.has(hitState)) {
                    if (world.isClientSide)
                        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.toggleBindMode(ctx.getClickedPos()));
                    player.getCooldowns()
                            .addCooldown(this, 2);
                    return InteractionResult.SUCCESS;
                }

                if (hitState.is(Blocks.LECTERN) && !hitState.getValue(LecternBlock.HAS_BOOK)) {
                    if (!world.isClientSide) {
                        ItemStack lecternStack = player.isCreative() ? stack.copy() : stack.split(1);
                        WCABlocks.LECTERN_BRASS_CONTROLLER.get().replaceLectern(hitState, world, pos, lecternStack);
                    }
                    return InteractionResult.SUCCESS;
                }

                if (WCABlocks.LECTERN_BRASS_CONTROLLER.has(hitState))
                    return InteractionResult.PASS;
            }
        }

        return use(world, player, ctx.getHand()).getResult();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (world.isClientSide)
                CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> openScreen(heldItem));
            return InteractionResultHolder.success(heldItem);
        }

        if (!player.isShiftKeyDown()) {
            if (world.isClientSide)
                CatnipServices.PLATFORM.executeOnClientOnly(() -> this::toggleActive);
            player.getCooldowns()
                    .addCooldown(this, 2);
        }

        return InteractionResultHolder.pass(heldItem);
    }

    @OnlyIn(Dist.CLIENT)
    private void openScreen(ItemStack stack) {
        ScreenOpener.open(new BrassLinkedControllerScreen(stack));
    }

    @OnlyIn(Dist.CLIENT)
    private void toggleBindMode(BlockPos pos) {
        BrassLinkedControllerClientHandler.toggleBindMode(pos);
    }

    @OnlyIn(Dist.CLIENT)
    private void toggleActive() {
        BrassLinkedControllerClientHandler.toggle();
    }

    public static List<BrassControllerBind> getFrequencies(ItemStack stack) {
        if (WCAItems.BRASS_LINKED_CONTROLLER.get() != stack.getItem())
            throw new IllegalArgumentException("Cannot get frequencies from non-brass controller: " + stack);

        List<BrassControllerBind> entries = BrassControllerBind.CODEC.listOf()
                .parse(NbtOps.INSTANCE, stack.getOrCreateTag().get("BrassLinkedControllerLinks"))
                .result().orElse(Collections.emptyList());

        List<BrassControllerBind> binds = new ArrayList<>();

        for (int i = 0; i < ExtendedControlsUtil.getExtendedControls().size(); i++) {
            if (i < entries.size()) {
                binds.add(entries.get(i));
            } else {
                binds.add(BrassControllerBind.EMPTY);
            }
        }

        return binds;
    }

    public static BrassControllerBind toFrequency(ItemStack stack, int index) {
        if (WCAItems.BRASS_LINKED_CONTROLLER.get() != stack.getItem())
            throw new IllegalArgumentException("Cannot get frequencies from non-brass controller: " + stack);

        List<BrassControllerBind> binds = BrassControllerBind.CODEC.listOf()
                .parse(NbtOps.INSTANCE, stack.getOrCreateTag().get("BrassLinkedControllerLinks"))
                .result().orElse(Collections.emptyList());

        if (index >= binds.size())
            return BrassControllerBind.EMPTY;

        return binds.get(index);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new BrassLinkedControllerItemRenderer()));
    }
}
