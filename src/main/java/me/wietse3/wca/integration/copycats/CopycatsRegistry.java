package me.wietse3.wca.integration.copycats;

import com.copycatsplus.copycats.CCBuilderTransformers;
import com.copycatsplus.copycats.foundation.tooltip.CopycatCharacteristics;
import com.copycatsplus.copycats.foundation.tooltip.CopycatDescription;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.copycat.CopycatBrassLinkBlockEntity;
import me.wietse3.wca.content.brass_link.copycat.CopycatBrassLinkBlock;
import me.wietse3.wca.content.link.copycat.CopycatLinkBlock;
import me.wietse3.wca.content.link.copycat.CopycatLinkBlockEntity;
import me.wietse3.wca.content.link.copycat.CopycatLinkModelCore;
import me.wietse3.wca.content.link.copycat.CopycatLinkRenderer;

import static com.copycatsplus.copycats.CCBlocks.onClient;
import static com.copycatsplus.copycats.CCCustomModels.createBlockModel;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static me.wietse3.wca.registry.WCABlocks.CARRY_ON_BLACKLIST;

public class CopycatsRegistry {
    private static final CreateRegistrate REGISTRATE = WietseCreateAdditions.getRegistrate();

    // Blocks

    public static final BlockEntry<CopycatLinkBlock> COPYCAT_REDSTONE_LINK =
            REGISTRATE.block("copycat_redstone_link", CopycatLinkBlock::new)
                    .transform(CCBuilderTransformers.copycat())
                    .tag(AllTags.AllBlockTags.BRITTLE.tag, AllTags.AllBlockTags.SAFE_NBT.tag, CARRY_ON_BLACKLIST)
                    .asOptional()
                    .onRegister(onClient(() -> createBlockModel(CopycatLinkModelCore::new)))
                    .item()
                    .onRegister(CopycatDescription.register(
                            CopycatCharacteristics.COPYCAT,
                            CopycatCharacteristics.FUNCTIONAL
                    ))
                    .transform(customItemModel("_", "item"))
                    .register();

    public static final BlockEntry<CopycatBrassLinkBlock> COPYCAT_BRASS_REDSTONE_LINK =
            REGISTRATE.block("copycat_brass_redstone_link", CopycatBrassLinkBlock::new)
                    .transform(CCBuilderTransformers.copycat())
                    .tag(AllTags.AllBlockTags.BRITTLE.tag, AllTags.AllBlockTags.SAFE_NBT.tag, CARRY_ON_BLACKLIST)
                    .asOptional()
                    .onRegister(onClient(() -> createBlockModel(CopycatLinkModelCore::new)))
                    .item()
                    .onRegister(CopycatDescription.register(
                            CopycatCharacteristics.COPYCAT,
                            CopycatCharacteristics.FUNCTIONAL
                    ))
                    .transform(customItemModel("copycat_redstone_link", "item"))
                    .register();

    // Block entities

    public static final BlockEntityEntry<CopycatLinkBlockEntity> COPYCAT_REDSTONE_LINK_BE = REGISTRATE
            .blockEntity("copycat_redstone_link", CopycatLinkBlockEntity::new)
            .validBlocks(CopycatsRegistry.COPYCAT_REDSTONE_LINK)
            .renderer(() -> CopycatLinkRenderer::new)
            .register();

    public static final BlockEntityEntry<CopycatBrassLinkBlockEntity> COPYCAT_BRASS_REDSTONE_LINK_BE = REGISTRATE
            .blockEntity("copycat_brass_redstone_link", CopycatBrassLinkBlockEntity::new)
            .validBlocks(CopycatsRegistry.COPYCAT_BRASS_REDSTONE_LINK)
            .renderer(() -> CopycatLinkRenderer::new)
            .register();

    public static void register() {}
}
