package org.nandayo.farmquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.nandayo.dapi.Util;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.enumeration.FarmBlock;
import org.nandayo.farmquest.enumeration.Setting;
import org.nandayo.farmquest.enumeration.VerticalGrowthType;
import org.nandayo.farmquest.event.FarmBlockBreakEvent;
import org.nandayo.farmquest.event.FarmBlockPlaceEvent;
import org.nandayo.farmquest.event.QuestCompleteEvent;
import org.nandayo.farmquest.event.QuestProgressEvent;
import org.nandayo.farmquest.model.BlockDataHolder;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.quest.Objective;
import org.nandayo.farmquest.model.quest.QuestProgress;
import org.nandayo.farmquest.util.FUtil;
import org.nandayo.farmquest.util.MaterialUtil;

public class CustomListener implements Listener {

    /**
     * This handler is for logic of breaking a FarmBlock
     * @param event FarmBlockBreakEvent
     */
    @EventHandler
    public void onFarmBlockBreak(FarmBlockBreakEvent event) {
        if(event.isCancelled()) return;

        Farmer farmer = event.getFarmer();
        if(farmer.getPlayer().isEmpty()) return;
        Player player = farmer.getPlayer().get();
        if(player.getGameMode() == GameMode.CREATIVE) return; // No progress in creative mode.

        Block block = event.getBlock();
        BlockData blockData = event.getBlockData();
        FarmBlock farmBlock = event.getFarmBlock();
        QuestProgress questProgress = event.getQuestProgress();
        int progress = 0;
        boolean isRoot;

        // Multi-block crops
        VerticalGrowthType growthType = VerticalGrowthType.get(blockData.getMaterial());
        if(growthType == null) {
            isRoot = true;
            if(FUtil.isReadyToHarvest(blockData)) progress++;
        }else {
            Util.log("Debug, growth type is not null");
            BlockFace face = growthType.getBlockFace();
            isRoot = !farmBlock.equals(block.getRelative(face.getOppositeFace()).getType());
            Block blockRelative = block;
            while(farmBlock.equals(blockRelative.getType())) {
                if(FUtil.isReadyToHarvest(blockRelative.getBlockData())) progress++;
                blockRelative = blockRelative.getRelative(face);
            }
        }

        // Save blockDataHolder if root.
        BlockDataHolder blockDataHolder = null;
        if(isRoot) {
            blockDataHolder = new BlockDataHolder(block, blockData);
        }

        if(progress > 0 && questProgress.getQuest().getType() == Objective.ObjectiveType.HARVEST) { // No progress in delivery objective.
            final int finalProgress = progress;
            Bukkit.getScheduler().runTask(FarmQuest.getInstance(), () ->
                    Bukkit.getPluginManager().callEvent(new QuestProgressEvent(farmer, questProgress, finalProgress)));
        }

        if(Setting.AUTO_PLANT.isEnabled() && isRoot) {
            final BlockDataHolder finalBdh = blockDataHolder;
            Bukkit.getScheduler().runTaskLater(FarmQuest.getInstance(), () -> {
                Block bdhBlock = finalBdh.getBlock();
                if(!(block.getType().isAir() || block.getType() == Material.WATER)) return;
                bdhBlock.setBlockData(finalBdh.getFormingBlockData());
            }, 6*20L);
        }
    }

    @EventHandler
    public void onFarmBlockPlace(FarmBlockPlaceEvent event) {
        if(event.isCancelled()) return;
        Farmer farmer = event.getFarmer();
        Player player = farmer.getOfflinePlayer().getPlayer();
        if(player == null || player.getGameMode() == GameMode.CREATIVE) return; // No progress in creative

        Block block = event.getBlock();
        BlockData blockData = event.getBlockData();
        QuestProgress questProgress = event.getQuestProgress();
        Bukkit.getScheduler().runTask(FarmQuest.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new QuestProgressEvent(farmer, questProgress, 1)));

        Material oldType = blockData.getMaterial();
        if(Setting.AUTO_REMOVE.isEnabled()) {
            Bukkit.getScheduler().runTaskLater(FarmQuest.getInstance(), () -> {
                if(block.getType() != oldType) return;
                block.setType(Material.AIR);
            }, 10*20L);
        }
    }

    @EventHandler
    public void onQuestProgress(QuestProgressEvent event) {
        Farmer farmer = event.getFarmer();
        QuestProgress questProgress = event.getQuestProgress();
        if(questProgress.plus(event.getProgress())) {
            Bukkit.getScheduler().runTask(FarmQuest.getInstance(), () ->
                    Bukkit.getPluginManager().callEvent(new QuestCompleteEvent(farmer, event.getQuestProgress())));
        }
    }

    @EventHandler
    public void onQuestComplete(QuestCompleteEvent event) {
        Farmer farmer = event.getFarmer();
        farmer.dropQuest(true);

        QuestProgress questProgress = event.getQuestProgress();
        Player player = farmer.getOfflinePlayer().getPlayer();
        if(questProgress.getQuest().getType() == Objective.ObjectiveType.DELIVER && player != null) {
            MaterialUtil.removeMaterials(player, questProgress.getQuest().getFarmBlock().getCropMaterial(), questProgress.getQuest().getTargetAmount());
        }
        questProgress.getQuest().grantRewards(farmer);
        farmer.tell(FarmQuest.getInstance().languageUtil.getString("quest_complete").replace("{quest}", questProgress.getQuest().getName()));
    }
}
