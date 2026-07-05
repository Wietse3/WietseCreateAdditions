package me.wietse3.wca.content.brass_link.controller;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import me.wietse3.wca.registry.WCAItems;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LecternBrassControllerBlockEntity extends SmartBlockEntity {
    private List<BrassControllerBind> binds = new ArrayList<>();
    private UUID user;
    private UUID prevUser;    // used only on client
    private boolean deactivatedThisTick;    // used only on server

    public LecternBrassControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);

        ListTag list = new ListTag();
        for (BrassControllerBind bind : binds) {
            CompoundTag tag = new CompoundTag();
            tag.putString("freq", bind.frequency());
            tag.putInt("power", bind.power());
            list.add(tag);
        }
        compound.put("Binds", list);

        if (user != null)
            compound.putUUID("User", user);
    }

    @Override
    public void writeSafe(CompoundTag compound) {
        super.writeSafe(compound);

        ListTag list = new ListTag();
        for (BrassControllerBind bind : binds) {
            CompoundTag tag = new CompoundTag();
            tag.putString("freq", bind.frequency());
            tag.putInt("power", bind.power());
            list.add(tag);
        }
        compound.put("Binds", list);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        binds.clear();
        if (compound.contains("Binds", Tag.TAG_LIST)) {
            ListTag list = compound.getList("Binds", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag tag = list.getCompound(i);

                String freq = tag.getString("freq");
                int power = tag.getInt("power");

                binds.add(new BrassControllerBind(freq, power));
            }
        }
        user = compound.hasUUID("User") ? compound.getUUID("User") : null;
    }

    public ItemStack getController() {
        return createLinkedController();
    }

    public boolean hasUser() {
        return user != null;
    }

    public boolean isUsedBy(Player player) {
        return hasUser() && user.equals(player.getUUID());
    }

    public void tryStartUsing(Player player) {
        if (!deactivatedThisTick && !hasUser() && !playerIsUsingLectern(player) && playerInRange(player, level, worldPosition))
            startUsing(player);
    }

    public void tryStopUsing(Player player) {
        if (isUsedBy(player))
            stopUsing(player);
    }

    private void startUsing(Player player) {
        user = player.getUUID();
        player.getPersistentData().putBoolean("IsUsingLecternController", true);
        sendData();
    }

    private void stopUsing(Player player) {
        user = null;
        if (player != null)
            player.getPersistentData().remove("IsUsingLecternController");
        deactivatedThisTick = true;
        sendData();
    }

    public static boolean playerIsUsingLectern(Player player) {
        return player.getPersistentData().contains("IsUsingLecternController");
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> this::tryToggleActive);
            prevUser = user;
        }

        if (!level.isClientSide) {
            deactivatedThisTick = false;

            if (!(level instanceof ServerLevel))
                return;
            if (user == null)
                return;

            Entity entity = ((ServerLevel) level).getEntity(user);
            if (!(entity instanceof Player player)) {
                stopUsing(null);
                return;
            }

            if (!playerInRange(player, level, worldPosition) || !playerIsUsingLectern(player))
                stopUsing(player);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tryToggleActive() {
        if (user == null && Minecraft.getInstance().player.getUUID().equals(prevUser)) {
            BrassLinkedControllerClientHandler.deactivateInLectern();
        } else if (prevUser == null && Minecraft.getInstance().player.getUUID().equals(user)) {
            BrassLinkedControllerClientHandler.activateInLectern(worldPosition);
        }
    }

    public void setController(ItemStack newController) {
        if (newController != null) {
            binds = new ArrayList<>(BrassControllerBind.CODEC.listOf()
                    .parse(NbtOps.INSTANCE, newController.getOrCreateTag().get("BrassLinkedControllerLinks"))
                    .result().orElse(List.of()));
            AllSoundEvents.CONTROLLER_PUT.playOnServer(level, worldPosition);
        }
    }

    public void swapControllers(ItemStack stack, Player player, InteractionHand hand, BlockState state) {
        ItemStack newController = stack.copy();
        stack.setCount(0);
        if (player.getItemInHand(hand).isEmpty()) {
            player.setItemInHand(hand, createLinkedController());
        } else {
            dropController(state);
        }
        setController(newController);
    }

    public void dropController(BlockState state) {
        Entity entity = ((ServerLevel) level).getEntity(user);
        if (entity instanceof Player player)
            stopUsing(player);

        Direction dir = state.getValue(LecternBrassControllerBlock.FACING);
        double x = worldPosition.getX() + 0.5 + 0.25 * dir.getStepX();
        double y = worldPosition.getY() + 1;
        double z = worldPosition.getZ() + 0.5 + 0.25 * dir.getStepZ();

        ItemEntity itemEntity = new ItemEntity(level, x, y, z, createLinkedController());
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
        binds.clear();
    }

    public static boolean playerInRange(Player player, Level world, BlockPos pos) {
        double reach = 0.4 * player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
        return player.distanceToSqr(Vec3.atCenterOf(pos)) < reach * reach;
    }

    private ItemStack createLinkedController() {
        ItemStack stack = WCAItems.BRASS_LINKED_CONTROLLER.asStack();
        stack.getOrCreateTag().put("BrassLinkedControllerLinks", BrassControllerBind.CODEC.listOf()
                .encodeStart(NbtOps.INSTANCE, new ArrayList<>(binds)).result().orElse(new ListTag()));
        return stack;
    }
}
