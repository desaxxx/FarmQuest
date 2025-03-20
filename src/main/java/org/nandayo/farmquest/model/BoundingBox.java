package org.nandayo.farmquest.model;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@Getter
@SuppressWarnings("unused")
public abstract class BoundingBox {

    private final Point minPoint;
    private final Point maxPoint;
    private final @NotNull World world;

    public BoundingBox(@NotNull Point point1, @NotNull Point point2, @NotNull World world) {
        this.minPoint = Point.getMinimum(point1, point2);
        this.maxPoint = Point.getMaximum(point1, point2);
        this.world = world;
    }

    @NotNull
    public Location getMinimumLocation() {
        return minPoint.toLocation(world);
    }

    @NotNull
    public Location getMaximumLocation() {
        return maxPoint.toLocation(world);
    }

    public boolean isInside(@NotNull Point point) {
        return (point.getX() >= minPoint.getX() && point.getX() <= maxPoint.getX())
                && (point.getY() >= minPoint.getY() && point.getY() <= maxPoint.getY())
                && (point.getZ() >= minPoint.getZ() && point.getZ() <= maxPoint.getZ());
    }

    public boolean isInside(@NotNull Location location) {
        return this.isInside(new Point(location));
    }

    public String parseString() {
        return getWorld().getName() + ",[" + getMinPoint().getX() + "_" + getMinPoint().getY() + "_" + getMinPoint().getZ() + "],[" + getMaxPoint().getX() + "_" + getMaxPoint().getY() + "_" + getMaxPoint().getZ() + "]";
    }

    //
}
