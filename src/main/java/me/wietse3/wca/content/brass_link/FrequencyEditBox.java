package me.wietse3.wca.content.brass_link;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.AddressEditBox;
import com.simibubi.create.content.trains.schedule.DestinationSuggestions;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;

public class FrequencyEditBox extends EditBox {

    private DestinationSuggestions destinationSuggestions;
    private Consumer<String> mainResponder;

    public FrequencyEditBox(Screen screen, Font font, int x, int y, int width, int height,
                            boolean anchorToBottom, BlockPos pos) {
        this(screen, font, x, y, width, height, anchorToBottom,
                BrassLinkBehavior.createSuggestions(Minecraft.getInstance().level, pos));
    }

    public FrequencyEditBox(Screen screen, Font font, int x, int y, int width, int height,
                            boolean anchorToBottom, List<IntAttached<String>> options) {
        super(font, x, y, width, height, Component.empty());

        destinationSuggestions = new DestinationSuggestions(Minecraft.getInstance(), screen, this, font, options,
                anchorToBottom, -72 + getY() + (anchorToBottom ? 0 : getHeight()));
        destinationSuggestions.setAllowSuggestions(true);
        destinationSuggestions.updateCommandInfo();

        mainResponder = t -> destinationSuggestions.updateCommandInfo();
        setResponder(mainResponder);
        setBordered(false);
        mouseClicked(0, 0, 0);
        setMaxLength(25);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (destinationSuggestions.keyPressed(keyCode, scanCode, modifiers))
            return true;
        if (isFocused() && keyCode == GLFW.GLFW_KEY_ENTER) {
            setFocused(false);
            moveCursorToEnd(false);
            mouseClicked(0, 0, 0);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (destinationSuggestions.mouseScrolled(Mth.clamp(scrollY, -1.0D, 1.0D)))
            return true;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (isMouseOver(mouseX, mouseY)) {
                setValue("");
                return true;
            }
        }

        boolean wasFocused = isFocused();
        if (super.mouseClicked(mouseX, mouseY, button)) {
            if (!wasFocused) {
                setHighlightPos(0);
                setCursorPosition(getValue().length());
            }
            return true;
        }
        if (destinationSuggestions.mouseClicked(mouseX, mouseY, button))
            return true;
        return false;
    }

    @Override
    public void setValue(String text) {
        setHighlightPos(0);
        super.setValue(text);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        PoseStack matrixStack = guiGraphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(0, 0, 500);
        destinationSuggestions.render(guiGraphics, mouseX, mouseY);
        matrixStack.popPose();
    }

    @Override
    public void setResponder(Consumer<String> responder) {
        super.setResponder(responder == mainResponder ? mainResponder : mainResponder.andThen(responder));
    }

    public void tick() {
        if (!isFocused())
            destinationSuggestions.hide();
        if (isFocused() && destinationSuggestions.suggestions == null)
            destinationSuggestions.updateCommandInfo();
        destinationSuggestions.tick();
    }
}
