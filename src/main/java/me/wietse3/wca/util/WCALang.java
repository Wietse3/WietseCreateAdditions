package me.wietse3.wca.util;

import me.wietse3.wca.WietseCreateAdditions;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;

public class WCALang extends Lang {

    public static LangBuilder builder() {
        return new LangBuilder(WietseCreateAdditions.MODID);
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

}
