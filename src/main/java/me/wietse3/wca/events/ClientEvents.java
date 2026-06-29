package me.wietse3.wca.events;

import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.controller.BrassLinkedControllerClientHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onTickPre(ClientTickEvent.Pre event) {
        BrassLinkedControllerClientHandler.tick();
    }

    @SubscribeEvent
    public static void onTickPost(ClientTickEvent.Post event) {}

    @SubscribeEvent
    public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null)
            return;

        if (!event.isUseItem())
            return;

        BrassLinkedControllerClientHandler.deactivateInLectern();
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, WietseCreateAdditions.asResource("brass_linked_controller"), BrassLinkedControllerClientHandler.OVERLAY);
    }
}
