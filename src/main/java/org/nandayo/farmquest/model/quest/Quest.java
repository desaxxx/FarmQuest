package org.nandayo.farmquest.model.quest;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.DAPI.Util;
import org.nandayo.farmquest.util.StringGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

@Getter
public class Quest {

    // objective
    // timer
    private final String id;
    private final Objective objective;
    private final String name;

    public Quest(@NotNull String id, @NotNull Objective objective, @NotNull String name) {
        this.id = id;
        this.objective = objective;
        this.name = name;
    }
    public Quest(@NotNull Objective objective, @NotNull String name) {
        this(StringGenerator.getRandomString(2), objective,name);
    }

    public void register() {
        if(getQuest(id) == null) {
            registeredQuests.add(this);
        }else {
            Util.log(String.format("{WARN}Quest with id '%s' was already registered.", id));
        }
    }
    public void unregister() {
        registeredQuests.remove(this);
    }

    public QuestProgress freshProgress() {
        return new QuestProgress(this, 0, Instant.now().getEpochSecond());
    }



    //

    @Getter
    static private final Collection<Quest> registeredQuests = new ArrayList<>();

    /**
     * Get Quest from id.
     * @param id Id
     * @return Quest if found, or <code>null</code>
     */
    @Nullable
    static public Quest getQuest(@NotNull String id) {
        return registeredQuests.stream()
                .filter(q -> q.getId().equals(id))
                .findFirst().orElse(null);
    }

    /**
     * Get Quest from id.
     * @param id Id
     * @return Quest
     */
    static public Quest getQuestOrThrow(@NotNull String id) {
        Quest quest = getQuest(id);
        if(quest != null) return quest;
        else throw new NullPointerException("Quest is null.");
    }
}
