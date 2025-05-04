package org.nandayo.farmquest.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;

public class ReloadCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.command.reload")) {
            plugin.tell(sender, plugin.languageUtil.getString("command.no_perm"));
            return true;
        }

        plugin.closeRegistry(true);
        plugin.tell(sender, plugin.languageUtil.getString("command.reload.success"));
        return true;
    }
}
