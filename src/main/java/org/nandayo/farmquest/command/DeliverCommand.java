package org.nandayo.farmquest.command;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.command.SubCommand;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.event.QuestCompleteEvent;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.quest.Objective;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.model.quest.QuestProgress;
import org.nandayo.farmquest.util.MaterialUtil;

public class DeliverCommand extends SubCommand {

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
        Farmer farmer = Farmer.getPlayer(player);
        if (farmer == null) {
            plugin.tell(player, plugin.languageUtil.getString("not_a_farmer_player"));
            return true;
        }
        QuestProgress questProgress = farmer.getActiveQuestProgress();
        if (questProgress == null) {
            farmer.tell(plugin.languageUtil.getString("do_not_have_active_quest"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            return true;
        }
        Quest quest = questProgress.getQuest();
        if (quest.getType() != Objective.ObjectiveType.DELIVER) {
            farmer.tell(plugin.languageUtil.getString("command.deliver.quest_not_delivery"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            return true;
        }
        Farm farm = Farm.getFarm(args[1]);
        if(farm == null) {
            plugin.tell(player, plugin.languageUtil.getString("command.farm_not_found").replace("{farm}", args[1]));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            return true;
        }
        if(!farm.getQuests().contains(quest)) {
            farmer.tell(plugin.languageUtil.getString("quest_does_not_belong_farm").replace("{farm}", farm.getId()));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            return true;
        }
        if(!MaterialUtil.hasMaterials(player, quest.getFarmBlock().getCropMaterial(), quest.getTargetAmount())) {
            farmer.tell(plugin.languageUtil.getString("not_enough_materials"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            return true;
        }

        Bukkit.getScheduler().runTask(plugin, () ->
                Bukkit.getPluginManager().callEvent(new QuestCompleteEvent(farmer, questProgress)));

        return true;
    }
}
