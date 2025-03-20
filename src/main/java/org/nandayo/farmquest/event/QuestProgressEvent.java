package org.nandayo.farmquest.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.nandayo.farmquest.model.Farm;
import org.nandayo.farmquest.model.player.FarmPlayer;
import org.nandayo.farmquest.model.quest.Quest;

@Getter
public class QuestProgressEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private final FarmPlayer farmPlayer;
    private final Quest quest;
    private final Farm farm;

    public QuestProgressEvent(@NotNull FarmPlayer farmPlayer, @NotNull Quest quest, @NotNull Farm farm) {
        this.farmPlayer = farmPlayer;
        this.quest = quest;
        this.farm = farm;
    }
}
