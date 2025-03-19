package org.nandayo.farmquest.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.nandayo.DAPI.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;

public class ReloadCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.reload")) {
            plugin.tell(sender, "{WARN}You don't have permission to use this command.");
            return true;
        }

        plugin.doRegistry(false);
        plugin.tell(sender, "{SUCCESS}Configurations have been reloaded!");
        return true;
    }
}
