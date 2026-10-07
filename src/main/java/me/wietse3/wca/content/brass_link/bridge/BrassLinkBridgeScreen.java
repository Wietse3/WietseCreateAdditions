package me.wietse3.wca.content.brass_link.bridge;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import me.wietse3.wca.content.brass_link.FrequencyEditBox;
import me.wietse3.wca.registry.WCAGuiTextures;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BrassLinkBridgeScreen extends AbstractSimiScreen {

    private final WCAGuiTextures background = WCAGuiTextures.BRASS_REDSTONE_LINK;
    private final ItemStack stack;

    private FrequencyEditBox frequencyBox;

    public BrassLinkBridgeScreen(ItemStack stack) {
        super(stack.getHoverName());
        this.stack = stack;
    }

    @Override
    protected void init() {
        setWindowSize(background.getWidth(), background.getHeight());
        setWindowOffset(-20, 0);
        super.init();

        int x = guiLeft;
        int y = guiTop;

        frequencyBox = new FrequencyEditBox(this, font, x + 20, y + 32, 132, 14,
                false, List.of());
        frequencyBox.setTextColor(0xFFFFFF);
        frequencyBox.setValue(BrassLinkBridgeItem.getFrequency(stack));
        addRenderableWidget(frequencyBox);
        setFocused(frequencyBox);

        IconButton confirmButton = new IconButton(x + background.getWidth() - 33, y + background.getHeight() - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onClose);
        addRenderableWidget(confirmButton);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, x, y);
        graphics.drawString(font, title, x + (background.getWidth() - 8) / 2 - font.width(title) / 2, y + 4, 0x592424, false);

        GuiGameElement.of(stack).<GuiGameElement
                        .GuiRenderBuilder>at(x + background.getWidth() + 8, y + background.getHeight() - 52, -200)
                .scale(4)
                .render(graphics);
    }

    @Override
    public void onClose() {
        super.onClose();

        CatnipServices.NETWORK.sendToServer(new BrassLinkBridgeScreenPacket(frequencyBox.getValue()));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (frequencyBox.mouseScrolled(mouseX, mouseY, scrollX, scrollY))
            return true;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void tick() {
        super.tick();
        frequencyBox.tick();
    }
}
