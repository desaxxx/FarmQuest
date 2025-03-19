package org.nandayo.farmquest.model.quest;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum ObjectiveType {
    HARVEST,PLANT,DELIVER;

    static public ObjectiveType get(@NotNull String name) {
        try {
            return ObjectiveType.valueOf(name.toUpperCase(Locale.ENGLISH));
        }catch (IllegalArgumentException e) {
            return null;
        }
    }
}
