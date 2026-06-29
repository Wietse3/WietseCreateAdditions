package me.wietse3.wca.content.girder;

import com.simibubi.create.foundation.block.connected.CTModel;
import net.minecraft.client.resources.model.BakedModel;

public class GirderVerticalShaftModel extends CTModel {

    public GirderVerticalShaftModel(BakedModel originalModel) {
        super(originalModel, new GirderVerticalShaftCTBehavior());
    }
}