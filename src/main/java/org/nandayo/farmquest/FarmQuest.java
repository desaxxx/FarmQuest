package org.nandayo.farmquest;

import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.DAPI;
import org.nandayo.dapi.HexUtil;
import org.nandayo.dapi.ItemCreator;
import org.nandayo.dapi.Util;
import org.nandayo.dapi.model.Point;
import org.nandayo.farmquest.command.MainCommand;
import org.nandayo.farmquest.listener.BukkitListener;
import org.nandayo.farmquest.listener.CustomListener;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.service.BossBarManager;
import org.nandayo.farmquest.service.registry.*;
import org.nandayo.farmquest.util.LanguageUtil;
import org.nandayo.farmquest.util.UpdateChecker;
import org.nandayo.farmquest.util.Wrapper;

import java.util.*;

public final class FarmQuest extends JavaPlugin {

    public final ItemStack FARM_MARKER = ItemCreator.of(Material.GOLDEN_AXE).name("&6Farm Marker").get();
    public final ItemStack FARM_DETECTOR = ItemCreator.of(Material.RABBIT_HIDE).get();
    public final HashMap<Player, Point[]> playerMarkers = new HashMap<>();

    @Getter
    static private FarmQuest instance;
    public BossBarManager bossBarManager;
    public QuestRegistry questRegistry;
    public FarmRegistry farmRegistry;
    public ConfigRegistry configRegistry;
    public GUIRegistry guiRegistry;
    public Wrapper wrapper;
    public LanguageUtil languageUtil;

    @Override
    public void onEnable() {
        instance = this;

        Objects.requireNonNull(getCommand("farmquest")).setExecutor(new MainCommand());

        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomListener(), this);

        if(!getDataFolder().exists()) //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();

        setupDAPI();

        wrapper = new Wrapper(this);

        doRegistry();

        bossBarManager = new BossBarManager(this);
        bossBarManager.start();

        new Metrics(this, 25233);
        new UpdateChecker(this, 124581).inform();
    }

    @Override
    public void onDisable() {
        closeRegistry(false);

        if(bossBarManager != null) bossBarManager.stop();

        instance = null;
    }

    private void setupDAPI() {
        DAPI.registerMenuListener();
        Util.PREFIX = "&8[<#008387>FarmQuest&8]&7 ";
        HexUtil.placeholders = new HashMap<>() {{
            put("{TITLE}", "<#008387>");
            put("{STAR}", "<#145f66>");
            put("{WHITE}", "<#ecfdff>");
            put("{WARN}", "<#e56666>");
            put("{SUCCESS}", "<#bfe769>");
        }};
        //noinspection deprecation
        DAPI.setMissingArgsMsg("{WARN}Usage: /{command} {current_args} {options}");
    }

    /**
     * Load database registries.
     */
    private void doRegistry() {
        // Order is important
        questRegistry = new QuestRegistry(this);
        questRegistry.load();
        farmRegistry = new FarmRegistry(this);
        farmRegistry.load();
        configRegistry = new ConfigRegistry(this);
        configRegistry.load();
        guiRegistry = new GUIRegistry(this);
        guiRegistry.load();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Farmer.load(player.getUniqueId());
        }

        languageUtil = new LanguageUtil(this, configRegistry.getConfig().getString("lang_file","en_US"));
    }

    /**
     * Save database registries.
     */
    public void closeRegistry(boolean reload) {
        // Order is important
        for (Farmer farmer : new ArrayList<>(Farmer.getPlayers())) {
            farmer.save();
        }
        farmRegistry.save();
        questRegistry.save();

        if(reload) {
            doRegistry();
        }
    }

    /**
     * Tell something.
     * @param receiver Receiver
     * @param message Message
     */
    public void tell(@NotNull CommandSender receiver, @NotNull String... message) {
        for(String m : message) {
            receiver.sendMessage(HexUtil.parse(Util.PREFIX + m));
        }
    }

    /**
     * Get time format of "hh:mm:ss".
     * @param time Seconds
     * @return String
     */
    public String formatTime(int time) {
        int sec = time;
        int min = sec / 60;
        int hour = min / 60;

        sec %= 60;
        min %= 60;
        hour %= 24;

        String str = String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
        return str.trim();
    }
}
