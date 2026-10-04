package me.wietse3.wca.content.link.copycat;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import me.wietse3.wca.integration.copycats.CopycatsRegistry;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.copycatsplus.copycats.utility.BlockUtils.transformFacing;

public class CopycatLinkBlock extends WrenchableDirectionalBlock implements IBE<CopycatLinkBlockEntity>, ICopycatBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty RECEIVER = RedstoneLinkBlock.RECEIVER;

    public CopycatLinkBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false)
                .setValue(RECEIVER, false));
    }

    public static boolean fromRegular(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof RedstoneLinkBlockEntity oldBE))
            return false;

        BlockState oldState = level.getBlockState(pos);
        CompoundTag data = oldBE.saveCustomOnly(level.registryAccess());

        BlockState newState = CopycatsRegistry.COPYCAT_REDSTONE_LINK.getDefaultState().getBlock()
                .withPropertiesOf(oldState);
        level.setBlockAndUpdate(pos, newState);

        if (level.getBlockEntity(pos) instanceof CopycatLinkBlockEntity newBE) {
            newBE.loadWithComponents(data, level.registryAccess());
            newBE.updateSelfAndAttached(newState);
        }

        level.scheduleTick(pos, newState.getBlock(), 1);
        return true;
    }

    public static boolean toRegular(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof CopycatLinkBlockEntity oldBE))
            return false;

        BlockState oldState = level.getBlockState(pos);
        CompoundTag data = oldBE.saveCustomOnly(level.registryAccess());

        BlockState newState = AllBlocks.REDSTONE_LINK.getDefaultState().getBlock()
                .withPropertiesOf(oldState);
        level.setBlockAndUpdate(pos, newState);

        if (level.getBlockEntity(pos) instanceof RedstoneLinkBlockEntity newBE) {
            newBE.loadWithComponents(data, level.registryAccess());
            newBE.updateSelfAndAttached(newState);
        }

        level.scheduleTick(pos, newState.getBlock(), 1);
        return true;
    }

    @Override
    public boolean canToggleCT(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        return false;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        ICopycatBlock.super.setPlacedBy(worldIn, pos, state, placer, stack);
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
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hitResult) {
        return ICopycatBlock.super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
                                boolean isMoving) {
        if (level.isClientSide)
            return;

        Direction blockFacing = state.getValue(FACING);
        if (fromPos.equals(pos.relative(blockFacing.getOpposite()))) {
            if (!canSurvive(state, level, pos)) {
                level.destroyBlock(pos, true);
                return;
            }
        }

        if (!level.getBlockTicks()
                .willTickThisTick(pos, this))
            level.scheduleTick(pos, this, 1);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource r) {
        updateTransmittedSignal(state, level, pos);

        if (state.getValue(RECEIVER))
            return;
        Direction attachedFace = state.getValue(CopycatLinkBlock.FACING)
                .getOpposite();
        BlockPos attachedPos = pos.relative(attachedFace);
        level.blockUpdated(pos, level.getBlockState(pos)
                .getBlock());
        level.blockUpdated(attachedPos, level.getBlockState(attachedPos)
                .getBlock());
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getBlock() == oldState.getBlock() || isMoving)
            return;
        updateTransmittedSignal(state, worldIn, pos);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        ICopycatBlock.super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston, super::onRemove);
    }

    public void updateTransmittedSignal(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide)
            return;
        if (state.getValue(RECEIVER))
            return;

        int power = getPower(level, state, pos);
        int powerFromPanels = getBlockEntityOptional(level, pos).map(be -> {
                    if (be.panelSupport == null)
                        return 0;
                    Boolean tri = be.panelSupport.shouldBePoweredTristate();
                    if (tri == null)
                        return -1;
                    return tri ? 15 : 0;
                })
                .orElse(0);

        // Suppress update if an input panel exists but is not loaded
        if (powerFromPanels == -1)
            return;

        power = Math.max(power, powerFromPanels);

        boolean previouslyPowered = state.getValue(POWERED);
        if (previouslyPowered != power > 0)
            level.setBlock(pos, state.cycle(POWERED), Block.UPDATE_CLIENTS);

        int transmit = power;
        withBlockEntityDo(level, pos, be -> be.transmit(transmit));
    }

    private static int getPower(Level level, BlockState state, BlockPos pos) {
        int power = 0;
        for (Direction direction : Iterate.directions)
            power = Math.max(level.getSignal(pos.relative(direction), direction), power);
        for (Direction direction : Iterate.directions) {
            if (state.getValue(FACING).getOpposite() != direction)
                power = Math.max(level.getSignal(pos.relative(direction), Direction.UP), power);
        }
        return power;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return state.getValue(POWERED) && state.getValue(RECEIVER);
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (side != blockState.getValue(FACING))
            return 0;
        return getSignal(blockState, blockAccess, pos, side);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (!state.getValue(RECEIVER))
            return 0;
        return getBlockEntityOptional(blockAccess, pos).map(CopycatLinkBlockEntity::getReceivedSignal)
                .orElse(0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, RECEIVER);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ICopycatBlockEntity copycatBE = getCopycatBlockEntity(level, pos);
        if (copycatBE != null && copycatBE.hasCustomMaterial())
            return ICopycatBlock.super.useWithoutItem(state, level, pos, player, hitResult);

        if (player.isShiftKeyDown() && toggleMode(state, level, pos) == InteractionResult.SUCCESS) {
            level.scheduleTick(pos, this, 1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public InteractionResult toggleMode(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        return onBlockEntityUse(level, pos, be -> {
            Boolean wasReceiver = state.getValue(RECEIVER);
            boolean blockPowered = level.hasNeighborSignal(pos);
            level.setBlock(pos, state.cycle(RECEIVER)
                    .setValue(POWERED, blockPowered), Block.UPDATE_ALL);
            be.transmit(wasReceiver ? 0 : getPower(level, state, pos));
            return InteractionResult.SUCCESS;
        });
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
    public BlockState getRotatedBlockState(BlockState originalState, Direction _targetedFace) {
        return originalState;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return side != null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockPos neighbourPos = pos.relative(state.getValue(FACING)
                .getOpposite());
        BlockState neighbour = worldIn.getBlockState(neighbourPos);
        return !neighbour.canBeReplaced();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        state = state.setValue(FACING, context.getClickedFace());
        return state;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.CASING_3PX.get(state.getValue(FACING));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public Class<CopycatLinkBlockEntity> getBlockEntityClass() {
        return CopycatLinkBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CopycatLinkBlockEntity> getBlockEntityType() {
        return CopycatsRegistry.COPYCAT_REDSTONE_LINK_BE.get();
    }
}