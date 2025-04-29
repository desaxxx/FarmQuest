package org.nandayo.farmquest.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.quest.Quest;

@Getter
public class QuestCompleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private final Farmer farmer;
    private final Quest quest;
    private final Farm farm;

    public QuestCompleteEvent(@NotNull Farmer farmer, @NotNull Quest quest, @NotNull Farm farm) {
        this.farmer = farmer;
        this.quest = quest;
        this.farm = farm;
    }
}
