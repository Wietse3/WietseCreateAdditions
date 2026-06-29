package me.wietse3.wca.content.brass_link.controller;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import me.wietse3.wca.registry.WCAGuiTextures;
import me.wietse3.wca.util.ExtendedControlsUtil;
import me.wietse3.wca.registry.WCALang;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BrassLinkedControllerScreen extends AbstractSimiScreen {

    private final WCAGuiTextures background = WCAGuiTextures.BRASS_LINKED_CONTROLLER;
    private final EditBox[] inputs = new EditBox[7];
    private final ScrollInput[] powers = new ScrollInput[7];
    private ItemStack heldItem;

    private IconButton resetButton;
    private IconButton confirmButton;

    public BrassLinkedControllerScreen(ItemStack heldItem) {
        super(heldItem.getHoverName());
        this.heldItem = heldItem;
    }

    @Override
    protected void init() {
        setWindowSize(background.getWidth(), background.getHeight());
        setWindowOffset(-20, 0);
        super.init();

        int x = guiLeft;
        int y = guiTop;

        resetButton = new IconButton(x + background.getWidth() - 62, y + background.getHeight() - 24, AllIcons.I_TRASH);
        resetButton.withCallback(() -> {
            for (int i = 0; i < inputs.length; i++) {
                inputs[i].setValue("");
                powers[i].setState(15);
            }
        });
        confirmButton = new IconButton(x + background.getWidth() - 33, y + background.getHeight() - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onClose);

        addRenderableWidget(resetButton);
        addRenderableWidget(confirmButton);

        List<BrassControllerBind> binds = BrassLinkedControllerItem.getFrequencies(heldItem);
        int inputY = 27;

        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = new EditBox(font, x + 28, y + inputY, 100, 16, Component.empty());
            inputs[i].setTextColor(0xFFFFFF);
            inputs[i].setBordered(false);
            inputs[i].setMaxLength(25);
            inputs[i].setValue(binds.get(i).frequency());
            addRenderableWidget(inputs[i]);

            powers[i] = new ScrollInput(x + 142, y + inputY - 6, 18, 18);
            powers[i].titled(WCALang.translate("gui.brass_linked_controller.power").component());
            powers[i].withRange(1, 16);
            powers[i].setState(binds.get(i).power());
            addRenderableWidget(powers[i]);

            inputY += 22;
            if (i == 3)
                inputY += 5;
        }
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, x, y);
        graphics.drawString(font, title, x + (background.getWidth() - 8) / 2 - font.width(title) / 2, y + 4, 0x592424, false);

        int inputY = 27;
        Component tooltip;

        for (int i = 0; i < inputs.length; i++) {
            graphics.drawCenteredString(font, String.valueOf(powers[i].getState()), x + 151, y + inputY, 0xFFFFFF);

            inputY += 22;
            if (i == 3)
                inputY += 5;

            if (inputs[i].isMouseOver(mouseX, mouseY)) {
                tooltip = WCALang.translate("gui.brass_linked_controller.key").add(
                        ExtendedControlsUtil.getExtendedControls()
                        .get(i)
                        .getTranslatedKeyMessage()
                ).component().withStyle(ChatFormatting.GOLD);
                graphics.renderTooltip(font, tooltip, mouseX, mouseY);
            }
        }

        GuiGameElement.of(heldItem).<GuiGameElement
                        .GuiRenderBuilder>at(x + background.getWidth() - 4, y + background.getHeight() - 56, -200)
                .scale(5)
                .render(graphics);
    }

    @Override
    public void onClose() {
        super.onClose();

        List<BrassControllerBind> binds = new ArrayList<>();
        for (int i = 0; i < inputs.length; i++) {
            binds.add(new BrassControllerBind(inputs[i].getValue(), powers[i].getState()));
        }

        int slot = minecraft.player.getInventory().selected;
        CatnipServices.NETWORK.sendToServer(new BrassLinkedControllerScreenPacket(slot, binds));

    }
}
