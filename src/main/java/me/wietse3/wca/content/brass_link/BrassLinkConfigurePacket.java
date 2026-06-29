package me.wietse3.wca.content.brass_link;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import me.wietse3.wca.registry.WCAPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class BrassLinkConfigurePacket extends BlockEntityConfigurationPacket<BrassRedstoneLinkBlockEntity> {

    public static final StreamCodec<FriendlyByteBuf, BrassLinkConfigurePacket> CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, packet -> packet.pos,
                    ByteBufCodecs.stringUtf8(64), packet -> packet.frequency,
                    ByteBufCodecs.BOOL, packet -> packet.inverted,
                    BrassLinkConfigurePacket::new
            );

    private final String frequency;
    private final boolean inverted;

    public BrassLinkConfigurePacket(BlockPos pos, String frequency, boolean inverted) {
        super(pos);
        this.frequency = frequency.strip();
        this.inverted = inverted;
    }

    @Override
    protected void applySettings(ServerPlayer player, BrassRedstoneLinkBlockEntity be) {
        BrassLinkBehavior behaviour = be.getBehaviour(BrassLinkBehavior.TYPE);
        if (behaviour == null)
            return;

        if (!player.mayBuild())
            return;

        behaviour.setFrequency(frequency);
        behaviour.setInverted(inverted);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return WCAPackets.BRASS_REDSTONE_LINK_CONFIGURE;
    }
}
