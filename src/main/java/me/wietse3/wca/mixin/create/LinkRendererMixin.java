package me.wietse3.wca.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.LinkRenderer;
import com.simibubi.create.foundation.utility.CreateLang;
import me.wietse3.wca.content.brass_link.bridge.BrassLinkBridge;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LinkRenderer.class)
public class LinkRendererMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/ValueSettingsClient;showHoverTip(Ljava/util/List;)V"))
    private static void wca$bridgeTip(
            CallbackInfo ci,
            @Local(name = "tip") List<MutableComponent> tip,
            @Local(name = "behaviour") LinkBehaviour link
    ) {
        String frequency = BrassLinkBridge.getFrequencyFrom(link);
        if (frequency.isBlank())
            return;

        tip.add(CreateLang.text("-> ").style(ChatFormatting.GRAY)
                .add(CreateLang.text(frequency).style(ChatFormatting.GOLD))
                .component());
    }
}
