package me.wietse3.wca.content.brass_link;

import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import me.wietse3.wca.WietseCreateAdditions;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.*;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class BrassLinkBehavior extends BlockEntityBehaviour implements IBrassLinkable, ClipboardCloneable {

    public static final BehaviourType<BrassLinkBehavior> TYPE = new BehaviourType<>();

    private static final Set<BrassLinkBehavior> CLIENT_LINKS = new HashSet<>();

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
        if (getWorld().isClientSide) {
            CLIENT_LINKS.add(this);
            return;
        }
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
        if (getWorld().isClientSide) {
            CLIENT_LINKS.remove(this);
            return;
        }
        getHandler().removeFromNetwork(getWorld(), this);
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        nbt.putString("Frequency", frequency);
        nbt.putBoolean("Inverted", inverted);
        nbt.putLong("LastKnownPosition", blockEntity.getBlockPos().asLong());
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        long positionInTag = blockEntity.getBlockPos()
                .asLong();
        long positionKey = nbt.getLong("LastKnownPosition");
        newPosition = positionInTag != positionKey;

        super.read(nbt, registries, clientPacket);
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
    public boolean writeToClipboard(@NotNull HolderLookup.Provider registries, CompoundTag tag, Direction side) {
        tag.putString("Frequency", frequency);
        return true;
    }

    @Override
    public boolean readFromClipboard(@NotNull HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (simulate)
            return true;
        if (!tag.contains("Frequency"))
            return false;
        setFrequency(tag.getString("Frequency"));
        return true;
    }

    public static List<IntAttached<String>> createSuggestions(Level level, BlockPos blockPos) {
        List<IntAttached<String>> frequencies = new ArrayList<>();

        Vector3d pos = JOMLConversion.atCenterOf(blockPos);
        SubLevelAccess playerSublevel = SableCompanion.INSTANCE.getContaining(level, pos);
        if (playerSublevel != null)
            playerSublevel.logicalPose().transformPosition(pos);

        int linkRange = AllConfigs.server().logistics.linkRange.get();
        Map<String, Integer> nearest = new HashMap<>();

        for (BrassLinkBehavior link : CLIENT_LINKS) {
            String frequency = link.getNetworkKey();
            if (frequency.isBlank())
                continue;

            Vector3d linkPos = JOMLConversion.atCenterOf(link.getLocation());
            SubLevelAccess linkSublevel = SableCompanion.INSTANCE.getContaining(level, linkPos);
            if (linkSublevel != null)
                linkSublevel.logicalPose().transformPosition(linkPos);

            double distance = pos.distanceSquared(linkPos);
            if (distance >= linkRange * linkRange)
                continue;

            nearest.merge(frequency, (int) distance, Math::min);
        }

        for (Map.Entry<String, Integer> entry : nearest.entrySet())
            frequencies.add(IntAttached.with(entry.getValue(), entry.getKey()));
        return frequencies;
    }

    public static void clearClientLinks() {
        CLIENT_LINKS.clear();
    }

}
