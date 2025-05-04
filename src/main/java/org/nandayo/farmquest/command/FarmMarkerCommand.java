package org.nandayo.farmquest.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;

public class FarmMarkerCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.command.farmmarker")) {
            plugin.tell(sender, plugin.languageUtil.getString("command.no_perm"));
            return true;
        }

        if(!(sender instanceof Player player)) {
            plugin.tell(sender, plugin.languageUtil.getString("command.only_players"));
            return true;
        }

        player.getInventory().addItem(plugin.FARM_MARKER);
        plugin.tell(player, plugin.languageUtil.getString("command.farm_marker.get_marker"));
        return true;
    }
}
