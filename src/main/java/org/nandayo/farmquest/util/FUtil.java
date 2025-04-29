package org.nandayo.farmquest.util;

import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CaveVines;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.jetbrains.annotations.NotNull;
import org.nandayo.farmquest.enumeration.FarmBlock;

public class FUtil {

    static public boolean isReadyToHarvest(final @NotNull BlockData blockData) {
        FarmBlock farmBlock = FarmBlock.get(blockData.getMaterial());
        if (farmBlock == null) return false;

        switch (farmBlock.getCropMaterial()) { // Crops that age doesn't matter
            case KELP, CACTUS, SUGAR_CANE, BAMBOO:
                return true;
        }

        if (blockData instanceof CaveVines caveVines) {
            return caveVines.isBerries();
        }
        else if (blockData instanceof CaveVinesPlant caveVinesPlant) {
            return caveVinesPlant.isBerries();
        }

        // General BlockData (ensure Ageable is only used for other crops)
        if (blockData instanceof Ageable ageable) {
            return ageable.getAge() >= ageable.getMaximumAge();
        }

        return true;
    }
}
