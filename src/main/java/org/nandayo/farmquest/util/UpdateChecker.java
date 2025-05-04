package org.nandayo.farmquest.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.nandayo.dapi.Util;
import org.nandayo.farmquest.FarmQuest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private final int resourceId;
    private final FarmQuest plugin;

    public UpdateChecker(FarmQuest plugin, int resourceId) {
        this.resourceId = resourceId;
        this.plugin = plugin;
    }

    private void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId + "/~").openStream();
                 Scanner scann = new Scanner(is)) {
                if (scann.hasNext()) {
                    consumer.accept(scann.next());
                }
            } catch (IOException e) {
                Util.log("&cUnable to check for updates: " + e.getMessage());
            }
        });
    }

    public void inform() {
        FileConfiguration config = plugin.configRegistry.getConfig();
        if(!config.getBoolean("update_check", true)) return;

        this.getVersion(version -> {
            if(plugin.getDescription().getVersion().equals(version)) {
                Util.log("{WHITE}You are up to date!");
            }else {
                Util.log("{WARN}You are not up to date. (Latest release: " + version + ")");
                Util.log("{WARN}Open this link to update the plugin. https://www.spigotmc.org/resources/farmquest." + this.resourceId + "/");
            }
        });
    }
}
