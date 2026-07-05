package me.wietse3.wca.content.brass_link.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class BrassLinkedControllerStopLecternPacket extends BrassLinkedControllerPacketBase {

    public BrassLinkedControllerStopLecternPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public BrassLinkedControllerStopLecternPacket(BlockPos lecternPos) {
        super(lecternPos);
    }

    @Override
    protected void handleLectern(ServerPlayer player, LecternBrassControllerBlockEntity lectern) {
        lectern.tryStopUsing(player);
    }

    @Override
    protected void handleItem(ServerPlayer player, ItemStack heldItem) { }

}
