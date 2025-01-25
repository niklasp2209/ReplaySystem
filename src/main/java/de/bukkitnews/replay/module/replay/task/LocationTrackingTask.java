package de.bukkitnews.replay.module.replay.task;

import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recordable.recordables.DespawnEntityRecordable;
import de.bukkitnews.replay.module.replay.data.recordable.recordables.LocationChangeRecordable;
import de.bukkitnews.replay.module.replay.data.recording.ActiveRecording;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import lombok.NonNull;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

public class LocationTrackingTask implements Runnable {

    private final ActiveRecording activeRecording;
    private final RecordingArea recordingArea;

    public LocationTrackingTask(ActiveRecording activeRecording) {
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
                                LocationChangeRecordable recordable = new LocationChangeRecordable(entity.getLocation(), entity.getUniqueId());
                                ReplayModule.instance.getRecordingHandler().addRecordable(activeRecording, recordable);
                            } else {
                                removeEntityFromTracking(recordableEntity);
                            }
                        }, () -> {
                            removeEntityFromTracking(recordableEntity);
                        });
            });
        }
    }

    /**
     * Removes an entity from tracking and records a despawn event.
     *
     * @param recordableEntity The UUID of the entity to be removed from tracking.
     */
    private void removeEntityFromTracking(UUID recordableEntity) {
        System.out.println("REMOVING THE DESPAWNED ENTITY FROM TRACKING");

        activeRecording.removeEntityFromTracking(recordableEntity);


        DespawnEntityRecordable despawnEntityRecordable = new DespawnEntityRecordable(recordableEntity);
        ReplayModule.instance.getRecordingHandler().addRecordable(activeRecording, despawnEntityRecordable);
    }
}
