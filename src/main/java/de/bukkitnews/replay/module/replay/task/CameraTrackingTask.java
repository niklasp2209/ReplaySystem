package de.bukkitnews.replay.module.replay.task;

import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recordable.recordables.SpawnEntityRecordable;
import de.bukkitnews.replay.module.replay.data.recording.ActiveRecording;
import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CameraTrackingTask implements Runnable {

    @NonNull private final ActiveRecording activeRecording;

    public CameraTrackingTask(@NonNull ActiveRecording activeRecording) {
        this.activeRecording = activeRecording;
    }

    /**
     * Runs the task that tracks entities in the camera's region.
     * It checks all loaded chunks within the camera's defined region
     * and adds entities within the region to the tracking list, recording their spawn event.
     */
    @Override
    public void run() {
        Location corner1 = activeRecording.getRecordingArea().getCorner1();
        Location corner2 = activeRecording.getRecordingArea().getCorner2();

        Chunk[] loadedChunks = corner1.getWorld().getLoadedChunks();
        List<Chunk> chunksInRegion = new ArrayList<>();

        int chunkX1 = corner1.getBlockX() >> 4;
        int chunkZ1 = corner1.getBlockZ() >> 4;
        int chunkX2 = corner2.getBlockX() >> 4;
        int chunkZ2 = corner2.getBlockZ() >> 4;

        int minX = Math.min(chunkX1, chunkX2);
        int maxX = Math.max(chunkX1, chunkX2);
        int minZ = Math.min(chunkZ1, chunkZ2);
        int maxZ = Math.max(chunkZ1, chunkZ2);

        for (Chunk chunk : loadedChunks) {
            if (chunk.getX() >= minX && chunk.getX() <= maxX &&
                    chunk.getZ() >= minZ && chunk.getZ() <= maxZ) {
                chunksInRegion.add(chunk);
            }
        }

        for (Chunk chunk : chunksInRegion) {
            Entity[] entities = chunk.getEntities();
            for (Entity entity : entities) {
                if (!activeRecording.getRecordingArea().isInRegion(entity.getLocation())) {
                    continue;
                }

                Optional<Entity> optionalEntity = Optional.ofNullable(entity);

                optionalEntity.filter(e -> activeRecording.addEntityIfNotAlreadyTracked(e.getUniqueId()))
                        .ifPresent(e -> {
                            System.out.println("ADDED ENTITY TO TRACKING: " + e.getType());
                            SpawnEntityRecordable spawnEntityRecordable = new SpawnEntityRecordable(e);
                            ReplayModule.instance.getRecordingHandler().addRecordable(activeRecording, spawnEntityRecordable);
                        });
            }
        }
    }
}