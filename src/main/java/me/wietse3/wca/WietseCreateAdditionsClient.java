package me.wietse3.wca;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = WietseCreateAdditions.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WietseCreateAdditionsClient {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        WietseCreateAdditions.LOGGER.info("HELLO FROM CLIENT SETUP");
    }
}
