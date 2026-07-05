package me.wietse3.wca.content.brass_link;

import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import me.wietse3.wca.WietseCreateAdditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class BrassLinkBehavior extends BlockEntityBehaviour implements IBrassLinkable, ClipboardCloneable {

    public static final BehaviourType<BrassLinkBehavior> TYPE = new BehaviourType<>();

    enum Mode {
        TRANSMIT, RECEIVE
    }

    String frequency;
    boolean inverted;

    public boolean newPosition;
    private Mode mode;
    private IntSupplier transmission;
    private IntConsumer signalCallback;

    protected BrassLinkBehavior(SmartBlockEntity be) {
        super(be);
        frequency = "";
        inverted = false;
        newPosition = true;
    }

    public static BrassLinkBehavior receiver(SmartBlockEntity be, IntConsumer signalCallback) {
        BrassLinkBehavior behavior = new BrassLinkBehavior(be);
        behavior.signalCallback = signalCallback;
        behavior.mode = Mode.RECEIVE;
        return behavior;
    }

    public static BrassLinkBehavior transmitter(SmartBlockEntity be, IntSupplier transmission) {
        BrassLinkBehavior behavior = new BrassLinkBehavior(be);
        behavior.transmission = transmission;
        behavior.mode = Mode.TRANSMIT;
        return behavior;
    }

    public void copyFrom(BrassLinkBehavior behavior) {
        if (behavior == null)
            return;
        frequency = behavior.frequency;
        inverted = behavior.inverted;
    }

    @Override
    public boolean isListening() {
        return mode == Mode.RECEIVE;
    }

    @Override
    public int getTransmittedStrength() {
        return mode == Mode.TRANSMIT ? transmission.getAsInt() : 0;
    }

    @Override
    public void setReceivedStrength(int networkPower) {
        if (!newPosition)
            return;
        if (inverted)
            networkPower = 15 - networkPower;
        signalCallback.accept(networkPower);
    }

    public void notifySignalChange() {
        getHandler().updateNetworkOf(getWorld(), this);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (getWorld().isClientSide)
            return;
        getHandler().addToNetwork(getWorld(), this);
        newPosition = true;
    }

    @Override
    public String getNetworkKey() {
        return frequency;
    }

    @Override
    public void unload() {
        super.unload();
        if (getWorld().isClientSide)
            return;
        getHandler().removeFromNetwork(getWorld(), this);
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        nbt.putString("Frequency", frequency);
        nbt.putBoolean("Inverted", inverted);
        nbt.putLong("LastKnownPosition", blockEntity.getBlockPos().asLong());
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        long positionInTag = blockEntity.getBlockPos()
                .asLong();
        long positionKey = nbt.getLong("LastKnownPosition");
        newPosition = positionInTag != positionKey;

        super.read(nbt, clientPacket);
        frequency = nbt.getString("Frequency");
        inverted = nbt.getBoolean("Inverted");
    }

    public void setFrequency(String freq) {
        boolean changed = !frequency.equals(freq);

        if (changed)
            getHandler().removeFromNetwork(getWorld(), this);

        frequency = freq;

        if (!changed)
            return;

        blockEntity.sendData();
        getHandler().addToNetwork(getWorld(), this);
    }

    public void setInverted(boolean pInverted) {
        inverted = pInverted;
        blockEntity.sendData();
        notifySignalChange();
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    private BrassLinkNetworkHandler getHandler() {
        return WietseCreateAdditions.BRASS_LINK_NETWORK_HANDLER;
    }

    @Override
    public boolean isAlive() {
        Level level = getWorld();
        BlockPos pos = getPos();
        if (blockEntity.isChunkUnloaded())
            return false;
        if (blockEntity.isRemoved())
            return false;
        if (!level.isLoaded(pos))
            return false;
        return level.getBlockEntity(pos) == blockEntity;
    }

    @Override
    public BlockPos getLocation() {
        return getPos();
    }

    @Override
    public String getClipboardKey() {
        return "BrassLink";
    }

    @Override
    public boolean writeToClipboard(CompoundTag tag, Direction side) {
        tag.putString("Frequency", frequency);
        return true;
    }

    @Override
    public boolean readFromClipboard(CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (simulate)
            return true;
        if (!tag.contains("Frequency"))
            return false;
        setFrequency(tag.getString("Frequency"));
        return true;
    }

}
