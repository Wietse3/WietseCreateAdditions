package me.wietse3.wca;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import me.wietse3.wca.content.brass_link.BrassLinkNetworkHandler;
import me.wietse3.wca.registry.*;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;

import static me.wietse3.wca.registry.WCACreativeTabs.BASE_CREATIVE_TAB;

@Mod(WietseCreateAdditions.MODID)
public class WietseCreateAdditions {
    public static final String MODID = "wca";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .defaultCreativeTab(BASE_CREATIVE_TAB.getKey())
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
            );

    public static final BrassLinkNetworkHandler BRASS_LINK_NETWORK_HANDLER = new BrassLinkNetworkHandler();

    public WietseCreateAdditions(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        modEventBus.addListener(this::commonSetup);

        WCABlocks.register();
        WCABlockEntityTypes.register();
        WCAItems.register();
        WCACreativeTabs.register(modEventBus);
        WCADataComponents.register(modEventBus);
        WCAPackets.register();
        WCALang.register();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    public static CreateRegistrate getRegistrate() {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
