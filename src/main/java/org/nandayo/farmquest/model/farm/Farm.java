package org.nandayo.farmquest.model.farm;

import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.dapi.Util;
import org.nandayo.farmquest.model.quest.Quest;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class Farm {

    private final String id;
    private final FarmRegion region;
    private final Collection<Quest> quests;

    public Farm(@NotNull String id, @NotNull FarmRegion region, @NotNull Collection<Quest> quests) {
        if(!id.matches("[a-z0-9]{8}")) {
            throw new IllegalArgumentException("Invalid Farm ID. (Does not match [a-z0-9]{8}).");
        }
        this.id = id;
        this.region = region;
        this.quests = new ArrayList<>(quests);
    }
    public Farm(@NotNull String id, @NotNull FarmRegion region) {
        this(id, region, new ArrayList<>());
    }


    public void register() {
        if(!registeredFarms.contains(this)) {
            registeredFarms.add(this);
        }else {
            Util.log(String.format("{WARN}Farm with id '%s' was already registered.", id));
        }
    }

    public void unregister() {
        registeredFarms.remove(this);
    }

    /**
     * Link a quest to the Farm.
     * @param quest Quest
     * @return whether linked.
     */
    public boolean linkQuest(@NotNull Quest quest) {
        if(this.quests.contains(quest)) {
            return false;
        }
        this.quests.add(quest);
        return true;
    }

    /**
     * Unlink a quest from the Farm.
     * @param quest Quest
     * @return whether unlinked.
     */
    public boolean unlinkQuest(@NotNull Quest quest) {
        return this.quests.remove(quest);
    }

    //

    @Getter
    static private final Collection<Farm> registeredFarms = new ArrayList<>();

    @Nullable
    static public Farm getFarm(@NotNull String id) {
        return registeredFarms.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst().orElse(null);
    }

    @Nullable
    static public Farm getFarm(@NotNull Location location) {
        return registeredFarms.stream()
                .filter(f -> f.getRegion().isInside(location))
                .findFirst().orElse(null);
    }
}
