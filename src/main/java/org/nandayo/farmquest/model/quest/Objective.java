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

    private final ObjectiveType type;
    private final FarmBlock farmBlock;
    private final int targetAmount;
    private final long timeLimit;
    private final Collection<Reward> rewards;

    public Objective(@NotNull ObjectiveType type, @NotNull FarmBlock farmBlock, int targetAmount, long timeLimit, @NotNull Collection<Reward> rewards) {
        this.type = type;
        this.farmBlock = farmBlock;
        this.targetAmount = targetAmount;
        this.timeLimit = timeLimit;
        this.rewards = rewards;
    }

    public void grantRewards(@NotNull Farmer farmer) {
        for (Reward reward : rewards) {
            reward.grant(farmer);
        }
    }


    @Getter
    public enum ObjectiveType {
        HARVEST("Harvest"),
        PLANT("Plant"),
        DELIVER("Deliver"),
        ;

        ObjectiveType(String displayName) {
            this.displayName = displayName;
        }

        private final String displayName;

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
