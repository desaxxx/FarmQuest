package org.nandayo.farmquest.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.event.QuestCompleteEvent;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.quest.Objective;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.model.quest.QuestProgress;
import org.nandayo.farmquest.util.MaterialUtil;

public class DeliverCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!(sender instanceof Player player)) {
            plugin.tell(sender, "{WARN}Only players can use this command.");
            return true;
        }
        if(args.length < 2) {
            this.sendMissingArgsMsg(sender, s, args, "<farmId>");
            return true;
        }
        if(Farm.getRegisteredFarms().isEmpty()) {
            plugin.tell(player, "{WARN}Farms are empty or not loaded yet.");
            return true;
        }
        Farmer farmer = Farmer.getPlayer(player);
        if (farmer == null) {
            plugin.tell(player, "{WARN}You are not a farmer player.");
            return true;
        }
        QuestProgress questProgress = farmer.getActiveQuestProgress();
        if (questProgress == null) {
            farmer.tell("{WARN}You don't have an active quest.");
            return true;
        }
        Quest quest = questProgress.getQuest();
        if (quest.getType() != Objective.ObjectiveType.DELIVER) {
            farmer.tell("{WARN}Your quest is not a delivery.");
            return true;
        }
        Farm farm = Farm.getFarmOrThrow(args[1]);
        if(!farm.getQuests().contains(quest)) {
            farmer.tell(String.format("{WARN}Your quest does not belong to Farm '%s'", farm.getId()));
            return true;
        }
        if(!MaterialUtil.hasMaterials(player, quest.getFarmBlock().getCropMaterial(), quest.getTargetAmount())) {
            farmer.tell("{WARN}You don't have enough materials.");
            return true;
        }

        Bukkit.getScheduler().runTask(plugin, () ->
                Bukkit.getPluginManager().callEvent(new QuestCompleteEvent(farmer, quest, farm)));

        return true;
    }
}
