package org.nandayo.farmquest.model;

import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.util.StringGenerator;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class Farm {

    private final String id;
    private final FarmRegion region;
    private final Collection<Quest> quests;

    public Farm(@NotNull String id, @NotNull FarmRegion region, @NotNull Collection<Quest> quests) {
        this.id = id;
        this.region = region;
        this.quests = new ArrayList<>(quests);
    }
    public Farm(@NotNull FarmRegion region) {
        this(StringGenerator.getRandomString(8), region, new ArrayList<>());
    }

    public void register() {
        registeredFarms.add(this);
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
                .filter(f -> f.getId().equalsIgnoreCase(id))
                .findFirst().orElse(null);
    }

    @NotNull
    static public Farm getFarmOrThrow(@NotNull String id) {
        Farm farm = getFarm(id);
        if(farm != null) return farm;
        else throw new NullPointerException("Farm is null!");
    }

    @Nullable
    static public Farm getFarm(@NotNull Location location) {
        return registeredFarms.stream()
                .filter(f -> f.getRegion().isInside(location))
                .findFirst().orElse(null);
    }

    @NotNull
    static public Farm getFarmOrThrow(@NotNull Location location) {
        Farm farm = getFarm(location);
        if (farm != null) return farm;
        else throw new NullPointerException("Farm is null.");
    }
}
