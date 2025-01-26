package de.bukkitnews.replay.module.replay.task;

import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recordable.recordables.SpawnEntityRecordable;
import de.bukkitnews.replay.module.replay.data.recording.ActiveRecording;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CameraTrackingTask implements Runnable {

    private final @NotNull ActiveRecording activeRecording;
    private final @NotNull ReplayModule replayModule;

    /**
     * Runs the task that tracks entities in the camera's region.
     * It checks all loaded chunks within the camera's defined region
     * and adds entities within the region to the tracking list, recording their spawn event.
     */
    @Override
    public void run() {
        Optional<Location> corner1Opt = activeRecording.getRecordingArea().getCorner1();
        Optional<Location> corner2Opt = activeRecording.getRecordingArea().getCorner2();

        if (corner1Opt.isEmpty() || corner2Opt.isEmpty()) {
            return;
        }

        Location corner1 = corner1Opt.get();
        Location corner2 = corner2Opt.get();

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

                if (activeRecording.addEntityIfNotAlreadyTracked(entity.getUniqueId())) {
                    SpawnEntityRecordable spawnEntityRecordable = new SpawnEntityRecordable(entity);
                    replayModule.getRecordingHandler().addRecordable(activeRecording, spawnEntityRecordable);
                }
            }
        }
    }
}