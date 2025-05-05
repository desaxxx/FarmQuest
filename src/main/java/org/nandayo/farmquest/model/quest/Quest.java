package org.nandayo.farmquest.model.quest;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.dapi.Util;
import org.nandayo.farmquest.enumeration.FarmBlock;
import org.nandayo.farmquest.model.farm.Farm;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

@Getter
public class Quest extends Objective {

    // objective
    // timer
    private final @NotNull String id;
    private final @NotNull String name;
    private final @NotNull String description;
    private final @NotNull Material icon;

    public Quest(@NotNull ObjectiveType type, @NotNull FarmBlock farmBlock, int targetAmount, long timeLimit, @NotNull Collection<Reward> rewards,
                 @NotNull String id, @NotNull String name, @NotNull String description, @NotNull Material icon) {
        super(type, farmBlock, targetAmount, timeLimit, rewards);
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
    public Quest(@NotNull ObjectiveType type, @NotNull FarmBlock farmBlock, int targetAmount, long timeLimit, @NotNull Collection<Reward> rewards,
                 @NotNull String name, @NotNull String description, @NotNull Material icon) {
        this(type, farmBlock, targetAmount, timeLimit,  rewards, Util.generateRandomLowerCaseString(2), name, description, icon);
    }

    public void register() {
        if(getQuest(id) == null) {
            registeredQuests.add(this);
        }else {
            Util.log(String.format("{WARN}Quest with id '%s' was already registered.", id));
        }
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

    /**
     * Get Quest from id.
     * @param id Id
     * @return Quest
     */
    static public Quest getQuestOrThrow(@NotNull String id) {
        Quest quest = getQuest(id);
        if(quest != null) return quest;
        else throw new NullPointerException("Quest is null.");
    }
}
