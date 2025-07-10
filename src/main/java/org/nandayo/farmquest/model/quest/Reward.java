package org.nandayo.farmquest.model.quest;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.farmquest.model.Farmer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
public class Reward {

    private final @NotNull RewardType type;
    private @NotNull List<String> run;

    public Reward(@NotNull RewardType type, @NotNull List<String> run) {
        this.type = type;
        this.run = new ArrayList<>(run);
    }

    public void setRun(@NotNull List<String> run) {
        this.run = new ArrayList<>(run);
    }

    public void grant(@NotNull Farmer farmer) {
        if(farmer.getPlayer().isEmpty()) return;

        Player player = farmer.getPlayer().get();
        if (type == RewardType.COMMAND) {
            for (String cmd : run) {
                String fixedCmd = cmd.replace("%player_name%", player.getName()).trim();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fixedCmd);
            }
        }
    }

    @Getter
    public enum RewardType {
        COMMAND(Material.COMMAND_BLOCK_MINECART),
        FARM_TOOL(Material.GOLDEN_HOE);

        RewardType(Material icon) {
            this.icon = icon;
        }

        private final Material icon;


        static public RewardType get(@NotNull String name) {
            try {
                return RewardType.valueOf(name.toUpperCase(Locale.ENGLISH));
            }catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
