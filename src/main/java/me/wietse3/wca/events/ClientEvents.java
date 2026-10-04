package me.wietse3.wca.events;

import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.BrassLinkBehavior;
import me.wietse3.wca.content.brass_link.controller.BrassLinkedControllerClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    private static ClientLevel lastLevel;

    @SubscribeEvent
    public static void onTickPre(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != lastLevel) {
            BrassLinkBehavior.clearClientLinks();
            lastLevel = mc.level;
        }

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

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(ModelResourceLocation.standalone(WietseCreateAdditions.asResource("block/copycat_redstone_link/indicator")));
        event.register(ModelResourceLocation.standalone(WietseCreateAdditions.asResource("block/copycat_redstone_link/indicator_powered")));
        event.register(ModelResourceLocation.standalone(WietseCreateAdditions.asResource("block/copycat_redstone_link/indicator_receiver")));
        event.register(ModelResourceLocation.standalone(WietseCreateAdditions.asResource("block/copycat_redstone_link/indicator_receiver_powered")));
    }
}
