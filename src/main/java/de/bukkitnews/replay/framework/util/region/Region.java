package de.bukkitnews.replay.framework.util.region;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents a 3D region defined by two corner locations in a Minecraft world.
 * Provides utilities to check positions, retrieve blocks or entities, and measure dimensions.
 */
@Data
public class Region {

    private final Optional<Location> corner1;
    private final Optional<Location> corner2;

    /**
     * Constructs a new Region with two corners.
     *
     * @param corner1 The first corner of the region (nullable).
     * @param corner2 The second corner of the region (nullable).
     */
    public Region(Location corner1, Location corner2) {
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
    public Optional<World> getWorld() {
        return corner1.map(Location::getWorld);
    }

    /**
     * Determines whether a given location is within the bounds of this region.
     *
     * @param loc The location to check.
     * @return true if the location is inside the region, false otherwise.
     */
    public boolean isIn(Location loc) {
        if (!isSet() || loc == null || !getWorld().orElse(null).equals(loc.getWorld())) {
            return false;
        }

        double xMin = Math.min(corner1.get().getX(), corner2.get().getX());
        double xMax = Math.max(corner1.get().getX(), corner2.get().getX());
        double yMin = Math.min(corner1.get().getY(), corner2.get().getY());
        double yMax = Math.max(corner1.get().getY(), corner2.get().getY());
        double zMin = Math.min(corner1.get().getZ(), corner2.get().getZ());
        double zMax = Math.max(corner1.get().getZ(), corner2.get().getZ());

        return loc.getX() >= xMin && loc.getX() <= xMax &&
                loc.getY() >= yMin && loc.getY() <= yMax &&
                loc.getZ() >= zMin && loc.getZ() <= zMax;
    }

    /**
     * Calculates the total number of blocks contained within this region.
     *
     * @return The total block count.
     */
    public int getTotalBlockSize() {
        return (int) (getXWidth() * getHeight() * getZWidth());
    }

    /**
     * Calculates the width of the region along the X-axis.
     *
     * @return The X-axis width as a double.
     */
    public double getXWidth() {
        return Math.abs(corner1.get().getX() - corner2.get().getX()) + 1;
    }

    /**
     * Calculates the height of the region along the Y-axis.
     *
     * @return The Y-axis height as a double.
     */
    public double getHeight() {
        return Math.abs(corner1.get().getY() - corner2.get().getY()) + 1;
    }

    /**
     * Calculates the width of the region along the Z-axis.
     *
     * @return The Z-axis width as a double.
     */
    public double getZWidth() {
        return Math.abs(corner1.get().getZ() - corner2.get().getZ()) + 1;
    }

    /**
     * Retrieves all blocks within the bounds of this region.
     *
     * @param world The world in which the blocks are located.
     * @return A list of all blocks within the region. Returns an empty list if the region is not set or the world is null.
     */
    public List<Block> blockList(World world) {
        if (!isSet() || world == null) {
            return Collections.emptyList();
        }

        int xMin = (int) Math.min(corner1.get().getX(), corner2.get().getX());
        int xMax = (int) Math.max(corner1.get().getX(), corner2.get().getX());
        int yMin = (int) Math.min(corner1.get().getY(), corner2.get().getY());
        int yMax = (int) Math.max(corner1.get().getY(), corner2.get().getY());
        int zMin = (int) Math.min(corner1.get().getZ(), corner2.get().getZ());
        int zMax = (int) Math.max(corner1.get().getZ(), corner2.get().getZ());

        return IntStream.rangeClosed(xMin, xMax).boxed().flatMap(x ->
                IntStream.rangeClosed(yMin, yMax).boxed().flatMap(y ->
                        IntStream.rangeClosed(zMin, zMax).mapToObj(z -> world.getBlockAt(x, y, z))
                )
        ).collect(Collectors.toList());
    }

    /**
     * Checks if a specific player is within the region.
     *
     * @param player The player to check.
     * @return true if the player is inside the region, otherwise false.
     */
    public boolean isPlayerIn(Player player) {
        return player != null && isIn(player.getLocation());
    }

    /**
     * Retrieves all entities within the region's bounds.
     *
     * @return A list of entities within the region. Returns an empty list if the region is not set.
     */
    public List<Entity> getEntities() {
        if (!isSet()) {
            return Collections.emptyList();
        }

        return getWorld().map(world ->
                world.getEntities().stream()
                        .filter(entity -> isIn(entity.getLocation()))
                        .collect(Collectors.toList())
        ).orElse(Collections.emptyList());
    }
}