package me.wietse3.wca.content.brass_link;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;

public class BrassRedstoneLinkRenderer extends SmartBlockEntityRenderer<BrassRedstoneLinkBlockEntity> {

    public BrassRedstoneLinkRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BrassRedstoneLinkBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
        String frequency = be.getBehaviour(BrassLinkBehavior.TYPE).getNetworkKey();

        if (!frequency.isEmpty()) {
            renderNameplateOnHover(be, Component.literal(frequency), 0.5f, ms, buffer, light);
        }
    }
}
