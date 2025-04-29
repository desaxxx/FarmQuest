package org.nandayo.farmquest;

import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import org.nandayo.farmquest.service.registry.FarmRegistry;
import org.nandayo.farmquest.service.registry.QuestRegistry;
import org.nandayo.farmquest.service.registry.ConfigRegistry;
import org.nandayo.farmquest.service.registry.ToolRegistry;
import org.nandayo.farmquest.util.Wrapper;

import java.util.*;

public final class FarmQuest extends JavaPlugin {

    public final ItemStack FARM_MARKER = ItemCreator.of(Material.GOLDEN_AXE).name("&6Farm Marker").get();
    public final ItemStack FARM_DETECTOR = ItemCreator.of(Material.RABBIT_HIDE).get();
    public final HashMap<Player, Point[]> playerMarkers = new HashMap<>();

    @Getter
    static private FarmQuest instance;
    public final Random random = new Random();
    public BossBarManager bossBarManager;
    public QuestRegistry questRegistry;
    public FarmRegistry farmRegistry;
    public ToolRegistry toolRegistry;
    public ConfigRegistry configRegistry;
    public Wrapper wrapper;

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

        doRegistry(true);

        bossBarManager = new BossBarManager(this);
        bossBarManager.start();

        new Metrics(this, 25233);
    }

    @Override
    public void onDisable() {
        closeRegistry();

        if(bossBarManager != null) bossBarManager.stop();

        instance = null;
    }

    private void setupDAPI() {
        DAPI dapi = new DAPI(this);
        dapi.registerMenuListener();
        Util.PREFIX = "&8[<#008387>FarmQuest&8]&7 ";
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
     */
    public void doRegistry(boolean includePlayers) {
        // Order is important
        questRegistry = new QuestRegistry(this);
        questRegistry.load();
        if(includePlayers) {
            Bukkit.getOnlinePlayers().forEach(p -> Farmer.load(p.getUniqueId()));
        }

        farmRegistry = new FarmRegistry(this);
        farmRegistry.load();
        toolRegistry = new ToolRegistry(this);
        toolRegistry.load();
        configRegistry = new ConfigRegistry(this);
        configRegistry.load();
    }

    /**
     * Save database registries.
     */
    private void closeRegistry() {
        // Order is important
        Bukkit.getOnlinePlayers().forEach(p -> {
            Farmer farmer = Farmer.getPlayer(p);
            if(farmer == null) return;
            farmer.save();
        });
        farmRegistry.save();
        questRegistry.save();
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

        StringBuilder sb = new StringBuilder();
        if(hour >= 10) sb.append(hour); else sb.append(0).append(hour);
        sb.append(":");
        if(min >= 10) sb.append(min); else sb.append(0).append(min);
        sb.append(":");
        if(sec >= 10) sb.append(sec); else sb.append(0).append(sec);
        return sb.toString().trim();
    }

    /**
     * Build an item stack from configuration section.
     * @return ItemStack
     */
    @Nullable
    public ItemStack buildItem(ConfigurationSection section) {
        if (section == null) return null;

        Material mat = Material.getMaterial(section.getString("material",""));
        if (mat == null) return null;

        String displayName = section.getString("display_name","");
        List<String> lore = section.getStringList("lore");
        boolean unbreakable = section.getBoolean("unbreakable",false);
        ItemCreator creator = ItemCreator.of(mat).name(displayName).lore(lore).unbreakable(unbreakable);

        for(String ecm : section.getStringList("enchantments")) {
            String[] split = ecm.split(",");
            if(split.length != 2) continue;

            @SuppressWarnings("deprecation") Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(split[0].toLowerCase(Locale.ENGLISH)));
            if(enchantment == null) continue;

            int level = Integer.parseInt(split[1]);
            creator.enchant(enchantment, level);
        }

        List<String> flags = section.getStringList("flags");
        if(flags.contains("ALL")) {
            creator.hideFlag(ItemFlag.values());
        }else {
            for(String flag : flags) {
                try {
                    creator.hideFlag(ItemFlag.valueOf(flag));
                }catch (IllegalArgumentException ignored) {}
            }
        }
        return creator.get();
    }
}
