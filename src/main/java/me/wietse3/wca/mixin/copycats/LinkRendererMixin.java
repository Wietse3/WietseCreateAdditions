package me.wietse3.wca.mixin.copycats;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.LinkRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import me.wietse3.wca.content.link.copycat.CopycatLinkBlockEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LinkRenderer.class)
public class LinkRendererMixin {

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/BlockEntityBehaviour;get(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lcom/simibubi/create/foundation/blockEntity/behaviour/BehaviourType;)Lcom/simibubi/create/foundation/blockEntity/behaviour/BlockEntityBehaviour;"
            )
    )
    private static BlockEntityBehaviour wca$hideCopycatValueBoxes(
            BlockEntityBehaviour link,
            @Local(name = "world") ClientLevel world,
            @Local(name = "pos") BlockPos pos
    ) {
        if (link instanceof LinkBehaviour
                && world.getBlockEntity(pos) instanceof CopycatLinkBlockEntity copycat
                && copycat.hasCustomMaterial()) {
            return link;
        }

        return link;
    }
}
