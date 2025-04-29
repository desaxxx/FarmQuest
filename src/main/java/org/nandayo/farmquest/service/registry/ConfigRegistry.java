package org.nandayo.farmquest.service.registry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.enumeration.Setting;

public class ConfigRegistry extends Registry {

    public ConfigRegistry(@NotNull FarmQuest plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getFilePath() {
        return "config.yml";
    }

    @Override
    public void load() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(getFile());
        for(Setting setting : Setting.values()) {
            boolean val = config.getBoolean("settings." + setting.name(), setting.isEnabled());
            Setting.set(setting, val);
        }
    }

    public void save() {
        // NO SAVE
    }
}
