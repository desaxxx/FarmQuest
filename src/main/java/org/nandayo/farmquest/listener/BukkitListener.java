package org.nandayo.farmquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.event.QuestProgressEvent;
import org.nandayo.farmquest.model.Farm;
import org.nandayo.farmquest.model.Point;
import org.nandayo.farmquest.model.player.FarmPlayer;
import org.nandayo.farmquest.model.quest.ObjectiveType;
import org.nandayo.farmquest.model.quest.QuestProgress;

public class BukkitListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        Block block = e.getClickedBlock();
        if (item == null || block == null) return;

        final FarmQuest plugin = FarmQuest.getInstance();
        if (!item.equals(plugin.FARM_MARKER)) return;

        e.setCancelled(true);
        Player player = e.getPlayer();
        Point[] points = plugin.playerMarkers.getOrDefault(player, new Point[2]);
        Point point = new Point(block.getLocation());
        switch (e.getAction()) {
            case LEFT_CLICK_BLOCK:
                points[0] = point;
                plugin.playerMarkers.put(player, points);
                plugin.tell(player, "{WHITE}You have selected 1st point.");
                break;

            case RIGHT_CLICK_BLOCK:
                points[1] = point;
                plugin.playerMarkers.put(player, points);
                plugin.tell(player, "{WHITE}You have selected 2nd point.");
                break;

        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        FarmPlayer.load(e.getPlayer().getUniqueId());
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        FarmPlayer farmPlayer = FarmPlayer.getPlayer(e.getPlayer());
        if(farmPlayer == null) return;
        farmPlayer.save();
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        FarmPlayer farmPlayer = FarmPlayer.getPlayer(player);
        if (farmPlayer == null) return;

        Block block = e.getBlock();
        Farm farm = Farm.getFarm(block.getLocation());
        if (farm == null) return;

        QuestProgress questProgress = farmPlayer.getActiveQuestProgress();
        if (questProgress == null || !farm.getQuests().contains(questProgress.getQuest())) return;

        if(questProgress.getQuest().getObjective().getType() == ObjectiveType.HARVEST && block.getType().equals(questProgress.getQuest().getObjective().getMaterial())) {
            if(block.getBlockData() instanceof Ageable ageable && ageable.getAge() < ageable.getMaximumAge()) return;

            Bukkit.getScheduler().runTask(FarmQuest.getInstance(), () ->
                    Bukkit.getPluginManager().callEvent(new QuestProgressEvent(farmPlayer, questProgress.getQuest(), farm)));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        FarmPlayer farmPlayer = FarmPlayer.getPlayer(player);
        if (farmPlayer == null) return;

        Block block = e.getBlock();
        Farm farm = Farm.getFarm(block.getLocation());
        if (farm == null) return;

        QuestProgress questProgress = farmPlayer.getActiveQuestProgress();
        if (questProgress == null || !farm.getQuests().contains(questProgress.getQuest())) return;

        if (questProgress.getQuest().getObjective().getType() == ObjectiveType.PLANT && block.getType().equals(questProgress.getQuest().getObjective().getMaterial())) {
            Bukkit.getScheduler().runTask(FarmQuest.getInstance(), () ->
                    Bukkit.getPluginManager().callEvent( new QuestProgressEvent(farmPlayer, questProgress.getQuest(), farm)));
        }
    }
}
