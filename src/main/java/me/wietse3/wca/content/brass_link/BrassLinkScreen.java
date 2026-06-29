package me.wietse3.wca.content.brass_link;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import me.wietse3.wca.registry.WCABlocks;
import me.wietse3.wca.registry.WCAGuiTextures;
import me.wietse3.wca.registry.WCALang;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class BrassLinkScreen extends AbstractSimiScreen {

    private final ItemStack renderedItem = WCABlocks.BRASS_REDSTONE_LINK.asStack();
    private final WCAGuiTextures background = WCAGuiTextures.BRASS_REDSTONE_LINK;
    private BrassRedstoneLinkBlockEntity be;

    private EditBox frequencyBox;
    private IconButton confirmButton;

    private IconButton invertButton;
    private boolean inverted;

    public BrassLinkScreen(BrassRedstoneLinkBlockEntity be) {
        super(be.getBlockState().getBlock().getName());
        this.be = be;
    }

    @Override
    protected void init() {
        setWindowSize(background.getWidth(), background.getHeight());
        setWindowOffset(-20, 0);
        super.init();

        int x = guiLeft;
        int y = guiTop;

        BrassLinkBehavior behavior = be.getBehaviour(BrassLinkBehavior.TYPE);

        frequencyBox = new EditBox(font, x + 20, y + 32, 132, 16, Component.empty());
        frequencyBox.setTextColor(0xFFFFFF);
        frequencyBox.setBordered(false);
        frequencyBox.setMaxLength(25);
        frequencyBox.setValue(behavior.frequency);
        addRenderableWidget(frequencyBox);
        setFocused(frequencyBox);

        confirmButton = new IconButton(x + background.getWidth() - 33, y + background.getHeight() - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onClose);
        addRenderableWidget(confirmButton);

        int optionX = x + 7;

        if (behavior.isListening()) {
            invertButton = new IconButton(optionX , y + background.getHeight() - 24, AllIcons.I_FLIP);
            invertButton.setToolTip(WCALang.translate("gui.brass_redstone_link.invert").component());
            invertButton.withCallback(() -> setInverted(!inverted));
            setInverted(behavior.inverted);
            addRenderableWidget(invertButton);
        }
    }

    private void setInverted(boolean pInverted) {
        inverted = pInverted;
        invertButton.green = inverted;
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, x, y);
        graphics.drawString(font, title, x + (background.getWidth() - 8) / 2 - font.width(title) / 2, y + 4, 0x592424, false);

        GuiGameElement.of(renderedItem).<GuiGameElement
                        .GuiRenderBuilder>at(x + background.getWidth() + 6, y + background.getHeight() - 56, 100)
                .scale(5)
                .render(graphics);
    }

    @Override
    public void onClose() {
        super.onClose();

        CatnipServices.NETWORK.sendToServer(new BrassLinkConfigurePacket(be.getBlockPos(), frequencyBox.getValue(), inverted));
    }
}
