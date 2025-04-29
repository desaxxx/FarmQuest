package org.nandayo.farmquest.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.Util;
import org.nandayo.dapi.command.SubCommand;
import org.nandayo.dapi.model.Point;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.farm.FarmRegion;

public class CreateRegionCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.command.createregion")) {
            plugin.tell(sender, "{WARN}You don't have permission to use this command.");
            return true;
        }
        if(!(sender instanceof Player player)) {
            plugin.tell(sender, "{WARN}Only players can use this command.");
            return true;
        }
        Point[] points = plugin.playerMarkers.get(player);
        if(points == null || points[0] == null || points[1] == null) {
            plugin.tell(player, "{WARN}You have missing selection points.");
            return true;
        }
        String id = Util.generateRandomLowerCaseString(8);
        if(args.length >= 2) {
            if(!args[1].matches("[a-z0-9]{8}")) {
                plugin.tell(player, "{WARN}You must enter an id that matches [a-z0-9]{8}.");
                return true;
            }else {
                id = args[1];
            }
        }
        if(Farm.getFarm(id) != null) {
            plugin.tell(player, "{WARN}Farm with id '" + id + "' already exists.");
            return true;
        }

        FarmRegion region = new FarmRegion(points[0], points[1], player.getWorld().getName());
        Farm farm = new Farm(id, region);
        farm.register();
        plugin.tell(player, String.format("{SUCCESS}Farm was created with id %s", farm.getId()));
        plugin.farmRegistry.save();
        return true;
    }
}
