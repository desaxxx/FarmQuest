package org.nandayo.farmquest.model.quest;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.farmquest.model.farm.FarmTool;
import org.nandayo.farmquest.model.Farmer;

import java.util.List;
import java.util.Locale;

@Getter
public class Reward {

    private final RewardType type;
    private final List<String> run;

    public Reward(@NotNull RewardType type, @NotNull List<String> run) {
        this.type = type;
        this.run = run;
    }

    public void grant(@NotNull Farmer farmer) {
        Player player = farmer.getOfflinePlayer().getPlayer();
        if(player == null) return;

        switch (type) {
            case COMMAND:
                for(String cmd : run) {
                    String fixedCmd = cmd.replace("%player_name%", player.getName()).trim();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fixedCmd);
                }
                break;

            case FARM_TOOL:
                for(String farmTool : run) {
                    FarmTool tool = FarmTool.getToolOrThrow(farmTool);
                    player.getInventory().addItem(tool.getItem());
                    farmer.tell("{SUCCESS}Gained a farm tool!");
                }
                break;
        }
    }

    public enum RewardType {
        COMMAND,FARM_TOOL;

        static public RewardType get(@NotNull String name) {
            try {
                return RewardType.valueOf(name.toUpperCase(Locale.ENGLISH));
            }catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
