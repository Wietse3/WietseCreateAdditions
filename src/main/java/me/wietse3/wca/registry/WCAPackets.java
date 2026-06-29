package me.wietse3.wca.registry;

import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.BrassLinkConfigurePacket;
import me.wietse3.wca.content.brass_link.controller.BrassLinkedControllerBindPacket;
import me.wietse3.wca.content.brass_link.controller.BrassLinkedControllerInputPacket;
import me.wietse3.wca.content.brass_link.controller.BrassLinkedControllerScreenPacket;
import me.wietse3.wca.content.brass_link.controller.BrassLinkedControllerStopLecternPacket;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

public enum WCAPackets implements BasePacketPayload.PacketTypeProvider {
    // Client to Server
    BRASS_REDSTONE_LINK_CONFIGURE(BrassLinkConfigurePacket.class, BrassLinkConfigurePacket.CODEC),
    BRASS_LINKED_CONTROLLER_INPUT(BrassLinkedControllerInputPacket.class, BrassLinkedControllerInputPacket.STREAM_CODEC),
    BRASS_LINKED_CONTROLLER_BIND(BrassLinkedControllerBindPacket.class, BrassLinkedControllerBindPacket.STREAM_CODEC),
    BRASS_LINKED_CONTROLLER_STOP_LECTERN(BrassLinkedControllerStopLecternPacket.class, BrassLinkedControllerStopLecternPacket.STREAM_CODEC),
    BRASS_LINKED_CONTROLLER_SCREEN(BrassLinkedControllerScreenPacket .class, BrassLinkedControllerScreenPacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> WCAPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(WietseCreateAdditions.asResource(name)),
                clazz, codec
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(WietseCreateAdditions.MODID, 1);
        for (WCAPackets packet : WCAPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}
