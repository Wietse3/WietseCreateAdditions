package me.wietse3.wca.content.girder;

import com.google.common.base.Predicates;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.infrastructure.config.AllConfigs;
import me.wietse3.wca.registry.WCABlocks;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeMod;

import java.util.List;
import java.util.function.Predicate;

public class GirderVerticalShaftPlacementHelper implements IPlacementHelper {

    @Override
    public Predicate<ItemStack> getItemPredicate() {
        return Predicates.or(AllBlocks.METAL_GIRDER::isIn, AllBlocks.SHAFT::isIn);
    }

    @Override
    public Predicate<BlockState> getStatePredicate() {
        return WCABlocks.METAL_GIRDER_VERTICAL_SHAFT::has;
    }

    private boolean canExtendToward(BlockState state, Direction side) {
        if (state.getBlock() instanceof GirderVerticalShaftBlock || state.getBlock() instanceof GirderBlock) {
            Axis axis = side.getAxis();
            return axis == Axis.Y;
        }
        return false;
    }

    private int attachedPoles(Level world, BlockPos pos, Direction direction) {
        BlockPos checkPos = pos.relative(direction);
        BlockState state = world.getBlockState(checkPos);
        int count = 0;
        while (canExtendToward(state, direction)) {
            count++;
            checkPos = checkPos.relative(direction);
            state = world.getBlockState(checkPos);
        }
        return count;
    }

    @Override
    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        List<Direction> directions =
                IPlacementHelper.orderedByDistance(pos, ray.getLocation(), dir -> canExtendToward(state, dir));
        for (Direction dir : directions) {
            int range = AllConfigs.server().equipment.placementAssistRange.get();
            if (player != null) {
                AttributeInstance reach = player.getAttribute(ForgeMod.BLOCK_REACH.get());
                if (reach != null && reach.hasModifier(ExtendoGripItem.singleRangeAttributeModifier))
                    range += 4;
            }
            int poles = attachedPoles(world, pos, dir);
            if (poles >= range)
                continue;

            BlockPos newPos = pos.relative(dir, poles + 1);
            BlockState newState = world.getBlockState(newPos);

            if (!newState.canBeReplaced())
                continue;

            return PlacementOffset.success(newPos);
        }

        return PlacementOffset.fail();
    }
}
