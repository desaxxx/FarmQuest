package org.nandayo.farmquest.event;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.nandayo.farmquest.enumeration.FarmBlock;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.quest.QuestProgress;

@Getter
public class FarmBlockBreakEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private final @NotNull Farmer farmer;
    private final @NotNull Block block;
    private final @NotNull FarmBlock farmBlock;
    private final @NotNull Farm farm;
    private final @NotNull QuestProgress questProgress;

    private boolean cancelled;

    public FarmBlockBreakEvent(@NotNull Farmer farmer, @NotNull Block block, @NotNull FarmBlock farmBlock, @NotNull Farm farm, @NotNull QuestProgress questProgress) {
        this.farmer = farmer;
        this.block = block;
        this.farmBlock = farmBlock;
        this.farm = farm;
        this.questProgress = questProgress;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
