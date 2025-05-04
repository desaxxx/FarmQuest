package org.nandayo.farmquest.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.Util;
import org.nandayo.farmquest.FarmQuest;

@Getter
public class Wrapper {

    private final FarmQuest plugin;
    public Wrapper(@NotNull FarmQuest plugin) {
        this.plugin = plugin;
        version = fetchVersion();
    }

    private final int version;

    private int fetchVersion() {
        String[] ver = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        if(ver.length < 2) {
            Util.log("{WARN}Could not fetch server version!");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        int major = 0;
        try {
            major = Integer.parseInt(ver[1]);
        } catch (NumberFormatException ignored) {}
        int minor = 0;
        if(ver.length >= 3) {
            try {
                minor = Integer.parseInt(ver[2]);
            } catch (NumberFormatException ignored) {}
        }

        int version = major * 10 + minor;
        if(version < 170) {
            Util.log(String.format("&cYou are using an unsupported server version '%s'!", String.join(".", ver)),
                    "&cPlease use v1.17 or newer.");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        return version;
    }
}
