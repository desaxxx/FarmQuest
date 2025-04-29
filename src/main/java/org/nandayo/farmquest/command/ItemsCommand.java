package org.nandayo.farmquest.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.menu.ItemsMenu;

public class ItemsCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.command.items")) {
            plugin.tell(sender, "{WARN}You don't have permission to use this command!");
            return true;
        }
        if(!(sender instanceof Player player)) {
            plugin.tell(sender, "{WARN}Only players can use this command!");
            return true;
        }

        new ItemsMenu(plugin).open(player);
        plugin.tell(player, "{SUCCESS}You opened items menu.");
        return true;
    }
}
