package org.nandayo.farmquest.enumeration;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

@Getter
public enum FarmBlock {
    WHEAT (Material.WHEAT_SEEDS, Material.WHEAT, Material.WHEAT),
    CARROTS (Material.CARROT, Material.CARROT, Material.CARROTS),
    POTATOES (Material.POTATO, Material.POTATO, Material.POTATOES),
    CAVE_VINES (Material.GLOW_BERRIES, Material.GLOW_BERRIES, Material.CAVE_VINES, Material.CAVE_VINES_PLANT),
    NETHER_WART (Material.NETHER_WART, Material.NETHER_WART, Material.NETHER_WART),
    KELP (Material.KELP, Material.KELP, Material.KELP, Material.KELP_PLANT),
    CACTUS (Material.CACTUS, Material.CACTUS, Material.CACTUS),
    SWEET_BERRY_BUSH (Material.SWEET_BERRIES, Material.SWEET_BERRIES, Material.SWEET_BERRY_BUSH),
    SUGAR_CANE(Material.SUGAR_CANE, Material.SUGAR_CANE, Material.SUGAR_CANE),
    COCOA (Material.COCOA_BEANS, Material.COCOA_BEANS, Material.COCOA),
    PITCHER_CROP (Material.PITCHER_POD, Material.PITCHER_PLANT, Material.PITCHER_CROP),
    BAMBOO (Material.BAMBOO, Material.BAMBOO, Material.BAMBOO, Material.BAMBOO_SAPLING),
    MELON (Material.MELON_SEEDS, Material.MELON_SLICE, Material.MELON),
    BEETROOTS (Material.BEETROOT_SEEDS, Material.BEETROOT, Material.BEETROOTS),
    PUMPKIN (Material.PUMPKIN_SEEDS, Material.PUMPKIN, Material.PUMPKIN),
    TORCHFLOWER_SEEDS (Material.TORCHFLOWER_SEEDS, Material.TORCHFLOWER, Material.TORCHFLOWER),
    ;

    FarmBlock(@NotNull Material seedMaterial, @NotNull Material cropMaterial, Material... blockMaterials) {
        this.seedMaterial = seedMaterial;
        this.cropMaterial = cropMaterial;
        this.blockMaterials = Arrays.asList(blockMaterials);
    }

    private final @NotNull Material seedMaterial;
    private final @NotNull Material cropMaterial;
    private final @NotNull Collection<Material> blockMaterials;

    public boolean equals(@NotNull FarmBlock other) {
        return this.toString().equals(other.toString());
    }

    public boolean equals(@NotNull Material material) {
        FarmBlock other = FarmBlock.get(material);
        return other != null && this.equals(other);
    }


    /**
     * Gets the FarmBlock from given material.
     * @param material Material
     * @return FarmBlock if exists, or <code>null</code>
     */
    @Nullable
    static public FarmBlock get(@NotNull Material material) {
        for (FarmBlock block : FarmBlock.values()) {
            if(block.getSeedMaterial() == material ||
            block.getCropMaterial() == material ||
            block.getBlockMaterials().contains(material)) {
                return block;
            }
        }
        return null;
    }

    /**
     * Gets the FarmBlock from given material name.
     * @param material Material name
     * @return FarmBlock if exists, or <code>null</code>
     */
    @Nullable
    static public FarmBlock get(@NotNull String material) {
        Material mat = Material.getMaterial(material);
        if(mat == null) return null;
        return get(mat);
    }

    /**
     * Check if a material is FarmBlock.
     * @param material Material
     * @return boolean
     */
    static public boolean isFarmBlock(@NotNull Material material) {
        return get(material) != null;
    }
}
