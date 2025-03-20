package org.nandayo.farmquest.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.DAPI.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.menu.FarmEditorMenu;
import org.nandayo.farmquest.model.Farm;

public class FarmManagerCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!(sender instanceof Player player)) {
            plugin.tell(sender, "{WARN}Only players can execute this command");
            return true;
        }
        if(args.length < 2) {
            this.sendMissingArgsMsg(sender, s, args, "<farmId>");
            return true;
        }
        if(Farm.getRegisteredFarms().isEmpty()) {
            plugin.tell(player, "{WARN}Farms are empty or not loaded yet.");
            return true;
        }

        Farm farm = Farm.getFarmOrThrow(args[1]);
        new FarmEditorMenu(plugin).open(player, farm);
        return true;
    }
}
