package org.nandayo.farmquest.service;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.nandayo.DAPI.HexUtil;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.player.FarmPlayer;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.model.quest.QuestProgress;

import java.time.Instant;
import java.util.ArrayList;

public class BossBarRunnable {

    private final FarmQuest plugin;
    private BukkitRunnable runnable;
    public BossBarRunnable(@NotNull FarmQuest plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if(this.runnable != null) return;
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for(FarmPlayer farmPlayer : new ArrayList<>(FarmPlayer.getPlayers())) {
                    Player player = farmPlayer.getOfflinePlayer().getPlayer();
                    if(player == null) continue;

                    QuestProgress questProgress = farmPlayer.getActiveQuestProgress();
                    if(questProgress == null) {
                        if(farmPlayer.getBossBar() != null) {
                            farmPlayer.getBossBar().removeAll();
                            Bukkit.removeBossBar(farmPlayer.getBossBar().getKey());
                            farmPlayer.setBossBar(null);
                        }
                        continue;
                    }

                    BossBar bossBar = farmPlayer.getBossBarOrCreate();
                    Quest quest = questProgress.getQuest();
                    int remained = (int) (quest.getObjective().getTimeLimit() - (Instant.now().getEpochSecond() - questProgress.getStartTime()));
                    bossBar.setTitle(HexUtil.parse(String.format("{TITLE}%s{WHITE}: {SUCCESS}%d {WHITE}out of {STAR}%d {WHITE}done! [{WARN}%s{WHITE}]",
                            quest.getName(), questProgress.getProgress(), quest.getObjective().getTargetAmount(), FarmQuest.getInstance().formatTime(remained))));
                }
            }
        };
        this.runnable.runTaskTimer(plugin, 20L, 3*20L);
    }

    public void stop() {
        if(this.runnable == null) return;
        this.runnable.cancel();
        this.runnable = null;
    }
}
