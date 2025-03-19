package org.nandayo.farmquest.service.registry;

import org.jetbrains.annotations.NotNull;
import org.nandayo.farmquest.FarmQuest;

import java.io.File;

public interface Registry<T> {

    @NotNull String getFilePath();

    default @NotNull File file() {
        String path = getFilePath();
        File file = new File(FarmQuest.getInstance().getDataFolder(), path);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            FarmQuest.getInstance().saveResource(path, false);
        }
        return file;
    }

    void load();
    void save();
}
