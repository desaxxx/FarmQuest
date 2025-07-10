package org.nandayo.farmquest.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.Util;
import org.nandayo.dapi.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.quest.Quest;

public class RemoveCompletedCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.command.removecompleted")) {
            plugin.tell(sender, plugin.languageUtil.getString("command.no_perm"));
            return true;
        }
        if(args.length < 3) {
            this.sendMissingArgsMsg(sender, s, args, "<questId> <player>");
            return true;
        }
        String id = args[1];
        Quest quest = Quest.getQuest(id);
        if(quest == null) {
            Util.log("Quest with id '" + id + "' does not exist!");
            return true;
        }
        Player player = Bukkit.getPlayer(args[2]);
        if(player == null) {
            plugin.tell(sender, plugin.languageUtil.getString("command.player_not_found").replace("{player}", args[2]));
            return true;
        }
        Farmer farmer = Farmer.getPlayer(player);
        if(farmer == null) {
            plugin.tell(sender, plugin.languageUtil.getString("not_a_farmer_player_other").replace("{player}", player.getName()));
            return true;
        }
        if(!farmer.getCompletedQuests().contains(quest.getId())) {
            plugin.tell(sender, plugin.languageUtil.getString("command.remove_completed.not_completed"));
            return true;
        }

        farmer.getCompletedQuests().remove(quest.getId());
        plugin.tell(sender, plugin.languageUtil.getString("command.remove_completed.success").replace("{player}", player.getName()));
        return true;
    }
}
