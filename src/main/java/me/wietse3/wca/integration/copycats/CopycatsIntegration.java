package me.wietse3.wca.integration.copycats;

import com.copycatsplus.copycats.foundation.tooltip.CopycatDescription;
import com.copycatsplus.copycats.utility.TooltipUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import me.wietse3.wca.WietseCreateAdditions;
import net.createmod.catnip.lang.FontHelper;
import net.neoforged.neoforge.common.NeoForge;

public class CopycatsIntegration {
    private static final CreateRegistrate REGISTRATE = WietseCreateAdditions.getRegistrate();

    public static void register() {
        REGISTRATE.setTooltipModifierFactory(item -> TooltipUtils.sequential(
                CopycatDescription.create(item),
                new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE),
                TooltipModifier.mapNull(KineticStats.create(item)))
        );

        CopycatsRegistry.register();
        NeoForge.EVENT_BUS.register(CopycatsEvents.class);
    }
}
