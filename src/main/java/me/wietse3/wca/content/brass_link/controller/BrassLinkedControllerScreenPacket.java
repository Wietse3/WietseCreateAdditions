package me.wietse3.wca.content.brass_link.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BrassLinkedControllerScreenPacket extends BrassLinkedControllerPacketBase {

    private final List<BrassControllerBind> binds;

    public BrassLinkedControllerScreenPacket(List<BrassControllerBind> binds) {
        super((BlockPos) null);
        this.binds = List.copyOf(binds);
    }

    public BrassLinkedControllerScreenPacket(FriendlyByteBuf buffer) {
        super(buffer);
        binds = new ArrayList<>();
        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++)
            binds.add(new BrassControllerBind(buffer.readUtf(), buffer.readInt()));
        }

    @Override
    public void write(FriendlyByteBuf buffer) {
        super.write(buffer);
        buffer.writeVarInt(binds.size());
        binds.forEach(bind -> {
            buffer.writeUtf(bind.frequency());
            buffer.writeInt(bind.power());
        });
    }

    @Override
    protected void handleItem(ServerPlayer player, ItemStack heldItem) {
        heldItem.getOrCreateTag().put("BrassLinkedControllerLinks", BrassControllerBind.CODEC.listOf()
                .encodeStart(NbtOps.INSTANCE, binds).result().orElse(new ListTag()));
    }

    @Override
    protected void handleLectern(ServerPlayer player, LecternBrassControllerBlockEntity lectern) {}

}
