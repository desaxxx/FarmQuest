package org.nandayo.farmquest.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.menu.QuestCreatorMenu;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.model.quest.WritableQuest;

public class EditQuestCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.command.editquest")) {
            plugin.tell(sender, plugin.languageUtil.getString("command.no_perm"));
            return true;
        }
        if(!(sender instanceof Player player)) {
            plugin.tell(sender, plugin.languageUtil.getString("command.only_players"));
            return true;
        }
        if(args.length < 2) {
            plugin.tell(player, plugin.languageUtil.getString("command.edit_quest.missing_id"));
            return true;
        }
        String id = args[1];
        Quest quest = Quest.getQuest(id);
        if(quest == null) {
            plugin.tell(player, plugin.languageUtil.getString("command.quest_not_found").replace("{quest}", id));
            return true;
        }

        new QuestCreatorMenu(player, WritableQuest.fromQuest(quest));
        return true;
    }
}
