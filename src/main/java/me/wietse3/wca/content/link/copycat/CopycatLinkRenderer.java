package me.wietse3.wca.content.link.copycat;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.redstone.link.LinkRenderer;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.BrassLinkBehavior;
import me.wietse3.wca.content.brass_link.copycat.CopycatBrassLinkBlockEntity;
import me.wietse3.wca.registry.WCABlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CopycatLinkRenderer<T extends SmartBlockEntity> extends SmartBlockEntityRenderer<T> {

    public CopycatLinkRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(T be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        Level level = be.getLevel();
        if (level == null)
            return;

        if (!(be instanceof ICopycatBlockEntity copycat))
            return;

        boolean isBrassLink = false;
        if (!copycat.hasCustomMaterial()) {
            if (be instanceof CopycatLinkBlockEntity) {
                LinkRenderer.renderOnBlockEntity(be, partialTicks, poseStack, buffer, light, overlay);
            } else if (be instanceof CopycatBrassLinkBlockEntity) {
                isBrassLink = true;

                BrassLinkBehavior behavior = be.getBehaviour(BrassLinkBehavior.TYPE);
                if (behavior != null) {
                    String frequency = behavior.getNetworkKey();
                    if (!frequency.isEmpty()) {
                        renderNameplateOnHover(be, Component.literal(frequency), 0.5f, poseStack, buffer, light);
                    }
                }
            }
        }

        BlockState state = be.getBlockState();
        Direction facing = state.getValue(DirectionalBlock.FACING);
        BakedModel model;

        poseStack.pushPose();
        if (copycat.hasCustomMaterial()) {
            model = getIndicatorModel(state);

            PoseTransformStack transform = TransformStack.of(poseStack).center();
            switch (facing) {
                case DOWN -> transform.rotateXDegrees(180);
                case NORTH -> transform.rotateXDegrees(-90);
                case SOUTH -> transform.rotateXDegrees(90);
                case WEST -> transform.rotateZDegrees(90);
                case EAST -> transform.rotateZDegrees(-90);
            }
            transform.uncenter();
        } else {
            model = getRedstoneLinkModel(state, isBrassLink);
        }

        Minecraft.getInstance()
                .getBlockRenderer()
                .getModelRenderer()
                .tesselateBlock(
                        level,
                        model,
                        state,
                        be.getBlockPos(),
                        poseStack,
                        buffer.getBuffer(RenderType.cutout()),
                        true,
                        RandomSource.create(state.getSeed(be.getBlockPos())),
                        state.getSeed(be.getBlockPos()),
                        overlay,
                        ModelData.EMPTY,
                        RenderType.cutout()
                );

        poseStack.popPose();
    }

    private BakedModel getIndicatorModel(BlockState state) {
        boolean powered = state.getValue(BlockStateProperties.POWERED);

        String variant;
        if (state.getValue(RedstoneLinkBlock.RECEIVER))
            variant = powered ? "indicator_receiver_powered" : "indicator_receiver";
        else
            variant = powered ? "indicator_powered" : "indicator";

        return Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(
                WietseCreateAdditions.asResource("block/copycat_redstone_link/" + variant)));
    }

    private BakedModel getRedstoneLinkModel(BlockState state, boolean brass) {
        BlockState modelState = (brass ? WCABlocks.BRASS_REDSTONE_LINK : AllBlocks.REDSTONE_LINK)
                .get().withPropertiesOf(state);

        return Minecraft.getInstance().getBlockRenderer().getBlockModel(modelState);
    }
}
