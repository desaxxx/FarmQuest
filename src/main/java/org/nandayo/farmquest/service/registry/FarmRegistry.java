package org.nandayo.farmquest.service.registry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.nandayo.DAPI.Util;
import org.nandayo.farmquest.model.Farm;
import org.nandayo.farmquest.model.FarmRegion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FarmRegistry implements Registry<Farm> {

    private File file;

    @Override
    public @NotNull String getFilePath() {
        return "database/farms.yml";
    }

    @Override
    public @NotNull File file() {
        if (file == null) file = Registry.super.file();
        return file;
    }

    @Override
    public void load() {
        Farm.getRegisteredFarms().clear();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file());
        ConfigurationSection farms = config.getConfigurationSection("farms");
        if(farms == null) return;
        for(String id : farms.getKeys(false)) {
            FarmRegion region = FarmRegion.fromString(farms.getString(id + ".region",""));

            new Farm(id, region).register();
        }
    }

    @Override
    public void save() {
        FileConfiguration config = new YamlConfiguration();
        for(Farm farm : new ArrayList<>(Farm.getRegisteredFarms())) {
            String namespace = "farms." + farm.getId();
            config.set(namespace + ".region", farm.getRegion().parseString());

            farm.unregister();
        }
        try {
            config.save(file());
        }catch (IOException e) {
            Util.log("{WARN}Couldn't save Farm configuration.");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
