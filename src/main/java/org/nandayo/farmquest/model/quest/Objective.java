package org.nandayo.farmquest.model.quest;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.farmquest.enumeration.FarmBlock;
import org.nandayo.farmquest.model.Farmer;

import java.util.Collection;
import java.util.Locale;

@Getter
public class Objective {

    private final @NotNull ObjectiveType type;
    private final @NotNull FarmBlock farmBlock;
    private final int targetAmount;
    private final long timeLimit;
    private final @NotNull Collection<Reward> rewards;
    private final @NotNull QuestProperty questProperty;

    public Objective(@NotNull ObjectiveType type, @NotNull FarmBlock farmBlock, int targetAmount, long timeLimit, @NotNull Collection<Reward> rewards, @NotNull QuestProperty questProperty) {
        this.type = type;
        this.farmBlock = farmBlock;
        this.targetAmount = targetAmount;
        this.timeLimit = timeLimit;
        this.rewards = rewards;
        this.questProperty = questProperty;
    }

    @Nullable
    public Reward getReward(@NotNull Reward.RewardType type) {
        return rewards.stream().filter(r -> r.getType().equals(type)).findFirst().orElse(null);
    }

    public void grantRewards(@NotNull Farmer farmer) {
        for (Reward reward : rewards) {
            reward.grant(farmer);
        }
    }


    @Getter
    public enum ObjectiveType {
        HARVEST("Harvest", Material.IRON_HOE),
        PLANT("Plant", Material.FLOWER_POT),
        DELIVER("Deliver", Material.BUNDLE),
        ;

        ObjectiveType(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }

        private final String displayName;
        private final Material icon;

        @Nullable
        static public ObjectiveType get(@NotNull String name) {
            try {
                return ObjectiveType.valueOf(name.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
