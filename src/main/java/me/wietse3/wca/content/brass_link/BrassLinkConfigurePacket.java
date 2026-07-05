package me.wietse3.wca.content.brass_link;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class BrassLinkConfigurePacket extends BlockEntityConfigurationPacket<BrassRedstoneLinkBlockEntity> {

    private String frequency;
    private boolean inverted;

    public BrassLinkConfigurePacket(BlockPos pos, String frequency, boolean inverted) {
        super(pos);
        this.frequency = frequency.strip();
        this.inverted = inverted;
    }

    public BrassLinkConfigurePacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        frequency = buffer.readUtf();
        inverted = buffer.readBoolean();
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeUtf(frequency);
        buffer.writeBoolean(inverted);
    }

    @Override
    protected void applySettings(BrassRedstoneLinkBlockEntity be) {
        BrassLinkBehavior behaviour = be.getBehaviour(BrassLinkBehavior.TYPE);
        if (behaviour == null)
            return;

        behaviour.setFrequency(frequency);
        behaviour.setInverted(inverted);
    }

}
