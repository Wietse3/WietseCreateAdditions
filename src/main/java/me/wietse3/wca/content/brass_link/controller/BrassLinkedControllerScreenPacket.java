package me.wietse3.wca.content.brass_link.controller;

import io.netty.buffer.ByteBuf;
import me.wietse3.wca.registry.WCADataComponents;
import me.wietse3.wca.registry.WCAPackets;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BrassLinkedControllerScreenPacket extends BrassLinkedControllerPacketBase {
    public static final StreamCodec<ByteBuf, BrassLinkedControllerScreenPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, p -> p.slot,
            CatnipStreamCodecBuilders.list(ByteBufCodecs.fromCodec(BrassControllerBind.CODEC)), p -> p.binds,
            BrassLinkedControllerScreenPacket::new
    );

    private final int slot;
    private final List<BrassControllerBind> binds;

    public BrassLinkedControllerScreenPacket(int slot, List<BrassControllerBind> binds) {
        super(null);
        this.slot = slot;
        this.binds = binds;
    }

    @Override
    protected void handleItem(ServerPlayer player, ItemStack heldItem) {
        heldItem.set(WCADataComponents.BRASS_LINKED_CONTROLLER_LINKS, binds);
    }

    @Override
    protected void handleLectern(ServerPlayer player, LecternBrassControllerBlockEntity lectern) {}

    @Override
    public PacketTypeProvider getTypeProvider() {
        return WCAPackets.BRASS_LINKED_CONTROLLER_SCREEN;
    }
}
