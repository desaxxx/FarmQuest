//package org.nandayo.farmquest.util;
//
//import org.bukkit.Material;
//import org.bukkit.block.data.Ageable;
//import org.bukkit.block.data.BlockData;
//import org.bukkit.block.data.type.CaveVines;
//import org.bukkit.block.data.type.CaveVinesPlant;
//import org.jetbrains.annotations.NotNull;
//
//public class FarmUtil {
//
//    static public boolean isSimilarMaterial(@NotNull Material originalMaterial, @NotNull Material compareMaterial) {
//        if(originalMaterial == compareMaterial) return true;
//        return switch (originalMaterial) {
//            // Item - Block
//            case CARROT -> compareMaterial == Material.CARROTS;
//            case CARROTS -> compareMaterial == Material.CARROT;
//            case POTATO -> compareMaterial == Material.POTATOES;
//            case POTATOES -> compareMaterial == Material.POTATO;
//            // Block- Block
//            case KELP -> compareMaterial == Material.KELP_PLANT;
//            case KELP_PLANT -> compareMaterial == Material.KELP;
//            case CAVE_VINES -> compareMaterial == Material.CAVE_VINES_PLANT;
//            case CAVE_VINES_PLANT -> compareMaterial == Material.CAVE_VINES;
//            case TORCHFLOWER -> compareMaterial == Material.TORCHFLOWER_CROP;
//            case TORCHFLOWER_CROP -> compareMaterial == Material.TORCHFLOWER;
//            default -> false;
//        };
//    }
//
//    static public boolean isReadyToHarvest(final @NotNull BlockData blockData) {
//        switch (blockData.getMaterial()) { // Crops that age doesn't matter
//            case KELP, KELP_PLANT, CACTUS, SUGAR_CANE, BAMBOO:
//                return true;
//        }
//
//        if(blockData.getMaterial() == Material.SUGAR_CANE ) return true;
//
//        if (blockData instanceof CaveVines caveVines) {
//            return caveVines.isBerries();
//        }
//        else if (blockData instanceof CaveVinesPlant caveVinesPlant) {
//            return caveVinesPlant.isBerries();
//        }
//
//        // General BlockData (ensure Ageable is only used for other crops)
//        if (blockData instanceof Ageable ageable) {
//            return ageable.getAge() >= ageable.getMaximumAge();
//        }
//
//        return true;
//    }
//}
