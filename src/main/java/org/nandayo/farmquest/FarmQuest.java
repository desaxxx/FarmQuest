package org.nandayo.farmquest;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.nandayo.DAPI.DAPI;
import org.nandayo.DAPI.HexUtil;
import org.nandayo.DAPI.ItemCreator;
import org.nandayo.DAPI.Util;
import org.nandayo.farmquest.command.MainCommand;
import org.nandayo.farmquest.listener.BukkitEvents;
import org.nandayo.farmquest.model.Point;
import org.nandayo.farmquest.model.player.FarmPlayer;
import org.nandayo.farmquest.service.registry.FarmRegistry;
import org.nandayo.farmquest.service.registry.QuestRegistry;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public final class FarmQuest extends JavaPlugin {

    public final ItemStack FARM_MARKER = ItemCreator.of(Material.GOLDEN_AXE).name("&6Farm Marker").get();
    public final HashMap<Player, Point[]> playerMarkers = new HashMap<>();

    @Getter
    static private FarmQuest instance;
    public final Random random = new Random();
    public QuestRegistry questRegistry;
    public FarmRegistry farmRegistry;

    @Override
    public void onEnable() {
        instance = this;

        Objects.requireNonNull(getCommand("farmquest")).setExecutor(new MainCommand());
        Bukkit.getPluginManager().registerEvents(new BukkitEvents(), this);

        if(!getDataFolder().exists()) //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();

        setupDAPI();

        doRegistry(true);
    }

    @Override
    public void onDisable() {
        closeRegistry();

        instance = null;
    }

    private void setupDAPI() {
        DAPI dapi = new DAPI(this);
        dapi.registerMenuListener();
        Util.PREFIX = "&8[<#008387>FarmQuest&8] ";
        HexUtil.placeholders = new HashMap<>() {{
            put("{TITLE}", "<#008387>");
            put("{STAR}", "<#145f66>");
            put("{WHITE}", "<#ecfdff>");
            put("{WARN}", "<#e56666>");
            put("{SUCCESS}", "<#bfe769>");
        }};
        dapi.setMissingArgsMsg("{WARN}Usage: /{label} {current_args} {options}");
    }

    /**
     * Load database registries.
     * Order is IMPORTANT.
     */
    public void doRegistry(boolean includePlayers) {
        // Order important
        questRegistry = new QuestRegistry();
        questRegistry.load();
        if(includePlayers) {
            Bukkit.getOnlinePlayers().forEach(p -> FarmPlayer.load(p.getUniqueId()));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                farmRegistry = new FarmRegistry();
                farmRegistry.load();
            }
        }.runTaskLater(this, 5*20L);
    }

    /**
     * Save database registries.
     */
    private void closeRegistry() {
        // Order important
        Bukkit.getOnlinePlayers().forEach(p -> {
            FarmPlayer farmPlayer = FarmPlayer.getPlayer(p);
            if(farmPlayer == null) return;
            farmPlayer.save();
        });
        questRegistry.save();
        farmRegistry.save();
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
     * Broadcast something.
     * @param message Message
     */
    public void broadcast(@NotNull String message) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            tell(player, message);
        }
        tell(Bukkit.getConsoleSender(), message);
    }
}
