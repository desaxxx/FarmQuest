package org.nandayo.farmquest.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Util {

    static public boolean hasMaterials(@NotNull Player player, @NotNull Material material, int amount) {
        int remaining = amount;
        for (ItemStack item : player.getInventory().getContents().clone()) {
            if (item != null && item.getType() == material) {
                remaining -= item.getAmount();
                if (remaining <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    static public void removeMaterials(@NotNull Player player, @NotNull Material material, int amount) {
        int remaining = Math.max(0, amount);
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                int itemAmount = item.getAmount();
                if (itemAmount <= remaining) {
                    remaining -= itemAmount;
                    item.setAmount(0);
                } else {
                    item.setAmount(itemAmount - remaining);
                    break;
                }
            }
        }
    }
}
