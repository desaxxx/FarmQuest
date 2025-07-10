package org.nandayo.farmquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.nandayo.dapi.Util;
import org.nandayo.dapi.model.Point;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.enumeration.FarmBlock;
import org.nandayo.farmquest.enumeration.Setting;
import org.nandayo.farmquest.event.FarmBlockBreakEvent;
import org.nandayo.farmquest.event.FarmBlockPlaceEvent;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.quest.Objective;
import org.nandayo.farmquest.model.quest.QuestProgress;
import org.nandayo.farmquest.model.quest.QuestProperty;

public class BukkitListener implements Listener {

    @EventHandler
    public void onFarmSelect(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        Block block = e.getClickedBlock();
        if (item == null || block == null) return;

        final FarmQuest plugin = FarmQuest.getInstance();
        if (!item.isSimilar(plugin.FARM_MARKER)) return;

        e.setCancelled(true);
        Location location = block.getLocation().clone();
        Player player = e.getPlayer();
        Point[] points = plugin.playerMarkers.getOrDefault(player, new Point[2]);
        Point point = new Point(location);
        switch (e.getAction()) {
            case LEFT_CLICK_BLOCK:
                points[0] = point;
                plugin.playerMarkers.put(player, points);
                plugin.tell(player, plugin.languageUtil.getString("select_1st_point")
                        .replace("{x}", String.format("%.1f", location.getX()))
                        .replace("{y}", String.format("%.1f", location.getY()))
                        .replace("{z}", String.format("%.1f", location.getZ())));
                break;

            case RIGHT_CLICK_BLOCK:
                points[1] = point;
                plugin.playerMarkers.put(player, points);
                plugin.tell(player, plugin.languageUtil.getString("select_2nd_point")
                        .replace("{x}", String.format("%.1f", location.getX()))
                        .replace("{y}", String.format("%.1f", location.getY()))
                        .replace("{z}", String.format("%.1f", location.getZ())));
                break;

        }
    }

    @EventHandler
    public void onFarmDetect(PlayerInteractEvent e) {
        if(e.getAction() == Action.PHYSICAL) return;

        ItemStack item = e.getItem();
        Block block = e.getClickedBlock();
        if (item == null || block == null) return;

        final FarmQuest plugin = FarmQuest.getInstance();
        if(!item.isSimilar(plugin.FARM_DETECTOR)) return;

        Farm farm = Farm.getFarm(block.getLocation());
        if (farm == null) return;

        e.setCancelled(true);
        plugin.tell(e.getPlayer(), plugin.languageUtil.getString("farm_detected").replace("{farm}", farm.getId()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Farmer.load(e.getPlayer().getUniqueId());
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Farmer farmer = Farmer.getPlayer(e.getPlayer());
        if(farmer == null) return;
        farmer.save();
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if(e.isCancelled()) return; // WorldGuard has EventPriority.HIGH so it will be cancelled beforehand.

        Block block = e.getBlock();
        Farm farm = Farm.getFarm(block.getLocation());
        if (farm == null) return;
        /* FARM REGION FROM HERE ON */

        boolean protecting = Setting.PROTECT_FARM_REGION.isEnabled() && !player.hasPermission("farmquest.protect.bypass");
        Material blockType = block.getType();
        Farmer farmer = Farmer.getPlayer(player);
        FarmBlock farmBlock = FarmBlock.get(blockType);
        QuestProgress questProgress = farmer == null ? null : farmer.getActiveQuestProgress();
        Objective.ObjectiveType objectiveType = questProgress != null ? questProgress.getQuest().getType() : null;
        if(farmer == null || questProgress == null || !farm.getQuests().contains(questProgress.getQuest())
                || !(objectiveType == Objective.ObjectiveType.HARVEST || objectiveType == Objective.ObjectiveType.DELIVER)
                || farmBlock == null || !farmBlock.equals(questProgress.getQuest().getFarmBlock())) {
            if(protecting) {
                e.setCancelled(true);
            }
            return;
        }

        FarmBlockBreakEvent farmBlockBreakEvent = new FarmBlockBreakEvent(farmer, block, block.getBlockData().clone(), farmBlock, farm, questProgress);
        Bukkit.getScheduler().runTask(FarmQuest.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(farmBlockBreakEvent));

        if(farmBlockBreakEvent.isCancelled()) {
            Util.log("Debug, its cancelled");
            e.setCancelled(true);
            return;
        }

        QuestProperty questProperty = questProgress.getQuest().getQuestProperty();
        if(questProperty.NO_BLOCK_DROPS.isEnabled() && objectiveType == Objective.ObjectiveType.HARVEST) {
            e.setDropItems(false);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if(e.isCancelled()) return;
        Block block = e.getBlock();
        Farm farm = Farm.getFarm(block.getLocation());
        if(farm == null) return;
        // FARM REGION FROM HERE

        boolean protecting = Setting.PROTECT_FARM_REGION.isEnabled() && !player.hasPermission("farmquest.protect.bypass");
        Material blockType = block.getType();
        Farmer farmer = Farmer.getPlayer(player);
        FarmBlock farmBlock = FarmBlock.get(blockType);
        QuestProgress questProgress = farmer == null ? null : farmer.getActiveQuestProgress();
        if(farmer == null || questProgress == null || !farm.getQuests().contains(questProgress.getQuest())
                || questProgress.getQuest().getType() != Objective.ObjectiveType.PLANT
                || farmBlock == null || !farmBlock.equals(questProgress.getQuest().getFarmBlock())) {
            if (protecting) {
                e.setCancelled(true);
            }
            return;
        }

        FarmBlockPlaceEvent farmBlockPlaceEvent = new FarmBlockPlaceEvent(farmer, block, block.getBlockData().clone(), farmBlock, farm, questProgress);
        Bukkit.getScheduler().runTask(FarmQuest.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(farmBlockPlaceEvent));

        if(farmBlockPlaceEvent.isCancelled()) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onFarmlandInteract(EntityInteractEvent e) {
        if(e.getBlock().getType() != Material.FARMLAND || !Setting.PROTECT_FARMLAND.isEnabled()) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onFarmlandInteract2(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.FARMLAND || e.getAction() != Action.PHYSICAL || !Setting.PROTECT_FARMLAND.isEnabled()) return;
        e.setCancelled(true);
    }
}
