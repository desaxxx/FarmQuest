package org.nandayo.farmquest.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.quest.QuestProgress;

public class DeleteRegionCommand extends SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender sender, @NotNull String s, String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();
        if(!sender.hasPermission("farmquest.command.deleteregion")) {
            plugin.tell(sender, plugin.languageUtil.getString("command.no_perm"));
            return true;
        }
        if(args.length < 2) {
            this.sendMissingArgsMsg(sender, s, args, "<farmId>");
            return true;
        }
        String id = args[1];
        Farm farm = Farm.getFarm(id);
        if(farm == null) {
            plugin.tell(sender, plugin.languageUtil.getString("command.delete_region.does_not_exist").replace("{id}", id));
            return true;
        }
        if(args.length < 3 || !args[2].equalsIgnoreCase("confirm")) {
            plugin.tell(sender, plugin.languageUtil.getString("command.delete_region.confirm_info").replace("{farm}", farm.getId()));
            return true;
        }

        for(Farmer farmer : Farmer.getPlayers()) {
            QuestProgress questProgress = farmer.getActiveQuestProgress();
            if(questProgress != null && questProgress.getFarm().getId().equals(id)) {
                farmer.dropQuest(false);
                farmer.tell(plugin.languageUtil.getString("farm_was_deleted"));
            }
        }
        farm.unregister();
        plugin.farmRegistry.save();
        plugin.tell(sender, plugin.languageUtil.getString("command.delete_region.success").replace("{id}", id));
        return true;
    }
}
