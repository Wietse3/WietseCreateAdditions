package me.wietse3.wca.registry;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.bridge.BrassLinkBridgeItem;
import me.wietse3.wca.content.brass_link.controller.BrassLinkedControllerItem;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;

public class WCAItems {
    private static final CreateRegistrate REGISTRATE = WietseCreateAdditions.getRegistrate();

    public static final ItemEntry<BrassLinkedControllerItem> BRASS_LINKED_CONTROLLER =
            REGISTRATE.item("brass_linked_controller", BrassLinkedControllerItem::new)
                    .properties(p -> p.stacksTo(1))
                    .model(AssetLookup.itemModelWithPartials())
                    .recipe((c, p) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get())
                                .pattern("BBB")
                                .pattern(" L ")
                                .pattern("BBB")
                                .define('B', ItemTags.WOODEN_BUTTONS)
                                .define('L', WCABlocks.BRASS_REDSTONE_LINK)
                                .unlockedBy("has_ingredient", RegistrateRecipeProvider.has(WCABlocks.BRASS_REDSTONE_LINK))
                                .save(p);
                    })
                    .register();

    public static final ItemEntry<BrassLinkBridgeItem> BRASS_LINK_BRIDGE =
            REGISTRATE.item("brass_link_bridge", BrassLinkBridgeItem::new)
                    .properties(p -> p)
                    .recipe((c, p) -> {
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get())
                                .pattern("S")
                                .pattern("T")
                                .pattern("C")
                                .define('S', CommonMetal.BRASS.plates)
                                .define('T', AllItems.TRANSMITTER)
                                .define('C', AllItems.CRAFTER_SLOT_COVER)
                                .unlockedBy("has_ingredient", RegistrateRecipeProvider.has(AllItems.TRANSMITTER))
                                .save(p);
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get())
                                .requires(c.get())
                                .unlockedBy("has_ingredient", RegistrateRecipeProvider.has(c.get()))
                                .save(p, c.getId() + "_clear");
                    })
                    .register();

    public static void register() {}
}
