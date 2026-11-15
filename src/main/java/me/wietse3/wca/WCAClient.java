package me.wietse3.wca;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = WietseCreateAdditions.MODID, dist = Dist.CLIENT)
public class WCAClient {
    public WCAClient(ModContainer modContainer) {
        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                (container, parent) -> new BaseConfigScreen(parent, container.getModId())
        );
    }
}
