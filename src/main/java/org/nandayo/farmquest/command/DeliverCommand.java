package org.nandayo.farmquest.command;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.DAPI.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.Farm;
import org.nandayo.farmquest.model.player.FarmPlayer;
import org.nandayo.farmquest.model.quest.ObjectiveType;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.model.quest.QuestProgress;
import org.nandayo.farmquest.util.Util;

public class DeliverCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!(sender instanceof Player player)) {
            plugin.tell(sender, "{WARN}Only players can use this command.");
            return true;
        }
        FarmPlayer farmPlayer = FarmPlayer.getPlayer(player);
        if (farmPlayer == null) {
            plugin.tell(player, "{WARN}You are not a farmer player.");
            return true;
        }
        QuestProgress questProgress = farmPlayer.getActiveQuestProgress();
        if (questProgress == null) {
            farmPlayer.tell("{WARN}You don't have an active quest.");
            return true;
        }
        Quest quest = questProgress.getQuest();
        if (quest.getObjective().getType() != ObjectiveType.DELIVER) {
            farmPlayer.tell("{WARN}Your quest is not a delivery.");
            return true;
        }
        if(!Util.hasMaterials(player, quest.getObjective().getMaterial(), quest.getObjective().getTargetAmount())) {
            farmPlayer.tell("{WARN}You don't have enough materials.");
            return true;
        }

        Util.removeMaterials(player, quest.getObjective().getMaterial(), quest.getObjective().getTargetAmount());
        farmPlayer.dropQuest(false);
        farmPlayer.tell(String.format("{WHITE}You completed the Quest '%s'.", quest.getName()));

        return true;
    }
}
