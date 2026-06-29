package me.wietse3.wca.content.brass_link.controller;

import com.simibubi.create.foundation.advancement.AllAdvancements;
import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.BrassLinkBehavior;
import me.wietse3.wca.content.brass_link.IBrassLinkable;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

import java.util.*;
import java.util.Map.Entry;

public class BrassLinkedControllerServerHandler {

    public static WorldAttached<Map<UUID, Collection<ManualFrequencyEntry>>> receivedInputs =
            new WorldAttached<>($ -> new HashMap<>());
    static final int TIMEOUT = 30;

    public static void tick(LevelAccessor world) {
        Map<UUID, Collection<ManualFrequencyEntry>> map = receivedInputs.get(world);
        for (Iterator<Entry<UUID, Collection<ManualFrequencyEntry>>> iterator = map.entrySet()
                .iterator(); iterator.hasNext(); ) {

            Entry<UUID, Collection<ManualFrequencyEntry>> entry = iterator.next();
            Collection<ManualFrequencyEntry> list = entry.getValue();

            for (Iterator<ManualFrequencyEntry> entryIterator = list.iterator(); entryIterator.hasNext(); ) {
                ManualFrequencyEntry manualFrequencyEntry = entryIterator.next();
                manualFrequencyEntry.decrement();
                if (!manualFrequencyEntry.isAlive()) {
                    WietseCreateAdditions.BRASS_LINK_NETWORK_HANDLER.removeFromNetwork(world, manualFrequencyEntry);
                    entryIterator.remove();
                }
            }

            if (list.isEmpty())
                iterator.remove();
        }
    }

    public static void receivePressed(LevelAccessor world, BlockPos pos, UUID uniqueID, List<BrassControllerBind> collect,
                                      boolean pressed) {
        Map<UUID, Collection<ManualFrequencyEntry>> map = receivedInputs.get(world);
        Collection<ManualFrequencyEntry> list = map.computeIfAbsent(uniqueID, $ -> new ArrayList<>());

        WithNext:
        for (BrassControllerBind activated : collect) {
            String frequency = activated.frequency();
            int power = activated.power();

            for (ManualFrequencyEntry entry : list) {
                if (entry.getSecond()
                        .equals(frequency)) {
                    if (!pressed)
                        entry.setFirst(0);
                    else
                        entry.updatePosition(pos);
                    continue WithNext;
                }
            }

            if (!pressed)
                continue;

            ManualFrequencyEntry entry = new ManualFrequencyEntry(pos, frequency, power);
            WietseCreateAdditions.BRASS_LINK_NETWORK_HANDLER.addToNetwork(world, entry);
            list.add(entry);

            for (IBrassLinkable linkable : WietseCreateAdditions.BRASS_LINK_NETWORK_HANDLER.getNetworkOf(world, entry))
                if (linkable instanceof BrassLinkBehavior lb && lb.isListening())
                    AllAdvancements.LINKED_CONTROLLER.awardTo(world.getPlayerByUUID(uniqueID));
        }
    }

    static class ManualFrequencyEntry extends IntAttached<String> implements IBrassLinkable {

        private BlockPos pos;
        private final int power;

        public ManualFrequencyEntry(BlockPos pos, String frequency, int power) {
            super(TIMEOUT, frequency);
            this.pos = pos;
            this.power = power;
        }

        public void updatePosition(BlockPos pos) {
            this.pos = pos;
            setFirst(TIMEOUT);
        }

        @Override
        public int getTransmittedStrength() {
            return isAlive() ? power : 0;
        }

        @Override
        public boolean isAlive() {
            return getFirst() > 0;
        }

        @Override
        public BlockPos getLocation() {
            return pos;
        }

        @Override
        public void setReceivedStrength(int power) {
        }

        @Override
        public boolean isListening() {
            return false;
        }

        @Override
        public String getNetworkKey() {
            return getSecond();
        }

    }

}
