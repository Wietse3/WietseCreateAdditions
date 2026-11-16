package me.wietse3.wca.mixin.create;

import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import me.wietse3.wca.content.brass_link.BrassLinkClientHandler;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityBehaviour.class)
public abstract class BlockEntityBehaviourMixin {

    @Shadow
    public abstract Level getWorld();

    @Inject(method = "initialize", at = @At("TAIL"))
    private void wca$initialize(CallbackInfo ci) {
        if (!getWorld().isClientSide)
            return;

        if (this instanceof IRedstoneLinkable link) {
            BrassLinkClientHandler.add(getWorld(), link);
        }
    }

    @Inject(method = "unload", at = @At("TAIL"))
    private void wca$unload(CallbackInfo ci) {
        if (!getWorld().isClientSide)
            return;

        if (this instanceof IRedstoneLinkable link) {
            BrassLinkClientHandler.remove(link);
        }
    }
}
