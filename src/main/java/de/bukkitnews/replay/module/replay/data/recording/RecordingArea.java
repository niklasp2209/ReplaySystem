package de.bukkitnews.replay.module.replay.data.recording;

import lombok.Data;
import org.bson.types.ObjectId;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
public class RecordingArea {

    private @NotNull ObjectId id;
    private final @NotNull String name;
    private final @NotNull UUID owner;
    private @NotNull Optional<Location> corner1 = Optional.empty();
    private @NotNull Optional<Location> corner2 = Optional.empty();

    public RecordingArea(@NotNull String name, @NotNull UUID owner) {
        this.name = name;
        this.owner = owner;
    }

    /**
     * Checks if the given location is within the region defined by corner1 and corner2.
     *
     * @param location The location to check.
     * @return true if the location is within the region, false otherwise.
     */
    public boolean isInRegion(Location location) {
        if (!corner1.isPresent() || !corner2.isPresent()) {
            return true;
        }

        Location corner1Location = corner1.get();
        Location corner2Location = corner2.get();

        double x1 = corner1Location.getX();
        double x2 = corner2Location.getX();
        double y1 = corner1Location.getY();
        double y2 = corner2Location.getY();
        double z1 = corner1Location.getZ();
        double z2 = corner2Location.getZ();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return !(x >= Math.min(x1, x2)) || !(x <= Math.max(x1, x2)) ||
                !(y >= Math.min(y1, y2)) || !(y <= Math.max(y1, y2)) ||
                !(z >= Math.min(z1, z2)) || !(z <= Math.max(z1, z2));
    }

    /**
     * Returns a list of all materials present within the region defined by corner1 and corner2.
     *
     * @return A list of materials found in the region.
     * @throws IllegalArgumentException If the corners are in different worlds.
     */
    public @NotNull List<Material> getMaterialsInRegion() {
        List<Material> materials = new ArrayList<>();

        if (!corner1.isPresent() || !corner2.isPresent()) {
            throw new IllegalArgumentException("Both corners must be set");
        }

        World world = corner1.get().getWorld();
        if (!corner1.get().getWorld().equals(corner2.get().getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world");
        }

        int startX = Math.min(corner1.get().getBlockX(), corner2.get().getBlockX());
        int endX = Math.max(corner1.get().getBlockX(), corner2.get().getBlockX());

        int startY = Math.min(corner1.get().getBlockY(), corner2.get().getBlockY());
        int endY = Math.max(corner1.get().getBlockY(), corner2.get().getBlockY());

        int startZ = Math.min(corner1.get().getBlockZ(), corner2.get().getBlockZ());
        int endZ = Math.max(corner1.get().getBlockZ(), corner2.get().getBlockZ());

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    materials.add(world.getBlockAt(x, y, z).getType());
                }
            }
        }

        return materials;
    }
}