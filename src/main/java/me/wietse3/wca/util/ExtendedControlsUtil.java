package me.wietse3.wca.util;

import com.simibubi.create.foundation.utility.ControlsUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

import java.util.ArrayList;
import java.util.List;

public class ExtendedControlsUtil extends ControlsUtil {

    private static List<KeyMapping> extendedControls;

    public static List<KeyMapping> getExtendedControls() {
        if (extendedControls == null) {
            Options gameSettings = Minecraft.getInstance().options;

            List<KeyMapping> standard = ControlsUtil.getControls();
            extendedControls = new ArrayList<>(standard.size() + 1);
            extendedControls.addAll(standard);

            extendedControls.add(gameSettings.keySprint);
        }
        return extendedControls;
    }
}
