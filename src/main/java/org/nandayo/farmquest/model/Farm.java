package org.nandayo.farmquest.model;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.nandayo.farmquest.util.StringGenerator;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class Farm {

    private final String id;
    private final FarmRegion region;

    public Farm(@NotNull String id, @NotNull FarmRegion region) {
        this.id = id;
        this.region = region;
    }
    public Farm(@NotNull FarmRegion region) {
        this(StringGenerator.getRandomString(8), region);
    }

    public void register() {
        registeredFarms.add(this);
    }
    public void unregister() {
        registeredFarms.remove(this);
    }

    //

    @Getter
    static private final Collection<Farm> registeredFarms = new ArrayList<>();
}
