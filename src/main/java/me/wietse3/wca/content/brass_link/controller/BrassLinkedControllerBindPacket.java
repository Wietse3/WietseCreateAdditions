package me.wietse3.wca.content.brass_link.controller;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import me.wietse3.wca.content.brass_link.BrassLinkBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BrassLinkedControllerBindPacket extends BrassLinkedControllerPacketBase {

    private final int button;
    private final BlockPos linkLocation;

    public BrassLinkedControllerBindPacket(int button, BlockPos linkLocation) {
        super((BlockPos) null);
        this.button = button;
        this.linkLocation = linkLocation;
    }

    public BrassLinkedControllerBindPacket(FriendlyByteBuf buffer) {
        super(buffer);
        this.button = buffer.readVarInt();
        this.linkLocation = buffer.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        super.write(buffer);
        buffer.writeVarInt(button);
        buffer.writeBlockPos(linkLocation);
    }

    @Override
    protected void handleItem(ServerPlayer player, ItemStack heldItem) {
        if (player.isSpectator())
            return;

        List<BrassControllerBind> binds = BrassLinkedControllerItem.getFrequencies(heldItem);
        BrassLinkBehavior link = BlockEntityBehaviour.get(player.level(), linkLocation, BrassLinkBehavior.TYPE);
        if (link == null)
            return;

        binds.set(button, new BrassControllerBind(link.getNetworkKey()));
        heldItem.getOrCreateTag().put("BrassLinkedControllerLinks", BrassControllerBind.CODEC.listOf()
                .encodeStart(NbtOps.INSTANCE, binds).result().orElse(new ListTag()));
    }

    @Override
    protected void handleLectern(ServerPlayer player, LecternBrassControllerBlockEntity lectern) {}

}
