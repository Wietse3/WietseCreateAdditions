package me.wietse3.wca.registry;

import me.wietse3.wca.WietseCreateAdditions;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;

public class WCALang extends Lang {

    public static void register() {
        add("tab.base", "Wietse's Create Additions");
        add("gui.brass_redstone_link.invert", "Invert");
        add("gui.brass_linked_controller.key", "Keybind: ");
        add("gui.brass_linked_controller.power", "Power");
    }

    private static void add(String key, String value) {
        addRaw(WietseCreateAdditions.MODID + "." + key, value);
    }

    private static void addRaw(String key, String value) {
        WietseCreateAdditions.getRegistrate().addRawLang(key, value);
    }

    public static LangBuilder builder() {
        return new LangBuilder(WietseCreateAdditions.MODID);
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }


}
