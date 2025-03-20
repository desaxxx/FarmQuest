package org.nandayo.farmquest.model.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.DAPI.Util;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.model.quest.QuestProgress;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Getter
public class FarmPlayer {

    private final UUID uuid;
    private QuestProgress activeQuestProgress;
    private final Collection<Quest> completedQuests;

    @Setter
    private KeyedBossBar bossBar;

    public FarmPlayer(@NotNull UUID uuid, QuestProgress activeQuestProgress, Collection<Quest> completedQuests) {
        this.uuid = uuid;
        this.activeQuestProgress = activeQuestProgress;
        this.completedQuests = new ArrayList<>(completedQuests);
    }

    public void register() {
        players.add(this);
    }
    public void unregister() {
        players.remove(this);
    }

    /**
     * Get OfflinePlayer of FarmPlayer.
     * @return OfflinePlayer
     */
    @NotNull
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public void tell(@NotNull String... message) {
        Player player = getOfflinePlayer().getPlayer();
        if(player == null) return;
        FarmQuest.getInstance().tell(player, message);
    }

    /**
     * Picks up a quest.
     * @param quest Quest
     */
    public void pickupQuest(@NotNull Quest quest) {
        if(this.activeQuestProgress == null) {
            QuestProgress zero = quest.freshProgress();
            this.activeQuestProgress = zero;
            this.activeQuestProgress.startTicking(() -> {
                if(this.activeQuestProgress == null || !activeQuestProgress.equals(zero)) return;

                dropQuest(false);
                tell("{WARN}You exceed the quest time limit.");
            });
            tell(String.format("{SUCCESS}Picked up quest '%s'.", quest.getName()));
        }else {
            tell("{WARN}You already have an active quest!");
        }
    }

    /**
     * Drops the active quest.
     */
    public void dropQuest(boolean completed) {
        if(completed) {
            this.completedQuests.add(this.activeQuestProgress.getQuest());
        }
        this.activeQuestProgress = null;
    }

    /**
     * Get BossBar, create if not found.
     * @return BossBar
     */
    @NotNull
    public KeyedBossBar getBossBarOrCreate() {
        if(this.bossBar == null) {
            this.bossBar = Bukkit.createBossBar(new NamespacedKey(FarmQuest.getInstance(), "bossbar_" + uuid), "Loading", BarColor.BLUE, BarStyle.SOLID);
            Player player = getOfflinePlayer().getPlayer();
            if(player != null) this.bossBar.addPlayer(player);
        }
        return this.bossBar;
    }



    public synchronized void save() {
        File file = new File(FarmQuest.getInstance().getDataFolder(), "players/" + uuid + ".yml");
        FileConfiguration config = new YamlConfiguration();

        config.set("active_quest.quest", this.activeQuestProgress == null ? null : this.activeQuestProgress.getQuest().getId());
        config.set("active_quest.progress", this.activeQuestProgress == null ? null : this.activeQuestProgress.getProgress());
        config.set("active_quest.start_time",  this.activeQuestProgress == null ? null : this.activeQuestProgress.getStartTime());
        config.set("completed_quests", this.completedQuests.stream().map(Quest::getId).toList());

        unregister();
        try {
            config.save(file);
        }catch (IOException e) {
            Util.log(String.format("{WARN}Couldn't save player data for '%s'.", uuid));
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    //

    @Getter
    static private final Collection<FarmPlayer> players = new ArrayList<>();

    /**
     * Get FarmPlayer from offline player.
     * @param offlinePlayer OfflinePlayer
     * @return FarmPlayer if found, or <code>null</code>
     */
    @Nullable
    static public FarmPlayer getPlayer(@NotNull OfflinePlayer offlinePlayer) {
        return players.stream()
                .filter(fp -> fp.getOfflinePlayer().equals(offlinePlayer))
                .findFirst().orElse(null);
    }

    /**
     * Get FarmPlayer from offline player.
     * @param offlinePlayer OfflinePlayer
     * @return FarmPlayer
     */
    @NotNull
    static public FarmPlayer getPlayerOrThrow(@NotNull OfflinePlayer offlinePlayer) {
        FarmPlayer farmPlayer = getPlayer(offlinePlayer);
        if(farmPlayer != null) return farmPlayer;
        else throw new NullPointerException("FarmPlayer is null.");
    }

    /**
     * Register a FarmPlayer from UUID.
     * @param uuid UUID
     */
    static public synchronized void load(@NotNull UUID uuid) {
        File file = new File(FarmQuest.getInstance().getDataFolder(), "players/" + uuid + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        Quest q = Quest.getQuest(config.getString("active_quest.quest",""));
        QuestProgress activeQuestProgress = null;
        if(q != null) {
            int progress = config.getInt("active_quest.progress",0);
            long startTime = config.getLong("active_quest.start_time",0);
            activeQuestProgress = new QuestProgress(q, progress, startTime);
        }
        Collection<Quest> completedQuests = config.getStringList("completed_quests").stream()
                .map(Quest::getQuest)
                .toList();

        new FarmPlayer(uuid, activeQuestProgress, completedQuests).register();
    }
}
