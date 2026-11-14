package me.wietse3.wca.config;

import java.util.List;

public class CServer extends WCAConfigBase {

    public final ConfigInt brassLinkRange = i(256, 1, "linkRange", Comments.brassLinkRange);
    public final ConfigEnum<CopycatLinkSetting> copycatLinkInteraction =
            e(CopycatLinkSetting.BOTH, "copycatLinkInteraction", Comments.copycatLinkInteraction);
    public final ConfigStringList linkBridgeBlacklist = sl("linkBridgeBlacklist",
            List.of("create:redstone_link"), Comments.linkBridgeBlacklist);

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String brassLinkRange = "Maximum possible range in blocks of brass redstone link connections.";
        static String copycatLinkInteraction =
                "Which redstone link can be replaced with the copycat link by clicking on it with a copycat panel.";
        static String linkBridgeBlacklist = "Redstone link blocks that shouldn't be able to use bridges.";
    }

    public enum CopycatLinkSetting {
        NONE, BRASS_ONLY, BOTH
    }

}
