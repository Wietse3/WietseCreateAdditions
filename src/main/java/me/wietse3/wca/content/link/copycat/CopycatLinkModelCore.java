package me.wietse3.wca.content.link.copycat;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableCullFace.*;

public class CopycatLinkModelCore extends CopycatModelCore {

    @Override
    public void emitCopycatQuads(String key, BlockState state, CopycatRenderContext context, BlockState material) {
        Direction facing = state.getValue(DirectionalBlock.FACING);

        if (AllBlocks.COPYCAT_BASE.has(material)) {
            if (facing.getAxis().isVertical()) {
                assemble(context, facing, 0, 0, 16, 2);
                assemble(context, facing, 0, 14, 16, 2);
                assemble(context, facing, 0, 2, 2, 12);
                assemble(context, facing, 14, 2, 2, 12);
            } else {
                assemble(context, facing, 0, 0, 16, 1);
                assemble(context, facing, 0, 15, 16, 1);
                assemble(context, facing, 0, 1, 3, 14);
                assemble(context, facing, 13, 1, 3, 14);
            }
        } else {
            assemble(context, facing, 0, 0, 16, 16);
        }
    }

    private void assemble(CopycatRenderContext context, Direction facing, int x, int z, int sizeX, int sizeZ) {
        if (facing.getAxis().isVertical()) {
            boolean flipY = facing == Direction.DOWN;
            AssemblyTransform transform = t -> t.flipY(flipY);

            context.assemblePiece(
                    transform,
                    vec3(x, 1, z),
                    aabb(sizeX, 2, sizeZ).move(x, 14, z),
                    cull(DOWN)
            );
            context.assemblePiece(
                    transform,
                    vec3(x, 0, z),
                    aabb(sizeX, 1, sizeZ).move(x, 0, z),
                    cull(UP)
            );
        } else {
            int rot = (int) facing.toYRot();
            AssemblyTransform transform = t -> t.rotateY(rot);

            context.assemblePiece(
                    transform,
                    vec3(x, z, 0),
                    aabb(sizeX, sizeZ, 1).move(x, z, 0),
                    cull(SOUTH)
            );
            context.assemblePiece(
                    transform,
                    vec3(x, z, 1),
                    aabb(sizeX, sizeZ, 2).move(x, z, 14),
                    cull(NORTH)
            );
        }
    }
}
