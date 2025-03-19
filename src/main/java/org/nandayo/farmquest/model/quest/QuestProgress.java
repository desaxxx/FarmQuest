package org.nandayo.farmquest.model.quest;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.nandayo.farmquest.FarmQuest;

import java.time.Instant;

@Getter
public class QuestProgress {

    private final Quest quest;
    private int progress;
    private final long startTime;
    private BukkitRunnable tickingTask = null;

    public QuestProgress(@NotNull Quest quest, int progress, long startTime) {
        this.quest = quest;
        this.progress = progress;
        this.startTime = startTime;
    }

    /**
     * Increase progress.
     * @param amount amount
     * @return whether completed.
     */
    public boolean plus(int amount) {
        this.progress += Math.max(0, amount);
        return this.progress >= quest.getObjective().getTargetAmount();
    }

    /**
     * Increase progress by 1.
     * @return whether completed.
     */
    public boolean plus() {
        return plus(1);
    }

    /**
     * Decrease progress.
     * @param amount amount
     * @return whether completed.
     */
    public boolean minus(int amount) {
        this.progress -= Math.max(0, amount);
        return this.progress >= quest.getObjective().getTargetAmount();
    }

    /**
     * Decrease progress by 1.
     * @return whether completed.
     */
    public boolean minus() {
        return minus(1);
    }

    /**
     * Start ticking for quest progress tracking.
     */
    public void startTicking(Runnable callbackOnTimeLimit) {
        if(tickingTask != null) return;

        tickingTask = new BukkitRunnable() {
            @Override
            public void run() {
                long elapsed = Instant.now().getEpochSecond() - startTime;
                if(elapsed > quest.getObjective().getTimeLimit()) {
                    cancel();
                    callbackOnTimeLimit.run();
                    tickingTask = null;
                }
            }
        };
        tickingTask.runTaskTimer(FarmQuest.getInstance(), 0, 20L);
    }

    /**
     * Check if it's ticking.
     * @return whether time limit was exceed.
     */
    public boolean isTicking() {
        return tickingTask != null;
    }
}
