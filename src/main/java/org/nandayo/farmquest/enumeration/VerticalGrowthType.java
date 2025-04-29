package org.nandayo.farmquest.enumeration;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum VerticalGrowthType {

    UP(new Material[]{
            Material.BAMBOO,
            Material.KELP,
            Material.KELP_PLANT,
            Material.CACTUS,
            Material.SUGAR_CANE
    }),
    DOWN(new Material[]{
            Material.CAVE_VINES,
            Material.CAVE_VINES_PLANT,
    });


    VerticalGrowthType(Material[] materials) {
        this.materials = materials;
    }

    private final Material[] materials;

    public BlockFace getBlockFace() {
        return BlockFace.valueOf(toString());
    }


    //

    static private final Map<Material, VerticalGrowthType> GROWTH_TYPE_MAP = new HashMap<>();

    static {
        for(Material material : VerticalGrowthType.UP.materials) {
            GROWTH_TYPE_MAP.put(material, VerticalGrowthType.UP);
        }
        for(Material material : VerticalGrowthType.DOWN.materials) {
            GROWTH_TYPE_MAP.put(material, VerticalGrowthType.DOWN);
        }
    }

    /**
     * Get VerticalGrowthType of a block type.
     * @param material Block type
     * @return VerticalGrowthType
     */
    @Nullable
    static public VerticalGrowthType get(@NotNull Material material) {
        return GROWTH_TYPE_MAP.get(material);
    }
}
