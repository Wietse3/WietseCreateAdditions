package me.wietse3.wca.content.brass_link.controller;

import io.netty.buffer.ByteBuf;
import me.wietse3.wca.registry.WCAPackets;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BrassLinkedControllerInputPacket extends BrassLinkedControllerPacketBase {
    public static final StreamCodec<ByteBuf, BrassLinkedControllerInputPacket> STREAM_CODEC = StreamCodec.composite(
            CatnipStreamCodecBuilders.list(ByteBufCodecs.INT), p -> p.activatedButtons,
            ByteBufCodecs.BOOL, p -> p.press,
            CatnipStreamCodecs.NULLABLE_BLOCK_POS, BrassLinkedControllerPacketBase::getLecternPos,
            BrassLinkedControllerInputPacket::new
    );

    private final List<Integer> activatedButtons;
    private final boolean press;

    public BrassLinkedControllerInputPacket(Collection<Integer> activatedButtons, boolean press) {
        this(activatedButtons, press, null);
    }

    public BrassLinkedControllerInputPacket(Collection<Integer> activatedButtons, boolean press, BlockPos lecternPos) {
        super(lecternPos);
        this.activatedButtons = List.copyOf(activatedButtons);
        this.press = press;
    }

    @Override
    protected void handleLectern(ServerPlayer player, LecternBrassControllerBlockEntity lectern) {
        if (lectern.isUsedBy(player))
            handleItem(player, lectern.getController());
    }

    @Override
    protected void handleItem(ServerPlayer player, ItemStack heldItem) {
        Level world = player.getCommandSenderWorld();
        UUID uniqueID = player.getUUID();
        BlockPos pos = player.blockPosition();

        if (player.isSpectator() && press)
            return;

        BrassLinkedControllerServerHandler.receivePressed(world, pos, uniqueID, activatedButtons.stream()
                .map(i -> BrassLinkedControllerItem.toFrequency(heldItem, i))
                .collect(Collectors.toList()), press);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return WCAPackets.BRASS_LINKED_CONTROLLER_INPUT;
    }
}
