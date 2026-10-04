package me.wietse3.wca.content.brass_link.copycat;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.StructureTransform;
import me.wietse3.wca.content.brass_link.BrassRedstoneLinkBlock;
import me.wietse3.wca.content.brass_link.BrassRedstoneLinkBlockEntity;
import me.wietse3.wca.integration.copycats.CopycatsRegistry;
import me.wietse3.wca.content.brass_link.BrassRedstoneLink;
import me.wietse3.wca.registry.WCABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.copycatsplus.copycats.utility.BlockUtils.transformFacing;

public class CopycatBrassLinkBlock extends BrassRedstoneLink<CopycatBrassLinkBlockEntity> implements ICopycatBlock {

    public CopycatBrassLinkBlock(Properties properties) {
        super(properties);
    }

    public static boolean fromRegular(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof BrassRedstoneLinkBlockEntity oldBE))
            return false;

        BlockState oldState = level.getBlockState(pos);
        CompoundTag data = oldBE.saveCustomOnly(level.registryAccess());

        BlockState newState = CopycatsRegistry.COPYCAT_BRASS_REDSTONE_LINK.getDefaultState().getBlock()
                .withPropertiesOf(oldState);
        level.setBlockAndUpdate(pos, newState);

        if (level.getBlockEntity(pos) instanceof CopycatBrassLinkBlockEntity newBE) {
            newBE.loadWithComponents(data, level.registryAccess());
            newBE.updateSelfAndAttached(newState);
        }

        level.scheduleTick(pos, newState.getBlock(), 1);
        return true;
    }

    public static boolean toRegular(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof CopycatBrassLinkBlockEntity oldBE))
            return false;

        BlockState oldState = level.getBlockState(pos);
        CompoundTag data = oldBE.saveCustomOnly(level.registryAccess());

        BlockState newState = WCABlocks.BRASS_REDSTONE_LINK.getDefaultState().getBlock()
                .withPropertiesOf(oldState);
        level.setBlockAndUpdate(pos, newState);

        if (level.getBlockEntity(pos) instanceof BrassRedstoneLinkBlockEntity newBE) {
            newBE.loadWithComponents(data, level.registryAccess());
            newBE.updateSelfAndAttached(newState);
        }

        level.scheduleTick(pos, newState.getBlock(), 1);
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.CASING_3PX.get(state.getValue(FACING));
    }

    @Override
    public boolean canToggleCT(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        return false;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hitResult) {
        return ICopycatBlock.super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                            BlockHitResult hitResult) {
        ICopycatBlockEntity copycatBE = getCopycatBlockEntity(level, pos);

        if (copycatBE != null && copycatBE.hasCustomMaterial())
            return ICopycatBlock.super.useWithoutItem(state, level, pos, player, hitResult);

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        ICopycatBlockEntity copycatBE = getCopycatBlockEntity(context.getLevel(), context.getClickedPos());
        if (copycatBE != null && copycatBE.hasCustomMaterial())
            return ICopycatBlock.super.onWrenched(state, context);

        Player player = context.getPlayer();
        if (player != null && !player.isShiftKeyDown()) {
            context.getLevel()
                    .levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, context.getClickedPos(), Block.getId(state));
            boolean success = toRegular(context.getLevel(), context.getClickedPos());

            if (!player.isCreative() && success)
                player.getInventory().placeItemBackInInventory(AllBlocks.COPYCAT_PANEL.asStack());

            return InteractionResult.SUCCESS;
        }

        return super.onWrenched(state, context);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return super.rotate(state, rot);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        ICopycatBlock.super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        ICopycatBlock.super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving, super::onRemove);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);
        ICopycatBlock.super.playerWillDestroy(level, pos, state, player);
        return state;
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        return state.setValue(FACING, transformFacing(transform, state.getValue(FACING)));
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state,
                                     BlockState neighborState, Direction dir) {
        return ICopycatBlock.hidesNeighborFace(level, pos, state, neighborState, dir);
    }

    @Override
    public boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public Class<CopycatBrassLinkBlockEntity> getBlockEntityClass() {
        return CopycatBrassLinkBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CopycatBrassLinkBlockEntity> getBlockEntityType() {
        return CopycatsRegistry.COPYCAT_BRASS_REDSTONE_LINK_BE.get();
    }
}
