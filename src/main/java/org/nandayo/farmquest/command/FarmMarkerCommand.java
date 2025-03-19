package org.nandayo.farmquest.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.DAPI.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;

public class FarmMarkerCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.farmmarker")) {
            plugin.tell(sender, "{WARN}You don't have permission to use this command.");
            return true;
        }

        if(!(sender instanceof Player player)) {
            plugin.tell(sender, "{WARN}Only players can use this command.");
            return true;
        }

        player.getInventory().addItem(plugin.FARM_MARKER);
        plugin.tell(player, "{SUCCESS}You got the Farm Marker.");
        return true;
    }
}
