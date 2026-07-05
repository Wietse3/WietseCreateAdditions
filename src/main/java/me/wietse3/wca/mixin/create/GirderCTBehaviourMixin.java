package me.wietse3.wca.mixin.create;

import com.simibubi.create.content.decoration.girder.GirderCTBehaviour;
import me.wietse3.wca.registry.WCABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GirderCTBehaviour.class)
public class GirderCTBehaviourMixin {

    @Inject(
            method = "connectsTo",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void wca$connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos,
                                BlockPos otherPos, Direction face, CallbackInfoReturnable<Boolean> cir) {
        if (other.is(WCABlocks.METAL_GIRDER_VERTICAL_SHAFT.get())) {
            cir.setReturnValue(true);
        }
    }
}
