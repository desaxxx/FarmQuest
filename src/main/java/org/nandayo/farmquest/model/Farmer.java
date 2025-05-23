package org.nandayo.farmquest.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.dapi.ItemCreator;
import org.nandayo.dapi.Util;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.quest.Objective;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.model.quest.QuestProgress;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Farmer {

    private final @NotNull UUID uuid;
    private @Nullable QuestProgress activeQuestProgress;
    private final @NotNull Collection<Quest> completedQuests;

    @Setter
    private @Nullable KeyedBossBar bossBar;

    public Farmer(@NotNull UUID uuid, @Nullable QuestProgress activeQuestProgress, @NotNull Collection<Quest> completedQuests) {
        this.uuid = uuid;
        this.activeQuestProgress = activeQuestProgress;
        this.completedQuests = new ArrayList<>(completedQuests);
    }

    public void register() {
        if(getPlayer(uuid) == null) {
            players.add(this);
        }else {
            Util.log(String.format("{WARN}Player with UUID '%s' was already registered.", uuid));
        }
    }
    public void unregister() {
        players.remove(this);
        KeyedBossBar bossBar = getBossBar();
        Player player = getOfflinePlayer().getPlayer();
        if(bossBar != null && player != null) {
            bossBar.removePlayer(player);
            Bukkit.removeBossBar(bossBar.getKey());
        }
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
    public void pickupQuest(@NotNull Quest quest, @NotNull Farm farm) {
        FarmQuest plugin = FarmQuest.getInstance();
        Player player = getOfflinePlayer().getPlayer();
        if(this.activeQuestProgress != null) {
            tell(plugin.languageUtil.getString("have_active_quest"));
            if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            return;
        }
        if(this.completedQuests.contains(quest)) {
            tell(plugin.languageUtil.getString("quest_already_completed"));
            if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            return;
        }
        QuestProgress freshProgress = quest.freshProgress(farm);
        if(freshProgress == null) {
            tell(plugin.languageUtil.getString("quest_is_not_linked_to_farm").replace("{farm}", farm.getId()));
            if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            return;
        }

        this.activeQuestProgress = freshProgress;
        if(quest.getType() == Objective.ObjectiveType.PLANT) {
            if(player != null) {
                ItemStack seeds = ItemCreator.of(quest.getFarmBlock().getSeedMaterial()).amount(quest.getTargetAmount()).get();
                player.getInventory().addItem(seeds);
            }
        }
        this.activeQuestProgress.startTicking(() -> {
            if(this.activeQuestProgress == null || !activeQuestProgress.getQuest().equals(quest)) return;

            dropQuest(false);
            tell(plugin.languageUtil.getString("exceed_quest_time_limit"));
        });
        tell(plugin.languageUtil.getString("pickup_quest").replace("{quest}", quest.getName()));
        if(player != null) player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
    }

    public void continueActiveQuest() {
        if(this.activeQuestProgress == null || this.activeQuestProgress.isTicking()) return;

        Quest quest = this.activeQuestProgress.getQuest();
        this.activeQuestProgress.startTicking(() -> {
            if(this.activeQuestProgress == null || !activeQuestProgress.getQuest().equals(quest)) return;

            dropQuest(false);
            tell(FarmQuest.getInstance().languageUtil.getString("exceed_quest_time_limit"));
            Player player = getOfflinePlayer().getPlayer();
            if(player != null) player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 1f);
        });
    }

    /**
     * Drops the active quest.
     */
    public void dropQuest(boolean completed) {
        if(this.activeQuestProgress == null) return;
        Player player = getOfflinePlayer().getPlayer();
        if(completed) {
            this.completedQuests.add(this.activeQuestProgress.getQuest());
            if(player != null) player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }else {
            if(player != null) player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
        }
        this.activeQuestProgress.stopTicking();
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



    public void save() {
        if(this.activeQuestProgress != null) {
            this.activeQuestProgress.stopTicking();
        }

        File file = new File(FarmQuest.getInstance().getDataFolder(), "players/" + uuid + ".yml");
        FileConfiguration config = new YamlConfiguration();

        config.set("active_quest.quest", this.activeQuestProgress == null ? null : this.activeQuestProgress.getQuest().getId());
        config.set("active_quest.farm", this.activeQuestProgress == null ? null : this.activeQuestProgress.getFarm().getId());
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
    static private final Collection<Farmer> players = new ArrayList<>();

    /**
     * Get FarmPlayer from UUID.
     * @param uuid UUID
     * @return FarmPlayer if found, or <code>null</code>
     */
    @Nullable
    static public Farmer getPlayer(@NotNull UUID uuid) {
        return players.stream()
                .filter(fp -> fp.getUuid().equals(uuid))
                .findFirst().orElse(null);
    }

    /**
     * Get FarmPlayer from offline player.
     * @param offlinePlayer OfflinePlayer
     * @return FarmPlayer if found, or <code>null</code>
     */
    @Nullable
    static public Farmer getPlayer(@NotNull OfflinePlayer offlinePlayer) {
        return players.stream()
                .filter(fp -> fp.getOfflinePlayer().equals(offlinePlayer))
                .findFirst().orElse(null);
    }

    /**
     * Register a FarmPlayer from UUID.
     * @param uuid UUID
     */
    static public void load(@NotNull UUID uuid) {
        File file = new File(FarmQuest.getInstance().getDataFolder(), "players/" + uuid + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        Quest quest = Quest.getQuest(config.getString("active_quest.quest",""));
        Farm farm = Farm.getFarm(config.getString("active_quest.farm",""));
        QuestProgress activeQuestProgress = null;
        if(quest != null && farm != null) {
            int progress = config.getInt("active_quest.progress",0);
            long startTime = config.getLong("active_quest.start_time",0);
            activeQuestProgress = new QuestProgress(quest, farm, progress, startTime);
        }
        Collection<Quest> completedQuests = config.getStringList("completed_quests").stream()
                .map(Quest::getQuest)
                .filter(Objects::nonNull)
                .toList();

        Farmer farmer = new Farmer(uuid, activeQuestProgress, completedQuests);
        farmer.register();
        farmer.continueActiveQuest();
    }
}
