package org.nandayo.farmquest.enumeration;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum Setting {

    AUTO_PLANT,
    AUTO_REMOVE,
    // TODO AUTO_PICKUP,
    PROTECT_FARMLAND,
    PROTECT_FARM_REGION;

    public boolean isEnabled() {
        return ENABLED_MAP.get(this);
    }


    //

    static private final Map<Setting, Boolean> ENABLED_MAP = new HashMap<>();

    static {
        ENABLED_MAP.put(AUTO_PLANT, true);
        ENABLED_MAP.put(AUTO_REMOVE, true);
        ENABLED_MAP.put(PROTECT_FARMLAND, true);
        ENABLED_MAP.put(PROTECT_FARM_REGION, true);
    }

    static public void set(@NotNull Setting setting, boolean enabled) {
        ENABLED_MAP.put(setting, enabled);
    }
}
