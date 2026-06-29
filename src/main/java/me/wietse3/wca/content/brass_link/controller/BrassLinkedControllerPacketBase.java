package me.wietse3.wca.content.brass_link.controller;

import me.wietse3.wca.registry.WCAItems;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class BrassLinkedControllerPacketBase implements ServerboundPacketPayload {
    @Nullable
    private final BlockPos lecternPos;

    public BrassLinkedControllerPacketBase(@Nullable BlockPos lecternPos) {
        this.lecternPos = lecternPos;
    }

    @Nullable
    public BlockPos getLecternPos() {
        return lecternPos;
    }

    @Override
    public void handle(ServerPlayer player) {
        if (this.lecternPos != null) {
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
    }

    protected abstract void handleItem(ServerPlayer player, ItemStack heldItem);
    protected abstract void handleLectern(ServerPlayer player, LecternBrassControllerBlockEntity lectern);
}
