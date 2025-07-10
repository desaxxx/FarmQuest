package org.nandayo.farmquest.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.Util;
import org.nandayo.dapi.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.menu.QuestCreatorMenu;
import org.nandayo.farmquest.model.quest.Quest;

public class CreateQuestCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.command.createquest")) {
            plugin.tell(sender, plugin.languageUtil.getString("command.no_perm"));
            return true;
        }
        if(!(sender instanceof Player player)) {
            plugin.tell(sender, plugin.languageUtil.getString("command.only_players"));
            return true;
        }
        String id = Util.generateRandomLowerCaseString(2);
        if(args.length >= 2) {
            if(!args[1].matches("[a-z0-9]{2}")) {
                plugin.tell(player, plugin.languageUtil.getString("command.create_quest.invalid_id"));
                return true;
            }else {
                id = args[1];
            }
        }
        if(Quest.getQuest(id) != null) {
            plugin.tell(player, plugin.languageUtil.getString("command.create_quest.already_exists").replace("{id}", id));
            return true;
        }

        new QuestCreatorMenu(player, id);
        return true;
    }
}
