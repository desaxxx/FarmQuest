package org.nandayo.farmquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
import org.nandayo.dapi.model.Point;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.enumeration.FarmBlock;
import org.nandayo.farmquest.enumeration.Setting;
import org.nandayo.farmquest.event.FarmBlockBreakEvent;
import org.nandayo.farmquest.event.FarmBlockPlaceEvent;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.farm.FarmTool;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.quest.Objective;
import org.nandayo.farmquest.model.quest.QuestProgress;
import org.nandayo.farmquest.util.MessageUtil;

import java.util.Collection;

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
        /* FARM REGION FROM HERE */

        Material blockType = block.getType();
        Farmer farmer = Farmer.getPlayer(player);
        FarmBlock farmBlock = FarmBlock.get(blockType);
        QuestProgress questProgress = farmer == null ? null : farmer.getActiveQuestProgress();
        Objective.ObjectiveType objectiveType = questProgress != null ? questProgress.getQuest().getType() : null;
        if(farmer == null || questProgress == null || objectiveType == null || !farm.getQuests().contains(questProgress.getQuest())
                || !(objectiveType == Objective.ObjectiveType.HARVEST || objectiveType == Objective.ObjectiveType.DELIVER)
                || farmBlock == null || !farmBlock.equals(questProgress.getQuest().getFarmBlock())) {
            return;
        }

        FarmBlockBreakEvent farmBlockBreakEvent = new FarmBlockBreakEvent(farmer, block, farmBlock, farm, questProgress);
        Bukkit.getScheduler().runTask(FarmQuest.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(farmBlockBreakEvent));

        if(farmBlockBreakEvent.isCancelled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreakWithFarmTool(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if(e.isCancelled()) return;
        if(player.getGameMode() == GameMode.CREATIVE) return;

        ItemStack itemInUse = player.getInventory().getItemInMainHand();
        FarmTool tool = FarmTool.getTool(itemInUse);
        if(tool == null) return;

        Block block = e.getBlock();
        Collection<ItemStack> extraDrops = tool.getExtraDrops(block.getDrops(itemInUse));
        if(extraDrops.isEmpty()) return;

        for(ItemStack extraDrop : extraDrops) {
            block.getWorld().dropItemNaturally(block.getLocation(), extraDrop);
        }
        MessageUtil.actionBar(player, "{STAR}* {WHITE}Dropped extra materials!");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if(e.isCancelled()) return;
        Block block = e.getBlock();
        Farm farm = Farm.getFarm(block.getLocation());
        if(farm == null) return;
        // FARM REGION FROM HERE

        Material blockType = block.getType();
        Farmer farmer = Farmer.getPlayer(player);
        FarmBlock farmBlock = FarmBlock.get(blockType);
        QuestProgress questProgress = farmer == null ? null : farmer.getActiveQuestProgress();
        if(farmer == null || questProgress == null || !farm.getQuests().contains(questProgress.getQuest())
                || questProgress.getQuest().getType() != Objective.ObjectiveType.PLANT
                || farmBlock == null || !farmBlock.equals(questProgress.getQuest().getFarmBlock())) {
            return;
        }

        FarmBlockPlaceEvent farmBlockPlaceEvent = new FarmBlockPlaceEvent(farmer, block, farmBlock, farm, questProgress);
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
