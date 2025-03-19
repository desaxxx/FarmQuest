package org.nandayo.farmquest.model;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@Getter
public class Point {

    private final int x, y, z;

    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(double x, double y, double z) {
        this((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    public Point(@NotNull Location location) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Location toLocation(@NotNull World world) {
        return new Location(world,x,y,z,0f, 0f);
    }

    static public Point getMinimum(@NotNull Point point1, @NotNull Point point2) {
        int minX = Math.min(point1.getX(), point2.getX());
        int minY = Math.min(point1.getY(), point2.getY());
        int minZ = Math.min(point1.getZ(), point2.getZ());
        return new Point(minX, minY, minZ);
    }

    static public Point getMaximum(@NotNull Point point1, @NotNull Point point2) {
        int maxX = Math.max(point1.getX(), point2.getX());
        int maxY = Math.max(point1.getY(), point2.getY());
        int maxZ = Math.max(point1.getZ(), point2.getZ());
        return new Point(maxX, maxY, maxZ);
    }
}
