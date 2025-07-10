package org.nandayo.farmquest.service.registry;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.Util;
import org.nandayo.dapi.object.DMaterial;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.enumeration.FarmBlock;
import org.nandayo.farmquest.model.quest.Objective;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.model.quest.QuestProperty;
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
            DMaterial dIcon = DMaterial.getByName(quests.getString(id + ".icon", "PAPER"));
            Material icon = dIcon.parseMaterial() == null ? Material.PAPER : dIcon.parseMaterial();

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
            for(Reward.RewardType rewardType : Reward.RewardType.values()) {
                String rewardNamespace = id + ".rewards." + rewardType.name();
                List<String> run = quests.getStringList(rewardNamespace);
                rewards.add(new Reward(rewardType, run));
            }
            QuestProperty questProperty = new QuestProperty();
            for(QuestProperty.Property property : questProperty.values()) {
                property.setEnabled(quests.getBoolean(id + ".property." + property.getString(),false));
            }

            new Quest(type, farmBlock, targetAmount, timeLimit, rewards, questProperty, id, name, description, icon).register();
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
            config.set(namespace + ".icon", quest.getIcon().toString());
            config.set(namespace + ".objective.type", quest.getType().toString());
            config.set(namespace + ".objective.farm_block", quest.getFarmBlock().toString());
            config.set(namespace + ".objective.target_amount", quest.getTargetAmount());
            config.set(namespace + ".objective.time_limit", quest.getTimeLimit());
            for(Reward.RewardType rewardType : Reward.RewardType.values()) {
                Reward reward = quest.getReward(rewardType);
                if(reward == null) continue;
                config.set(namespace + ".rewards." + rewardType.name(), reward.getRun());
            }
            for(QuestProperty.Property property : quest.getQuestProperty().values()) {
                config.set(namespace + ".property." + property.getString(), property.isEnabled());
            }
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
