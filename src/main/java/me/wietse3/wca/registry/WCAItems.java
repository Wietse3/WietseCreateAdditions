package me.wietse3.wca.registry;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.controller.BrassLinkedControllerItem;

public class WCAItems {
    private static final CreateRegistrate REGISTRATE = WietseCreateAdditions.getRegistrate();

    public static final ItemEntry<BrassLinkedControllerItem> BRASS_LINKED_CONTROLLER =
            REGISTRATE.item("brass_linked_controller", BrassLinkedControllerItem::new)
                    .properties(p -> p.stacksTo(1))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();

    public static void register() {}
}
