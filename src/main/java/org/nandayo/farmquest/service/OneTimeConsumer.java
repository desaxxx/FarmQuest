package org.nandayo.farmquest.service;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.nandayo.farmquest.FarmQuest;

import java.util.function.Consumer;

public class OneTimeConsumer<T> implements Consumer<T> {

    private final Consumer<T> delegate;
    public OneTimeConsumer(Consumer<T> delegate) {
        this.delegate = delegate;
    }

    @Getter
    private boolean used = false;

    @Override
    public void accept(T t) {
        if(used) return;
        used = true;
        delegate.accept(t);
    }

    public void acceptSync(T t) {
        Bukkit.getScheduler().runTask(FarmQuest.getInstance(), () -> accept(t));
    }
}
