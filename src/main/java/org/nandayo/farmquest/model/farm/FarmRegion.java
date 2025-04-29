package org.nandayo.farmquest.model.farm;

import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.model.BoundingBox;
import org.nandayo.dapi.model.Point;

public class FarmRegion extends BoundingBox {

    public FarmRegion(@NotNull Point point1, @NotNull Point point2, @NotNull String worldName) {
        super(point1, point2, worldName);
    }
}