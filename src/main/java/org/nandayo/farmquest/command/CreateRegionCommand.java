package org.nandayo.farmquest.command;

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
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.command.createregion")) {
            plugin.tell(sender, plugin.languageUtil.getString("command.no_perm"));
            return true;
        }
        if(!(sender instanceof Player player)) {
            plugin.tell(sender, plugin.languageUtil.getString("command.only_players"));
            return true;
        }
        Point[] points = plugin.playerMarkers.get(player);
        if(points == null || points[0] == null || points[1] == null) {
            plugin.tell(player, plugin.languageUtil.getString("command.create_region.missing_points"));
            return true;
        }
        String id = Util.generateRandomLowerCaseString(8);
        if(args.length >= 2) {
            if(!args[1].matches("[a-z0-9]{8}")) {
                plugin.tell(player, plugin.languageUtil.getString("command.create_region.invalid_id"));
                return true;
            }else {
                id = args[1];
            }
        }
        if(Farm.getFarm(id) != null) {
            plugin.tell(player, plugin.languageUtil.getString("command.create_region.already_exists").replace("{id}", id));
            return true;
        }

        FarmRegion region = new FarmRegion(points[0], points[1], player.getWorld().getName());
        Farm farm = new Farm(id, region);
        farm.register();
        plugin.tell(player, plugin.languageUtil.getString("command.create_region.success").replace("{id}", id));
        plugin.farmRegistry.save();
        return true;
    }
}
