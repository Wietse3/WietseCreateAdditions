package me.wietse3.wca.config;

import net.createmod.catnip.config.ConfigBase;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public abstract class WCAConfigBase extends ConfigBase {

    protected ConfigStringList sl(String name, List<String> def, String... comment) {
        return new ConfigStringList(name, def, comment);
    }

    public class ConfigStringList extends CValue<List<? extends String>, ModConfigSpec.ConfigValue<List<? extends String>>> {

        public ConfigStringList(String name, List<String> def, String... comment) {
            super(name, b -> b.defineListAllowEmpty(name, def, () -> "", e -> e instanceof String), comment);
        }
    }

}
