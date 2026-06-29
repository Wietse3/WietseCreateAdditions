package me.wietse3.wca.registry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.BrassRedstoneLinkBlock;
import me.wietse3.wca.content.brass_link.BrassRedstoneLinkGenerator;
import me.wietse3.wca.content.brass_link.controller.LecternBrassControllerBlock;
import me.wietse3.wca.content.girder.GirderVerticalShaftModel;
import me.wietse3.wca.content.girder.GirderVerticalShaftBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.*;

public class WCABlocks {
    private static final CreateRegistrate REGISTRATE = WietseCreateAdditions.getRegistrate();

    public static final BlockEntry<GirderVerticalShaftBlock> METAL_GIRDER_VERTICAL_SHAFT =
            REGISTRATE.block("metal_girder_vertical_shaft", GirderVerticalShaftBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                            .sound(SoundType.NETHERITE_BLOCK))
                    .transform(pickaxeOnly())
                    .blockstate((ctx, prov) -> {
                        prov.simpleBlock(ctx.getEntry(), prov.models().getExistingFile(
                                prov.modLoc("block/metal_girder_vertical_shaft")
                        ));
                    })
                    .loot((p, b) -> p.add(b, p.createSingleItemTable(AllBlocks.METAL_GIRDER.get())
                            .withPool(p.applyExplosionCondition(AllBlocks.SHAFT.get(), LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .add(LootItem.lootTableItem(AllBlocks.SHAFT.get()))))))
                    .onRegister(CreateRegistrate.blockModel(() -> GirderVerticalShaftModel::new))
                    .register();

    public static final BlockEntry<BrassRedstoneLinkBlock> BRASS_REDSTONE_LINK =
            REGISTRATE.block("brass_redstone_link", BrassRedstoneLinkBlock::new)
                    .initialProperties(SharedProperties::wooden)
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN)
                            .forceSolidOn())
                    .transform(axeOrPickaxe())
                    .tag(AllTags.AllBlockTags.BRITTLE.tag, AllTags.AllBlockTags.SAFE_NBT.tag)
                    .blockstate(new BrassRedstoneLinkGenerator()::generate)
                    .addLayer(() -> RenderType::cutoutMipped)
                    .item()
                    .transform(customItemModel("_", "transmitter"))
                    .register();

    public static final BlockEntry<LecternBrassControllerBlock> LECTERN_BRASS_CONTROLLER =
            REGISTRATE.block("lectern_brass_controller", LecternBrassControllerBlock::new)
                    .initialProperties(() -> Blocks.LECTERN)
                    .transform(axeOnly())
                    .blockstate((c, p) -> p.horizontalBlock(c.get(), p.models()
                            .getExistingFile(p.mcLoc("block/lectern"))))
                    .loot((lt, block) -> lt.dropOther(block, Blocks.LECTERN))
                    .register();

    public static void register() {}
}
