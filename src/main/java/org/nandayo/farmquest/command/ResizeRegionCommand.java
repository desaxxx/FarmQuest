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

public class ResizeRegionCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.command.resizeregion")) {
            plugin.tell(sender, plugin.languageUtil.getString("command.no_perm"));
            return true;
        }
        if(!(sender instanceof Player player)) {
            plugin.tell(sender, plugin.languageUtil.getString("command.only_players"));
            return true;
        }
        if(args.length < 2) {
            this.sendMissingArgsMsg(sender, s, args, "<farmId>");
            return true;
        }
        Point[] points = plugin.playerMarkers.get(player);
        if(points == null || points[0] == null || points[1] == null) {
            plugin.tell(player, plugin.languageUtil.getString("command.resize_region.missing_points"));
            return true;
        }
        String id = args[1];
        Farm farm = Farm.getFarm(id);
        if(farm == null) {
            plugin.tell(player, plugin.languageUtil.getString("command.resize_region.does_not_exist").replace("{id}", id));
            return true;
        }

        FarmRegion region = farm.getRegion();
        region.resize(points[0], points[1], player.getWorld().getName());
        plugin.farmRegistry.save();
        plugin.tell(player, plugin.languageUtil.getString("command.resize_region.success").replace("{id}", id));
        return true;
    }
}
