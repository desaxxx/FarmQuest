package org.nandayo.farmquest.service.registry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.Util;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.farm.FarmTool;

public class ToolRegistry extends Registry {

    public ToolRegistry(@NotNull FarmQuest plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getFilePath() {
        return "database/tools.yml";
    }

    @Override
    public void load() {
        FarmTool.getRegisteredTools().clear();
        FileConfiguration config = YamlConfiguration.loadConfiguration(getFile());
        ConfigurationSection tools = config.getConfigurationSection("tools");
        if(tools == null) return;
        for(String id : tools.getKeys(false)) {
            String name = tools.getString(id + ".name","Unknown");
            ItemStack item = FarmQuest.getInstance().buildItem(tools.getConfigurationSection(id + ".item"));
            if(item == null) {
                Util.log(String.format("{WARN}Couldn't build item for FarmTool '%s'", id));
                continue;
            }

            new FarmTool(id, name, item).register();
        }
        Util.log("Loaded " + FarmTool.getRegisteredTools().size() + " farm tools.");
    }

    @Override
    public void save() {
        // NO SAVE
    }
}
