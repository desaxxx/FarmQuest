package org.nandayo.farmquest.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MaterialUtil {

    static public boolean hasSpace(@NotNull Player player, @NotNull final ItemStack itemStack) {
        int space = 0;
        for(final ItemStack item : player.getInventory().getContents()) {
            if(item == null) {
                space += itemStack.getMaxStackSize();
            }else if(item.isSimilar(itemStack)) {
                space += itemStack.getMaxStackSize() - item.getAmount();
            }
            if(space >= itemStack.getAmount()) return true;
        }
        return false;
    }

    static public boolean hasMaterials(@NotNull Player player, @NotNull Material material, int amount) {
        int remaining = amount;
        for (final ItemStack item : player.getInventory().getContents()) {
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

    static public void removeItemStack(@NotNull Player player, @NotNull ItemStack itemStack) {
        int remaining = Math.max(0, itemStack.getAmount());
        for(ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.isSimilar(itemStack)) {
                int itemAmount = item.getAmount();
                if (itemAmount <= remaining) {
                    remaining -= itemAmount;
                    item.setAmount(0);
                }else {
                    item.setAmount(itemAmount - remaining);
                    break;
                }
            }
        }
    }

    static public void giveMaterials(@NotNull Player player, @NotNull Material material, int amount) {
        int maxStackSize = material.getMaxStackSize();
        while (amount > 0) {
            int stackSize = Math.min(amount, maxStackSize);
            amount -= stackSize;
            player.getInventory().addItem(new ItemStack(material, stackSize));
        }
    }
}
