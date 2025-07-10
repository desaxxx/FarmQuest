package org.nandayo.farmquest.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.menu.FarmerMenu;
import org.nandayo.farmquest.model.farm.Farm;

public class FarmerMenuCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!(sender instanceof Player player)) {
            plugin.tell(sender, plugin.languageUtil.getString("command.only_players"));
            return true;
        }
        if(args.length < 2) {
            this.sendMissingArgsMsg(sender, s, args, "<farmId>");
            return true;
        }
        if(Farm.getRegisteredFarms().isEmpty()) {
            plugin.tell(player, plugin.languageUtil.getString("farms_empty"));
            return true;
        }
        Farm farm = Farm.getFarm(args[1]);
        if(farm == null) {
            plugin.tell(player, plugin.languageUtil.getString("command.farm_not_found").replace("{farm}", args[1]));
            return true;
        }

        new FarmerMenu(plugin, player, farm);
        return true;
    }
}
