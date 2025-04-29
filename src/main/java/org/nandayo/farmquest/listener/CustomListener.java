package org.nandayo.farmquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.event.QuestCompleteEvent;
import org.nandayo.farmquest.event.QuestProgressEvent;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.quest.Objective;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.model.quest.QuestProgress;
import org.nandayo.farmquest.util.MaterialUtil;

public class CustomListener implements Listener {

    @EventHandler
    public void onQuestProgress(QuestProgressEvent event) {
        Farmer farmer = event.getFarmer();
        QuestProgress questProgress = farmer.getActiveQuestProgress();
        if(questProgress.plus(event.getProgress())) {
            Bukkit.getScheduler().runTask(FarmQuest.getInstance(), () ->
                    Bukkit.getPluginManager().callEvent(new QuestCompleteEvent(farmer, event.getQuest(), event.getFarm())));
        }
    }

    @EventHandler
    public void onQuestComplete(QuestCompleteEvent event) {
        Farmer farmer = event.getFarmer();
        farmer.dropQuest(true);

        Quest quest = event.getQuest();
        Player player = farmer.getOfflinePlayer().getPlayer();
        if(quest.getType() == Objective.ObjectiveType.DELIVER && player != null) {
            MaterialUtil.removeMaterials(player, quest.getFarmBlock().getCropMaterial(), quest.getTargetAmount());
        }
        quest.grantRewards(farmer);
        farmer.tell(String.format("{WHITE}You completed the Quest '%s'.", quest.getName()));
    }
}
