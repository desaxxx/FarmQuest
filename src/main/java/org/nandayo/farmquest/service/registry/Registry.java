package org.nandayo.farmquest.service.registry;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.nandayo.farmquest.FarmQuest;

import java.io.File;

@Getter
public abstract class Registry {

    public Registry(@NotNull FarmQuest plugin) {
        loadFile(plugin);
    }

    private File file;

    abstract public @NotNull String getFilePath();

    public void loadFile(@NotNull FarmQuest plugin) {
        String path = getFilePath();
        File file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            plugin.saveResource(path, false);
        }
        this.file = file;
    }

    abstract void load();
    abstract void save();
}
