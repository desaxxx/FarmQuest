package org.nandayo.farmquest.model.quest;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.dapi.Util;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.enumeration.FarmBlock;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.farm.Farm;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

@Getter
public class Quest extends Objective {

    private final @NotNull String id;
    private final @NotNull String name;
    private final @NotNull String description;
    private @Nullable Material icon;

    public Quest(@NotNull ObjectiveType type, @NotNull FarmBlock farmBlock, int targetAmount, long timeLimit, @NotNull Collection<Reward> rewards, @NotNull QuestProperty questProperty,
                 @NotNull String id, @NotNull String name, @NotNull String description, @Nullable Material icon) {
        super(type, farmBlock, targetAmount, timeLimit, rewards, questProperty);
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
    public Quest(@NotNull ObjectiveType type, @NotNull FarmBlock farmBlock, int targetAmount, long timeLimit, @NotNull Collection<Reward> rewards, @NotNull QuestProperty questProperty,
                 @NotNull String name, @NotNull String description, @Nullable Material icon) {
        this(type, farmBlock, targetAmount, timeLimit,  rewards, questProperty,
                Util.generateRandomLowerCaseString(2), name, description, icon);
    }

    public void register() {
        if(getQuest(id) == null) registeredQuests.add(this);
    }

    public void unregister() {
        registeredQuests.remove(this);
        questDeletion();
    }

    public void questDeletion() {
        // Make farmers who are progressing this quest drop it.
        for(Farmer farmer : Farmer.getPlayers()) {
            QuestProgress questProgress = farmer.getActiveQuestProgress();
            if(questProgress == null || !questProgress.getQuest().equals(this)) continue;

            farmer.dropQuest(false);
            farmer.tell(FarmQuest.getInstance().languageUtil.getString("quest_was_deleted"));
        }

        // Unlink the quest from farms
        for(Farm farm : Farm.getRegisteredFarms()) {
            if(!farm.getQuests().contains(this)) continue;
            farm.getQuests().remove(this);
        }
    }

    public void replaceWith(@NotNull Quest quest) {
        registeredQuests.remove(this);
        quest.register();

        // Make farmers who are progressing this quest drop it.
        for(Farmer farmer : Farmer.getPlayers()) {
            QuestProgress questProgress = farmer.getActiveQuestProgress();
            if(questProgress == null || !questProgress.getQuest().equals(this)) continue;

            farmer.dropQuest(false);
            farmer.tell(FarmQuest.getInstance().languageUtil.getString("quest_was_deleted"));
        }

        // Replace the quest in farms with new quest.
        for(Farm farm : Farm.getRegisteredFarms()) {
            if(!farm.getQuests().contains(this)) continue;
            farm.getQuests().remove(this);
            farm.getQuests().add(quest);
        }
    }

    public @NotNull Material getIcon() {
        return icon != null ? icon : getFarmBlock().getSeedMaterial();
    }

    public void setIcon(@Nullable Material icon) {
        this.icon = icon == null || !icon.isItem() ? null : icon;
    }

    /**
     * Create a fresh QuestProgress object from a Farm.<br>
     * It will return <code>null</code> in case the quest isn't linked to the given Farm.
     * @param farm Farm
     * @return QuestProgress or null
     */
    @Nullable
    public QuestProgress freshProgress(@NotNull Farm farm) {
        if(!farm.getQuests().contains(this)) {
            return null;
        }
        return new QuestProgress(this, farm, 0, Instant.now().getEpochSecond());
    }



    //

    @Getter
    static private final Collection<Quest> registeredQuests = new ArrayList<>();

    /**
     * Get Quest from id.
     * @param id Id
     * @return Quest if found, or <code>null</code>
     */
    @Nullable
    static public Quest getQuest(@NotNull String id) {
        return registeredQuests.stream()
                .filter(q -> q.getId().equals(id))
                .findFirst().orElse(null);
    }
}
