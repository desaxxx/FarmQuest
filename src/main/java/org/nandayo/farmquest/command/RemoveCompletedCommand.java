package org.nandayo.farmquest.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.quest.Quest;

public class RemoveCompletedCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.command.removecompleted")) {
            plugin.tell(sender, "{WARN}You don't have permission to use this command.");
            return true;
        }
        if(args.length < 3) {
            this.sendMissingArgsMsg(sender, s, args, "<questId> <player>");
            return true;
        }
        Quest quest = Quest.getQuestOrThrow(args[1]);
        Player player = Bukkit.getPlayer(args[2]);
        if(player == null) {
            plugin.tell(sender, "{WARN}Player was not found!");
            return true;
        }
        Farmer farmer = Farmer.getPlayerOrThrow(player);
        if(!farmer.getCompletedQuests().contains(quest)) {
            plugin.tell(sender, "{WARN}This player didn't complete the quest!");
            return true;
        }

        farmer.getCompletedQuests().remove(quest);
        plugin.tell(sender, String.format("{SUCCESS}Removed the quest from completed quests of player '%s'!", player.getName()));
        return true;
    }
}
