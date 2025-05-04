package org.nandayo.farmquest.service.registry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.Util;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.enumeration.FarmBlock;
import org.nandayo.farmquest.model.quest.Objective;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.model.quest.Reward;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuestRegistry extends Registry {

    public QuestRegistry(@NotNull FarmQuest plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getFilePath() {
        return "database/quests.yml";
    }

    @Override
    public void load() {
        Quest.getRegisteredQuests().clear();
        FileConfiguration config = YamlConfiguration.loadConfiguration(getFile());
        ConfigurationSection quests = config.getConfigurationSection("quests");
        if(quests == null) return;
        for(String id : quests.getKeys(false)) {
            String name = quests.getString(id + ".name", "Unknown");
            String description = quests.getString(id + ".description","Unknown");

            Objective.ObjectiveType type = Objective.ObjectiveType.get(quests.getString(id + ".objective.type",""));
            FarmBlock farmBlock = FarmBlock.get(quests.getString(id + ".objective.farm_block",""));
            int targetAmount = quests.getInt(id + ".objective.target_amount", 0);
            long timeLimit = quests.getLong(id + ".objective.time_limit", 0);
            if(type == null) {
                Util.log("{WARN}ObjectiveType for Quest '" + id + "' is null.");
                continue;
            }
            if(farmBlock == null) {
                Util.log("{WARN}FarmBlock for Quest '" + id + "' is null.");
                continue;
            }

            Collection<Reward> rewards = new ArrayList<>();
            ConfigurationSection rewardSection = quests.getConfigurationSection(id + ".rewards");
            if(rewardSection != null) {
                for(String rewardKey : rewardSection.getKeys(false)) {
                    Reward.RewardType rewardType = Reward.RewardType.get(rewardKey);
                    if(rewardType == null) {
                        Util.log("{WARN}RewardType for Quest '" + id + "' is null.");
                        continue;
                    }
                    List<String> run = rewardSection.getStringList(rewardKey);

                    rewards.add(new Reward(rewardType, run));
                }
            }

            new Quest(id, name, description, type, farmBlock, targetAmount, timeLimit, rewards).register();
        }
        Util.log("Loaded " + Quest.getRegisteredQuests().size() + " quests.");
    }

    @Override
    public void save() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(getFile());
        config.set("quests", null);
        for(Quest quest : Quest.getRegisteredQuests()) {
            String namespace = "quests." + quest.getId();
            config.set(namespace + ".name", quest.getName());
            config.set(namespace + ".description", quest.getDescription());
            config.set(namespace + ".objective.type", quest.getType().toString());
            config.set(namespace + ".objective.farm_block", quest.getFarmBlock().toString());
            config.set(namespace + ".objective.target_amount", quest.getTargetAmount());
            config.set(namespace + ".objective.time_limit", quest.getTimeLimit());
        }
        try {
            config.save(getFile());
        }catch (IOException e) {
            Util.log("{WARN}Couldn't save Quests configuration.");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
