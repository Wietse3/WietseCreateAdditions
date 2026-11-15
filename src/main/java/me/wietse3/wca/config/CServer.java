package me.wietse3.wca.config;

import me.wietse3.wca.config.server.CLinks;
import net.createmod.catnip.config.ConfigBase;

public class CServer extends ConfigBase {

    public final CLinks links = nested(0, CLinks::new, Comments.links);

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String links = "Settings related to redstone links";
    }

}
