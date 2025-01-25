package de.bukkitnews.replay.module.replay.region.region;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a 3D region defined by two corner locations in a Minecraft world.
 * Provides utilities to check positions, retrieve blocks or entities, and measure dimensions.
 */
@Data
public class Region {

    private final @NotNull Optional<Location> corner1;
    private final @NotNull Optional<Location> corner2;

    /**
     * Constructs a new Region with two corners.
     *
     * @param corner1 The first corner of the region (nullable).
     * @param corner2 The second corner of the region (nullable).
     */
    public Region(@Nullable Location corner1, @Nullable Location corner2) {
        this.corner1 = Optional.ofNullable(corner1);
        this.corner2 = Optional.ofNullable(corner2);
    }

    /**
     * Checks if both corners of the region are set.
     *
     * @return true if both corners are present, otherwise false.
     */
    public boolean isSet() {
        return corner1.isPresent() && corner2.isPresent();
    }

    /**
     * Retrieves the world of the region based on the first corner's world.
     *
     * @return An Optional containing the world, or empty if the first corner is not set.
     */
    public @NotNull Optional<World> getWorld() {
        return corner1.map(Location::getWorld);
    }

    /**
     * Determines whether a given location is within the bounds of this region.
     *
     * @param location The location to check.
     * @return true if the location is inside the region, false otherwise.
     */
    public boolean isIn(@Nullable Location location) {
        if (!isSet() || location == null || !getWorld().orElse(null).equals(location.getWorld())) {
            return false;
        }
        return corner1.flatMap(c1 -> corner2.map(c2 -> {
            double xMin = Math.min(c1.getX(), c2.getX());
            double xMax = Math.max(c1.getX(), c2.getX());
            double yMin = Math.min(c1.getY(), c2.getY());
            double yMax = Math.max(c1.getY(), c2.getY());
            double zMin = Math.min(c1.getZ(), c2.getZ());
            double zMax = Math.max(c1.getZ(), c2.getZ());

            return location.getX() >= xMin && location.getX() <= xMax &&
                    location.getY() >= yMin && location.getY() <= yMax &&
                    location.getZ() >= zMin && location.getZ() <= zMax;
        })).orElse(false);
    }
}