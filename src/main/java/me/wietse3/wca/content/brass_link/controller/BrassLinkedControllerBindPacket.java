package me.wietse3.wca.content.brass_link.controller;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import io.netty.buffer.ByteBuf;
import me.wietse3.wca.registry.WCADataComponents;
import me.wietse3.wca.registry.WCAPackets;
import me.wietse3.wca.content.brass_link.BrassLinkBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BrassLinkedControllerBindPacket extends BrassLinkedControllerPacketBase {
    public static final StreamCodec<ByteBuf, BrassLinkedControllerBindPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, p -> p.button,
            BlockPos.STREAM_CODEC, p -> p.linkLocation,
            BrassLinkedControllerBindPacket::new
    );

    private final int button;
    private final BlockPos linkLocation;

    public BrassLinkedControllerBindPacket(int button, BlockPos linkLocation) {
        super(null);
        this.button = button;
        this.linkLocation = linkLocation;
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
        heldItem.set(WCADataComponents.BRASS_LINKED_CONTROLLER_LINKS, binds);
    }

    @Override
    protected void handleLectern(ServerPlayer player, LecternBrassControllerBlockEntity lectern) {}

    @Override
    public PacketTypeProvider getTypeProvider() {
        return WCAPackets.BRASS_LINKED_CONTROLLER_BIND;
    }
}
