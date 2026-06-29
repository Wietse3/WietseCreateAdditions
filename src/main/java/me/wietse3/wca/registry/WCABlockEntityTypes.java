package me.wietse3.wca.registry;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.BrassRedstoneLinkBlockEntity;
import me.wietse3.wca.content.brass_link.BrassRedstoneLinkRenderer;
import me.wietse3.wca.content.brass_link.controller.LecternBrassControllerBlockEntity;
import me.wietse3.wca.content.brass_link.controller.LecternBrassControllerRenderer;

public class WCABlockEntityTypes {
    private static final CreateRegistrate REGISTRATE = WietseCreateAdditions.getRegistrate();

    public static final BlockEntityEntry<KineticBlockEntity> ENCASED_SHAFT = REGISTRATE
            .blockEntity("encased_shaft", KineticBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual::shaft, false)
            .validBlocks(WCABlocks.METAL_GIRDER_VERTICAL_SHAFT)
            .renderer(() -> ShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<BrassRedstoneLinkBlockEntity> BRASS_REDSTONE_LINK = REGISTRATE
            .blockEntity("brass_redstone_link", BrassRedstoneLinkBlockEntity::new)
            .validBlocks(WCABlocks.BRASS_REDSTONE_LINK)
            .renderer(() -> BrassRedstoneLinkRenderer::new)
            .register();

    public static final BlockEntityEntry<LecternBrassControllerBlockEntity> LECTERN_BRASS_CONTROLLER = REGISTRATE
            .blockEntity("lectern_brass_controller", LecternBrassControllerBlockEntity::new)
            .validBlocks(WCABlocks.LECTERN_BRASS_CONTROLLER)
            .renderer(() -> LecternBrassControllerRenderer::new)
            .register();

    public static void register() {}
}
