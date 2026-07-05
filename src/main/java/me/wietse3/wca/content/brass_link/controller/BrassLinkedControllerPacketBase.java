package me.wietse3.wca.content.brass_link.controller;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import me.wietse3.wca.registry.WCAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public abstract class BrassLinkedControllerPacketBase extends SimplePacketBase {

    private BlockPos lecternPos;

    public BrassLinkedControllerPacketBase(BlockPos lecternPos) {
        this.lecternPos = lecternPos;
    }

    public BrassLinkedControllerPacketBase(FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            lecternPos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
        }
    }

    protected boolean inLectern() {
        return lecternPos != null;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(inLectern());
        if (inLectern()) {
            buffer.writeInt(lecternPos.getX());
            buffer.writeInt(lecternPos.getY());
            buffer.writeInt(lecternPos.getZ());
        }
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null)
                return;

            if (inLectern()) {
                BlockEntity be = player.level().getBlockEntity(lecternPos);
                if (!(be instanceof LecternBrassControllerBlockEntity))
                    return;
                handleLectern(player, (LecternBrassControllerBlockEntity) be);
            } else {
                ItemStack controller = player.getMainHandItem();
                if (!WCAItems.BRASS_LINKED_CONTROLLER.isIn(controller)) {
                    controller = player.getOffhandItem();
                    if (!WCAItems.BRASS_LINKED_CONTROLLER.isIn(controller))
                        return;
                }
                handleItem(player, controller);
            }
        });
        return true;
    }

    protected abstract void handleItem(ServerPlayer player, ItemStack heldItem);
    protected abstract void handleLectern(ServerPlayer player, LecternBrassControllerBlockEntity lectern);
}
