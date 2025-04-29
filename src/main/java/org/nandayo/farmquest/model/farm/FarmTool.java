package org.nandayo.farmquest.model.farm;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.dapi.Util;
import org.nandayo.farmquest.FarmQuest;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class FarmTool {

    private final String id;
    private final String name;
    private final ItemStack item;

    public FarmTool(@NotNull String id, @NotNull String name, @NotNull ItemStack item) {
        this.id = id;
        this.name = name;
        this.item = item;
    }

    public void register() {
        if(getTool(id) == null) {
            registeredTools.add(this);
        }else {
            Util.log(String.format("{WARN}FarmTool with id '%s' was already registered.", id));
        }
    }

    public Collection<ItemStack> getExtraDrops(@NotNull Collection<ItemStack> actualDrops) {
        if(actualDrops.isEmpty()) return new ArrayList<>();

        Collection<ItemStack> drops = new ArrayList<>();
        if(id.equals("rookie_hoe")) {
            float chance = FarmQuest.getInstance().random.nextFloat();
            if(chance < 0.1f) {
                for(ItemStack drop : actualDrops) {
                    ItemStack d = drop.clone();
                    d.setAmount(1);
                    drops.add(d);
                }
            }
        }
        return drops;
    }



    //

    @Getter
    static private final Collection<FarmTool> registeredTools = new ArrayList<>();

    /**
     * Get tool from id.
     * @param id Id
     * @return FarmTool if found, or <code>null</code>
     */
    @Nullable
    static public FarmTool getTool(@NotNull String id) {
        return registeredTools.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst().orElse(null);
    }

    /**
     * Get tool from id.
     * @param id Id
     * @return FarmTool
     */
    @NotNull
    static public FarmTool getToolOrThrow(@NotNull String id) {
        FarmTool tool = getTool(id);
        if(tool != null) return tool;
        else throw new NullPointerException("FarmTool is null!");
    }

    /**
     * Get tool from item.
     * @param item ItemStack
     * @return FarmTool if found, or <code>null</code>
     */
    @Nullable
    static public FarmTool getTool(@NotNull ItemStack item) {
        return registeredTools.stream()
                .filter(t -> t.getItem().isSimilar(item))
                .findFirst().orElse(null);
    }

    /**
     * Get tool from item.
     * @param item ItemStack
     * @return FarmTool
     */
    @NotNull
    static  public FarmTool getToolOrThrow(@NotNull ItemStack item) {
        FarmTool tool = getTool(item);
        if(tool != null) return tool;
        else throw new NullPointerException("FarmTool is null!");
    }
}
