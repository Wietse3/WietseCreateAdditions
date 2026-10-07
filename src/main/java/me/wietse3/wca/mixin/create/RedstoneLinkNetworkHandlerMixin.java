package me.wietse3.wca.mixin.create;

import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.bridge.BrassLinkBridgeManager;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RedstoneLinkNetworkHandler.class)
public class RedstoneLinkNetworkHandlerMixin {

    @Inject(method = "addToNetwork", at = @At("HEAD"), cancellable = true)
    private void addToNetwork(LevelAccessor world, IRedstoneLinkable actor, CallbackInfo ci) {
        if (BrassLinkBridgeManager.add(world, actor))
            ci.cancel();
    }

    @Inject(method = "removeFromNetwork", at = @At("HEAD"), cancellable = true)
    private void wca$removeFromNetwork(LevelAccessor world, IRedstoneLinkable actor, CallbackInfo ci) {
        if (!BrassLinkBridgeManager.contains(actor))
            return;

        BrassLinkBridgeManager.remove(world, actor);
        ci.cancel();
    }

    @Inject(method = "updateNetworkOf", at = @At("HEAD"), cancellable = true)
    private void wca$updateNetworkOf(LevelAccessor world, IRedstoneLinkable actor, CallbackInfo ci) {
        if (!BrassLinkBridgeManager.contains(actor))
            return;

        WietseCreateAdditions.BRASS_LINK_NETWORK_HANDLER.updateNetworkOf(world, BrassLinkBridgeManager.get(actor));
        ci.cancel();
    }
}
