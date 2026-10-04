package me.wietse3.wca.content.brass_link;

import me.wietse3.wca.registry.WCABlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BrassRedstoneLinkBlock extends BrassRedstoneLink<BrassRedstoneLinkBlockEntity> {

    public BrassRedstoneLinkBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<BrassRedstoneLinkBlockEntity> getBlockEntityClass() {
        return BrassRedstoneLinkBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BrassRedstoneLinkBlockEntity> getBlockEntityType() {
        return WCABlockEntityTypes.BRASS_REDSTONE_LINK.get();
    }
}
