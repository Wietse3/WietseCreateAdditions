package me.wietse3.wca.mixin.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import me.wietse3.wca.registry.WCABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

@Mixin(ShaftBlock.class)
public class ShaftBlockMixin {

    @Inject(
            method = "useItemOn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void wca$encaseVerticalShaft(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit,
            CallbackInfoReturnable<ItemInteractionResult> cir
    ) {
        if (player == null)
            return;

        if (player.isShiftKeyDown() || !player.mayBuild())
            return;

        if (!AllBlocks.METAL_GIRDER.isIn(stack) || state.getValue(ShaftBlock.AXIS) != Direction.Axis.Y)
            return;

        KineticBlockEntity.switchToBlockState(level, pos, WCABlocks.METAL_GIRDER_VERTICAL_SHAFT.getDefaultState()
                .setValue(WATERLOGGED, state.getValue(WATERLOGGED)));

        level.playSound(null, pos, SoundEvents.NETHERITE_BLOCK_HIT, SoundSource.BLOCKS, 0.5f, 1.25f);
        if (!level.isClientSide && !player.isCreative()) {
            stack.shrink(1);
            if (stack.isEmpty())
                player.setItemInHand(hand, ItemStack.EMPTY);
        }

        cir.setReturnValue(ItemInteractionResult.SUCCESS);
    }
}

