package me.wietse3.wca.content.brass_link.controller;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.controller.BrassLinkedControllerClientHandler.Mode;
import me.wietse3.wca.registry.WCAItems;
import me.wietse3.wca.util.ExtendedControlsUtil;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BrassLinkedControllerItemRenderer extends CustomRenderedItemModelRenderer {

    protected static final PartialModel POWERED = PartialModel.of(WietseCreateAdditions.asResource("item/brass_linked_controller/powered"));
    protected static final PartialModel BUTTON = PartialModel.of(WietseCreateAdditions.asResource("item/brass_linked_controller/button"));

    static LerpedFloat equipProgress;
    static List<LerpedFloat> buttons;

    static {
        int controls = ExtendedControlsUtil.getExtendedControls().size();
        equipProgress = LerpedFloat.linear()
                .startWithValue(0);
        buttons = new ArrayList<>(controls);
        for (int i = 0; i < controls; i++)
            buttons.add(LerpedFloat.linear()
                    .startWithValue(0));
    }

    static void tick() {
        if (Minecraft.getInstance()
                .isPaused())
            return;

        boolean active = BrassLinkedControllerClientHandler.MODE != Mode.IDLE;
        equipProgress.chase(active ? 1 : 0, .2f, LerpedFloat.Chaser.EXP);
        equipProgress.tickChaser();

        if (!active)
            return;

        for (int i = 0; i < buttons.size(); i++) {
            LerpedFloat lerpedFloat = buttons.get(i);
            lerpedFloat.chase(BrassLinkedControllerClientHandler.currentlyPressed.contains(i) ? 1 : 0, .4f, LerpedFloat.Chaser.EXP);
            lerpedFloat.tickChaser();
        }
    }

    static void resetButtons() {
        for (LerpedFloat button : buttons) {
            button.startWithValue(0);
        }
    }

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer,
                          ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light,
                          int overlay) {
        renderNormal(stack, model, renderer, transformType, ms, light);
    }

    protected static void renderNormal(ItemStack stack, CustomRenderedItemModel model,
                                       PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms,
                                       int light) {
        render(stack, model, renderer, transformType, ms, light, RenderType.NORMAL, false, false);
    }

    public static void renderInLectern(ItemStack stack, CustomRenderedItemModel model,
                                       PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms,
                                       int light, boolean active, boolean renderDepression) {
        render(stack, model, renderer, transformType, ms, light, RenderType.LECTERN, active, renderDepression);
    }

    protected static void render(ItemStack stack, CustomRenderedItemModel model,
                                 PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms,
                                 int light, RenderType renderType, boolean active, boolean renderDepression) {
        float pt = AnimationTickHolder.getPartialTicks();
        var msr = TransformStack.of(ms);

        ms.pushPose();

        if (renderType == RenderType.NORMAL) {
            Minecraft mc = Minecraft.getInstance();
            boolean rightHanded = mc.options.mainHand().get() == HumanoidArm.RIGHT;
            ItemDisplayContext mainHand =
                    rightHanded ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
            ItemDisplayContext offHand =
                    rightHanded ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;

            active = false;
            boolean noControllerInMain = !WCAItems.BRASS_LINKED_CONTROLLER.isIn(mc.player.getMainHandItem());

            if (transformType == mainHand || (transformType == offHand && noControllerInMain)) {
                float equip = equipProgress.getValue(pt);
                int handModifier = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1 : 1;
                msr.translate(0, equip / 4, equip / 4 * handModifier);
                msr.rotateYDegrees(equip * -30 * handModifier);
                msr.rotateZDegrees(equip * -30);
                active = true;
            }

            if (transformType == ItemDisplayContext.GUI) {
                if (stack == mc.player.getMainHandItem())
                    active = true;
                if (stack == mc.player.getOffhandItem() && noControllerInMain)
                    active = true;
            }

            active &= BrassLinkedControllerClientHandler.MODE != Mode.IDLE;

            renderDepression = true;
        }

        renderer.render(active ? POWERED.get() : model.getOriginalModel(), light);

        if (!active) {
            ms.popPose();
            return;
        }

        BakedModel button = BUTTON.get();
        float s = 1 / 16f;
        float b = s * -.75f;
        int index = 0;

        if (renderType == RenderType.NORMAL) {
            if (BrassLinkedControllerClientHandler.MODE == Mode.BIND) {
                int i = (int) Mth.lerp((Mth.sin(AnimationTickHolder.getRenderTime() / 4f) + 1) / 2, 5, 15);
                light = i << 20;
            }
        }

        ms.pushPose();
        msr.translate(2 * s, 0, 8 * s);
        renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
        msr.translate(4 * s, 0, 0);
        renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
        msr.translate(-2 * s, 0, 2 * s);
        renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
        msr.translate(0, 0, -4 * s);
        renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
        ms.popPose();

        msr.translate(2 * s, 0, 3 * s);
        renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
        msr.translate(2 * s, 0, 0);
        renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
        msr.translate(2 * s, 0, 0);
        renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);

        ms.popPose();
    }

    protected static void renderButton(PartialItemModelRenderer renderer, PoseStack ms, int light, float pt, BakedModel button,
                                       float b, int index, boolean renderDepression) {
        ms.pushPose();
        if (renderDepression) {
            float depression = b * buttons.get(index).getValue(pt);
            ms.translate(0, depression, 0);
        }
        renderer.renderSolid(button, light);
        ms.popPose();
    }

    protected enum RenderType {
        NORMAL, LECTERN;
    }

}
