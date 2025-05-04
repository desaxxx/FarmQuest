package org.nandayo.farmquest.service.registry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.Util;
import org.nandayo.dapi.model.BoundingBox;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.farm.FarmRegion;
import org.nandayo.farmquest.model.quest.Quest;

import java.io.IOException;
import java.util.*;

public class FarmRegistry extends Registry {

    public FarmRegistry(@NotNull FarmQuest plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getFilePath() {
        return "database/farms.yml";
    }

    @Override
    public void load() {
        Farm.getRegisteredFarms().clear();
        FileConfiguration config = YamlConfiguration.loadConfiguration(getFile());
        ConfigurationSection farms = config.getConfigurationSection("farms");
        if(farms == null) return;
        for(String id : farms.getKeys(false)) {
            BoundingBox box = BoundingBox.fromString(farms.getString(id + ".region",""));
            FarmRegion region = new FarmRegion(box.getMinPoint(), box.getMaxPoint(), box.getWorldName());
            Collection<Quest> quests = farms.getStringList(id + ".quests").stream().filter(Objects::nonNull).map(Quest::getQuest).filter(Objects::nonNull).toList();

            new Farm(id, region, quests).register();
        }
        Util.log("Loaded " + Farm.getRegisteredFarms().size() + " farms.");
    }

    @Override
    public void save() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(getFile());
        config.set("farms", null);
        for(Farm farm : Farm.getRegisteredFarms()) {
            String namespace = "farms." + farm.getId();
            config.set(namespace + ".region", farm.getRegion().parseString());
            config.set(namespace + ".quests", farm.getQuests().stream().filter(Objects::nonNull).map(Quest::getId).toList());
        }
        try {
            config.save(getFile());
        }catch (IOException e) {
            Util.log("{WARN}Couldn't save Farm configuration.");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
