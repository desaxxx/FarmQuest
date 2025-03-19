package org.nandayo.farmquest.model;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class FarmRegion extends BoundingBox {

    public FarmRegion(@NotNull Point point1, @NotNull Point point2, @NotNull World world) {
        super(point1, point2, world);
    }

    //

    @NotNull
    static public FarmRegion fromString(@NotNull String str) {
        String[] split = str.split(",");

        World world = Bukkit.getWorld(split[0]);
        if (world == null) {
            throw new NullPointerException("World not found!");
        }

        String[] point1split = split[1].replace("[", "").replace("]", "").split("_");
        Point minPoint = new Point(Integer.parseInt(point1split[0]), Integer.parseInt(point1split[1]), Integer.parseInt(point1split[2]));

        String[] point2split = split[2].replace("[", "").replace("]", "").split("_");
        Point maxPoint = new Point(Integer.parseInt(point2split[0]), Integer.parseInt(point2split[1]), Integer.parseInt(point2split[2]));

        return new FarmRegion(minPoint, maxPoint, world);
    }
}
