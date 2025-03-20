package org.nandayo.farmquest.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.nandayo.farmquest.event.QuestProgressEvent;
import org.nandayo.farmquest.model.player.FarmPlayer;
import org.nandayo.farmquest.model.quest.QuestProgress;

public class FarmListener implements Listener {

    @EventHandler
    public void onQuestProgress(QuestProgressEvent event) {
        FarmPlayer farmPlayer = event.getFarmPlayer();
        QuestProgress questProgress = farmPlayer.getActiveQuestProgress();
        if(questProgress.plus()) {
            farmPlayer.dropQuest(true);
            farmPlayer.tell(String.format("{WHITE}You completed the Quest '%s'.", event.getQuest().getName()));
        }
    }
}
