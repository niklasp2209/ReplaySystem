package de.bukkitnews.replay.module.replay.task;

import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recordable.recordables.DespawnEntityRecordable;
import de.bukkitnews.replay.module.replay.data.recordable.recordables.LocationChangeRecordable;
import de.bukkitnews.replay.module.replay.data.recording.ActiveRecording;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

public class LocationTrackingTask implements Runnable {

    private final @NotNull ActiveRecording activeRecording;
    private final @NotNull RecordingArea recordingArea;
    private final @NotNull ReplayModule replayModule;

    public LocationTrackingTask(@NotNull ReplayModule replayModule, @NotNull ActiveRecording activeRecording) {
        this.replayModule = replayModule;
        this.activeRecording = activeRecording;
        this.recordingArea = activeRecording.getRecordingArea();
    }

    /**
     * Runs the task that tracks the location of each entity in the recording.
     * This method checks whether each entity is within the camera's region and records its location.
     * If the entity is no longer in the region, it removes the entity from tracking and records a despawn event.
     */
    @Override
    public void run() {
        Queue<UUID> recordableEntities = activeRecording.getRecordableEntities();
        synchronized (recordableEntities) {
            recordableEntities.forEach(recordableEntity -> {
                Optional.ofNullable(Bukkit.getEntity(recordableEntity))
                        .ifPresentOrElse(entity -> {
                            if (recordingArea.isInRegion(entity.getLocation())) {
                                removeEntityFromTracking(recordableEntity);
                                return;
                            }

                            LocationChangeRecordable recordable = new LocationChangeRecordable(entity.getLocation(), entity.getUniqueId());
                            replayModule.getRecordingHandler().addRecordable(activeRecording, recordable);
                        }, () -> removeEntityFromTracking(recordableEntity));
            });
        }
    }

    /**
     * Removes an entity from tracking and records a despawn event.
     *
     * @param recordableEntity The UUID of the entity to be removed from tracking.
     */
    private void removeEntityFromTracking(UUID recordableEntity) {
        activeRecording.removeEntityFromTracking(recordableEntity);
        DespawnEntityRecordable despawnEntityRecordable = new DespawnEntityRecordable(recordableEntity);
        replayModule.getRecordingHandler().addRecordable(activeRecording, despawnEntityRecordable);
    }
}
