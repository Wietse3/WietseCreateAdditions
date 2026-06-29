package me.wietse3.wca.content.girder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class GirderVerticalShaftCTBehavior extends ConnectedTextureBehaviour.Base {

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, TextureAtlasSprite sprite) {
        return AllSpriteShifts.GIRDER_POLE;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter level,
                              BlockPos pos, BlockPos otherPos, Direction face) {

        return other.getBlock() == state.getBlock() || other.is(AllBlocks.METAL_GIRDER.get());
    }
}
