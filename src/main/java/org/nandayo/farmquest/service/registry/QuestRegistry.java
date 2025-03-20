package org.nandayo.farmquest.service.registry;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.nandayo.DAPI.Util;
import org.nandayo.farmquest.model.quest.Objective;
import org.nandayo.farmquest.model.quest.ObjectiveType;
import org.nandayo.farmquest.model.quest.Quest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class QuestRegistry implements Registry<Quest> {

    private File file;

    @Override
    public @NotNull String getFilePath() {
        return "database/quests.yml";
    }

    @Override
    public @NotNull File file() {
        if(file == null) file = Registry.super.file();
        return file;
    }

    @Override
    public void load() {
        Quest.getRegisteredQuests().clear();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file());
        ConfigurationSection quests = config.getConfigurationSection("quests");
        if(quests == null) return;
        for(String id : quests.getKeys(false)) {
            String name = quests.getString(id + ".name","Unknown");

            ObjectiveType type = ObjectiveType.get(quests.getString(id + ".objective.type",""));
            Material material = Material.getMaterial(quests.getString(id + ".objective.material",""));
            int targetAmount = quests.getInt(id + ".objective.target_amount", 0);
            long timeLimit = quests.getLong(id + ".objective.time_limit", 0);
            if(type == null || material == null) continue;

            Objective objective = new Objective(type, material, targetAmount, timeLimit);
            new Quest(id, objective, name).register();

        }
    }

    @Override
    public void save() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file());
        for(Quest quest : new ArrayList<>(Quest.getRegisteredQuests())) {
            String namespace = "quests." + quest.getId();
            config.set(namespace + ".name", quest.getName());
            config.set(namespace + ".objective.type", quest.getObjective().getType().toString());
            config.set(namespace + ".objective.material", quest.getObjective().getMaterial().toString());
            config.set(namespace + ".objective.target_amount", quest.getObjective().getTargetAmount());
            config.set(namespace + ".objective.time_limit", quest.getObjective().getTimeLimit());
        }
        try {
            config.save(file());
        }catch (IOException e) {
            Util.log("{WARN}Couldn't save Quests configuration.");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
