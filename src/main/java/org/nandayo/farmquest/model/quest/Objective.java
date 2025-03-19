package org.nandayo.farmquest.model.quest;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@Getter
public class Objective {

    private final ObjectiveType type;
    private final Material material;
    private final int targetAmount;
    private final long timeLimit;

    public Objective(@NotNull ObjectiveType type, @NotNull Material material, int targetAmount, long timeLimit) {
        this.type = type;
        this.material = material;
        this.targetAmount = targetAmount;
        this.timeLimit = timeLimit;
    }
}
