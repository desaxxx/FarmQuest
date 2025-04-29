package org.nandayo.farmquest.model;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@Getter
public class BlockDataHolder {

    private Block block;
    private final BlockData formingBlockData;

    public BlockDataHolder(final @NotNull Block block) {
        this.block = block;
        if(block.getBlockData() instanceof Bisected bisected) {
            if(bisected.getHalf() == Bisected.Half.TOP) {
                this.block = this.block.getRelative(BlockFace.DOWN);
            }
        }
        this.formingBlockData = BlockAdapter.findAdapterGetData(this.block.getBlockData().clone());
    }


    private enum BlockAdapter {

        CAVE_VINES_ADAPTER(oldData -> {
            if(!(oldData instanceof CaveVinesPlant caveVinesPlant)) return oldData;
            BlockData newData = Material.CAVE_VINES.createBlockData();
            if(newData instanceof CaveVinesPlant caveVinesPlant1) {
                caveVinesPlant1.setBerries(caveVinesPlant.isBerries());
            }
            if(newData instanceof Ageable ageable) {
                ageable.setAge(0);
            }
            return newData;
        }),
        KELP_ADAPTER(oldData -> {
            if(oldData.getMaterial() != Material.KELP_PLANT) return oldData;
            BlockData newData = Material.KELP.createBlockData();
            if(newData instanceof Ageable ageable) {
                ageable.setAge(0);
            }
            return newData;
        }),
        TORCHFLOWER_CROP_ADAPTER(oldData -> {
            if(oldData.getMaterial() != Material.TORCHFLOWER) return oldData;
            BlockData newData = Material.TORCHFLOWER_CROP.createBlockData();
            if(newData instanceof Ageable ageable) {
                ageable.setAge(0);
            }
            return newData;
        }),
        AGEABLE_ADAPTER(oldData -> {
            if(!(oldData instanceof Ageable)) return oldData;
            BlockData newData = oldData.clone();
            if(newData instanceof Ageable ageable) {
                ageable.setAge(0);
            }
            return newData;
        });

        BlockAdapter(Function<BlockData, BlockData> function) {
            this.function = function;
        }

        private final Function<BlockData, BlockData> function;

        public BlockData getData(@NotNull BlockData oldData) {
            return function.apply(oldData);
        }

        //

        static public BlockData findAdapterGetData(@NotNull BlockData blockData) {
            return switch (blockData.getMaterial()) {
                case CAVE_VINES_PLANT -> CAVE_VINES_ADAPTER.getData(blockData);
                case KELP_PLANT -> KELP_ADAPTER.getData(blockData);
                case TORCHFLOWER -> TORCHFLOWER_CROP_ADAPTER.getData(blockData);
                default -> AGEABLE_ADAPTER.getData(blockData);
            };
        }
    }
}
