package org.nandayo.farmquest.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.DAPI.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.player.FarmPlayer;
import org.nandayo.farmquest.model.quest.Quest;

public class PickQuestCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!(sender instanceof Player player)) {
            plugin.tell(sender, "{WARN}Only players can use this command.");
            return true;
        }
        if(args.length < 2) {
            this.sendMissingArgsMsg(sender, s, args, "<questId>");
            return true;
        }

        String id = args[1];
        FarmPlayer farmPlayer = FarmPlayer.getPlayerOrThrow(player);
        Quest quest = Quest.getQuestOrThrow(id);
        farmPlayer.pickupQuest(quest);
        return true;
    }
}
