package org.nandayo.farmquest.service;

import org.bukkit.Bukkit;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.HexUtil;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.model.quest.QuestProgress;

import java.time.Instant;

public class BossBarManager {

    private final FarmQuest plugin;
    private BukkitRunnable runnable;
    public BossBarManager(@NotNull FarmQuest plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if(this.runnable != null) return;
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                long currentSeconds = Instant.now().getEpochSecond();
                for(Farmer farmer : Farmer.getPlayers()) {
                    Player player = farmer.getOfflinePlayer().getPlayer();
                    if(player == null) continue;

                    QuestProgress questProgress = farmer.getActiveQuestProgress();
                    KeyedBossBar bossBar = farmer.getBossBar();
                    if(questProgress == null) {
                        if(bossBar != null) {
                            bossBar.removePlayer(player);
                            Bukkit.removeBossBar(bossBar.getKey());
                            farmer.setBossBar(null);
                        }
                        continue;
                    }

                    if(bossBar == null) bossBar = farmer.getBossBarOrCreate();
                    Quest quest = questProgress.getQuest();
                    int remained = (int) (quest.getTimeLimit() - (currentSeconds - questProgress.getStartTime()));
                    final String title = HexUtil.parse(String.format("{TITLE}%s{WHITE}: {SUCCESS}%d {WHITE}out of {STAR}%d {WHITE}done! [{WARN}%s{WHITE}]",
                            quest.getName(), questProgress.getProgress(), quest.getTargetAmount(), FarmQuest.getInstance().formatTime(remained)));
                    bossBar.setTitle(title);
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
