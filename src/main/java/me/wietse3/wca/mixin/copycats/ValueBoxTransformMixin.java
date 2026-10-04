package me.wietse3.wca.mixin.copycats;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import me.wietse3.wca.content.link.copycat.CopycatLinkBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ValueBoxTransform.Dual.class)
public class ValueBoxTransformMixin {

    @Inject(method = "testHit", at = @At("HEAD"), cancellable = true)
    private void wca$copycatLinkInteraction(
            LevelAccessor level,
            BlockPos pos,
            BlockState state,
            Vec3 localHit,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (level.getBlockEntity(pos) instanceof CopycatLinkBlockEntity copycat && copycat.hasCustomMaterial()) {
            cir.setReturnValue(false);
        }
    }
}
