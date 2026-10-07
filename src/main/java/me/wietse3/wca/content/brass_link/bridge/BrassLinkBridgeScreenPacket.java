package me.wietse3.wca.content.brass_link.bridge;

import me.wietse3.wca.registry.WCAPackets;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class BrassLinkBridgeScreenPacket implements ServerboundPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, BrassLinkBridgeScreenPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.stringUtf8(64), packet -> packet.frequency,
                    BrassLinkBridgeScreenPacket::new
            );

    private final String frequency;

    public BrassLinkBridgeScreenPacket(String frequency) {
        this.frequency = frequency;
    }

    @Override
    public void handle(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BrassLinkBridgeItem))
            return;
        BrassLinkBridgeItem.setFrequency(stack, frequency);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return WCAPackets.BRASS_LINK_BRIDGE_SCREEN;
    }
}
