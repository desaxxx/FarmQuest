package org.nandayo.farmquest.service.registry;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.nandayo.farmquest.FarmQuest;

import java.util.List;

public class GUIRegistry extends Registry {

    @Getter
    private FileConfiguration config;
    public GUIRegistry(@NotNull FarmQuest plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getFilePath() {
        return "gui.yml";
    }

    @Override
    public void load() {
        config = YamlConfiguration.loadConfiguration(getFile());
    }

    @Override
    public void save() {
        // NO SAVE
    }



    @NotNull
    public String getString(@NotNull String path) {
        return config.getString(path,"");
    }

    @NotNull
    public List<String> getStringList(@NotNull String path) {
        return config.getStringList(path);
    }
}
