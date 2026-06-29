package me.wietse3.wca.events;

import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.controller.BrassLinkedControllerServerHandler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber
public class CommonEvents {

    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor world = event.getLevel();
        WietseCreateAdditions.BRASS_LINK_NETWORK_HANDLER.onLoadWorld(world);
    }

    @SubscribeEvent
    public static void onUnloadWorld(LevelEvent.Unload event) {
        LevelAccessor world = event.getLevel();
        WietseCreateAdditions.BRASS_LINK_NETWORK_HANDLER.onUnloadWorld(world);
    }

    @SubscribeEvent
    public static void onServerWorldTick(LevelTickEvent.Post event) {
        Level world = event.getLevel();
        if (world.isClientSide())
            return;

        BrassLinkedControllerServerHandler.tick(world);
    }
}
