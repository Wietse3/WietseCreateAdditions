package me.wietse3.wca.content.brass_link.controller;

import io.netty.buffer.ByteBuf;
import me.wietse3.wca.registry.WCAPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class BrassLinkedControllerStopLecternPacket extends BrassLinkedControllerPacketBase {
    public static final StreamCodec<ByteBuf, BrassLinkedControllerStopLecternPacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(
            BrassLinkedControllerStopLecternPacket::new, BrassLinkedControllerPacketBase::getLecternPos
    );

    public BrassLinkedControllerStopLecternPacket(BlockPos lecternPos) {
        super(Objects.requireNonNull(lecternPos));
    }

    @Override
    protected void handleLectern(ServerPlayer player, LecternBrassControllerBlockEntity lectern) {
        lectern.tryStopUsing(player);
    }

    @Override
    protected void handleItem(ServerPlayer player, ItemStack heldItem) {}

    @Override
    public PacketTypeProvider getTypeProvider() {
        return WCAPackets.BRASS_LINKED_CONTROLLER_STOP_LECTERN;
    }
}
