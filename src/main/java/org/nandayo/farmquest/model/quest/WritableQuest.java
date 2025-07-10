package org.nandayo.farmquest.model.quest;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.dapi.Util;
import org.nandayo.farmquest.enumeration.FarmBlock;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class WritableQuest {

    private @NotNull Objective.ObjectiveType type = Objective.ObjectiveType.HARVEST;
    private @NotNull FarmBlock farmBlock = FarmBlock.CACTUS;
    private int targetAmount = 10;
    private long timeLimit = 60;
    private @NotNull Collection<Reward> rewards = new ArrayList<>();
    private @NotNull QuestProperty questProperty = new QuestProperty();

    private final @NotNull String id;
    private @NotNull String name;
    private @NotNull String description = "";
    private @Nullable Material icon;

    public WritableQuest(@NotNull String id) {
        this.id = id;
        name = "Quest #" + id;
    }

    public WritableQuest(@NotNull Objective.ObjectiveType type, @NotNull FarmBlock farmBlock, int targetAmount, long timeLimit, @NotNull Collection<Reward> rewards, @NotNull QuestProperty questProperty,
                         @NotNull String id, @NotNull String name, @NotNull String description, @NotNull Material icon) {
        this.type = type;
        this.farmBlock = farmBlock;
        this.targetAmount = targetAmount;
        this.timeLimit = timeLimit;
        this.rewards = rewards;
        this.questProperty = questProperty;
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public @NotNull Material getIcon() {
        return icon != null ? icon : farmBlock.getSeedMaterial();
    }

    public void setIcon(@Nullable Material icon) {
        this.icon = icon == null || !icon.isItem() ? null : icon;
    }

    public void setTargetAmount(int targetAmount) {
        this.targetAmount = Math.max(1, targetAmount);
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = Math.max(1, timeLimit);
    }

    /**
     * Get reward of given type from the Quest.
     * @param type RewardType
     * @return Reward if found, else {@code null}.
     */
    @Nullable
    public Reward getReward(@NotNull Reward.RewardType type) {
        return rewards.stream().filter(r -> r.getType().equals(type)).findFirst().orElse(null);
    }

    /**
     * Get reward of given type from the Quest. Create one if it doesn't exist.
     * @param type RewardType
     * @return Reward
     */
    @NotNull
    public Reward getRewardOrCreate(@NotNull Reward.RewardType type) {
        Reward reward = getReward(type);
        if(reward != null) return reward;
        reward = new Reward(type, new ArrayList<>());
        rewards.add(reward);
        return reward;
    }

    /**
     * Create Quest from WritableQuest.
     * @return New Quest if there is no registered Quest with this id, else {@code null}.
     */
    @Nullable
    public Quest createQuest() {
        if(Quest.getQuest(id) != null) {
            Util.log("Quest already exists with id '" + id + "'!");
            return null;
        }
        return new Quest(type, farmBlock, targetAmount, timeLimit, rewards, questProperty, id, name, description, icon);
    }

    /**
     * Override a Quest with this WritableQuest.
     * @param id Id of overridden Quest
     * @return Quest if there was a Quest with this id, else {@code null}.
     */
    @Nullable
    public Quest saveToQuest(@NotNull String id) {
        Quest oldQuest = Quest.getQuest(id);
        if(oldQuest == null) {
            Util.log("Quest with id '" + id + "' does not exist!");
            return null;
        }
        Quest newQuest = new Quest(type, farmBlock, targetAmount, timeLimit, rewards, questProperty, id, name, description, icon);
        oldQuest.replaceWith(newQuest);
        return newQuest;
    }

    /**
     * Override a Quest with this WritableQuest.
     * @return Quest if there was a Quest with id of WritableQuest, else {@code null}.
     */
    @Nullable
    public Quest saveToQuest() {
        return saveToQuest(id);
    }




    /**
     * Create a WritableQuest from given Quest.
     * @param quest Quest to copy
     * @return WritableQuest
     */
    @NotNull
    static public WritableQuest fromQuest(@NotNull Quest quest) {
        return new WritableQuest(quest.getType(), quest.getFarmBlock(), quest.getTargetAmount(), quest.getTimeLimit(), quest.getRewards(), quest.getQuestProperty(),
                quest.getId(), quest.getName(), quest.getDescription(), quest.getIcon());
    }
}
