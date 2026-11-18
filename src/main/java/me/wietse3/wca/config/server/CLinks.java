package me.wietse3.wca.config.server;

import me.wietse3.wca.config.WCAConfigBase;

import java.util.List;

public class CLinks extends WCAConfigBase {

    public final ConfigStringList bridgeBlacklist = sl("bridgeBlacklist",
            List.of("create:redstone_link", "wca:copycat_redstone_link"), Comments.bridgeBlacklist);
    public final ConfigEnum<CopycatLinkSetting> copycatLinkInteraction =
            e(CopycatLinkSetting.BOTH, "copycatLinkInteraction", Comments.copycatLinkInteraction);

    public final ConfigGroup brassRedstoneLink = group(1, "brassRedstoneLink", Comments.brassRedstoneLink);
    public final ConfigInt linkRange = i(256, 1, "linkRange", Comments.linkRange);
    public final ConfigBool allowInvertMode = b(true, "allowInvertMode", Comments.allowInvertMode);

    @Override
    public String getName() {
        return "links";
    }

    private static class Comments {
        static String bridgeBlacklist = "Redstone link blocks that shouldn't use bridges.";

        static String brassRedstoneLink = "Configure brass redstone links.";
        static String linkRange = "Maximum possible range in blocks of brass redstone link connections.";
        static String copycatLinkInteraction =
                "Which redstone links can be replaced with the copycat link by clicking on it with a copycat panel.";
        static String allowInvertMode = "Whether players can use Invert mode on receivers.";
    }

    public enum CopycatLinkSetting {
        NONE, BRASS_ONLY, BOTH
    }

}
