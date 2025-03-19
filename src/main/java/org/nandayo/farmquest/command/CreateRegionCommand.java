package org.nandayo.farmquest.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.DAPI.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.Farm;
import org.nandayo.farmquest.model.FarmRegion;
import org.nandayo.farmquest.model.Point;

public class CreateRegionCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.createregion")) {
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

        FarmRegion region = new FarmRegion(points[0], points[1], player.getWorld());
        Farm farm = new Farm(region);
        farm.register();
        plugin.tell(player, String.format("{SUCCESS}Farm was created with id %s", farm.getId()));
        return true;
    }
}
