package me.wietse3.wca.mixin.copycats;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.model.neoforge.CopycatModelNeoForge;
import com.copycatsplus.copycats.utility.neoforge.ModelDataUtils;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import me.wietse3.wca.content.brass_link.copycat.CopycatBrassLinkBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({
        CopycatBrassLinkBlockEntity.class
})
public abstract class CopycatBlockEntityMixin extends SmartBlockEntity implements ICopycatBlockEntity {

    public CopycatBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    @Unique
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public ModelData getModelData() {
        return ModelDataUtils.mergeData(
                super.getModelData(),
                ModelData.builder()
                        .with(CopycatModelNeoForge.MATERIAL_PROPERTY, getMaterial())
                        .build()
        ).build();
    }
}
