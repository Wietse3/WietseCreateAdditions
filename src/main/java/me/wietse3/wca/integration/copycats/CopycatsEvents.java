package me.wietse3.wca.integration.copycats;

import com.simibubi.create.AllBlocks;
import me.wietse3.wca.content.brass_link.copycat.CopycatBrassLinkBlock;
import me.wietse3.wca.content.link.copycat.CopycatLinkBlock;
import me.wietse3.wca.registry.WCABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class CopycatsEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();

        if (player.isShiftKeyDown() || !player.mayBuild())
            return;

        ItemStack stack = player.getItemInHand(event.getHand());

        if (!AllBlocks.COPYCAT_PANEL.isIn(stack))
            return;

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        boolean isLink = state.is(AllBlocks.REDSTONE_LINK.get());
        boolean isBrassLink = state.is(WCABlocks.BRASS_REDSTONE_LINK.get());

        if (!isLink && !isBrassLink)
            return;

        level.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1, 1.25f);

        if (!level.isClientSide()) {
            boolean success;
            if (isLink)
                success = CopycatLinkBlock.fromRegular(level, pos);
            else
                success = CopycatBrassLinkBlock.fromRegular(level, pos);

            if (!player.isCreative() && success)
                stack.shrink(1);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
