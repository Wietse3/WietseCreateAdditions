package me.wietse3.wca;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = WietseCreateAdditions.MODID, dist = Dist.CLIENT)
public class WietseCreateAdditionsClient {
    public WietseCreateAdditionsClient(IEventBus modEventBus) {
        modEventBus.addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        WietseCreateAdditions.LOGGER.info("HELLO FROM CLIENT SETUP");
    }
}
