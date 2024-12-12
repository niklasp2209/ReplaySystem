package de.bukkitnews.replay.module.replay.data.recording;

import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.task.CameraTrackingTask;
import de.bukkitnews.replay.module.replay.task.EquipmentTrackerTask;
import de.bukkitnews.replay.module.replay.task.LocationTrackingTask;
import de.bukkitnews.replay.module.replay.task.TickTrackerTask;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
public class ActiveRecording {

    private Recording recording;
    private RecordingArea recordingArea;
    private final Queue<UUID> recordableEntities;
    private BukkitTask trackLocationTask;
    private BukkitTask scanEntitiesTask;
    private BukkitTask trackEquipmentTask;
    private Queue<Recordable> recordableBuffer = new ConcurrentLinkedQueue<>();
    private volatile boolean isFlushing = false;

    /**
     * Creates a new active recording for the given camera and owner.
     *
     * @param recordingArea The camera being used for recording.
     * @param owner  The player who owns this recording.
     */
    public ActiveRecording(RecordingArea recordingArea, Player owner) {
        this.recording = new Recording(recordingArea, owner);
        this.recordingArea = recordingArea;
        this.recording.setOriginalBlocks(this.recordingArea.getMaterialsInRegion());
        this.recordableEntities = new ConcurrentLinkedQueue<>();

        this.scanEntitiesTask = Bukkit.getScheduler().runTaskTimer(
                ReplayModule.instance.getReplaySystem(),
                new CameraTrackingTask(this),
                0L,
                20L
        );

        this.trackLocationTask = Bukkit.getScheduler().runTaskTimer(
                ReplayModule.instance.getReplaySystem(),
                new LocationTrackingTask(this),
                0L,
                1L
        );

        this.trackEquipmentTask = Bukkit.getScheduler().runTaskTimer(
                ReplayModule.instance.getReplaySystem(),
                new EquipmentTrackerTask(this),
                0L,
                20L
        );
    }

    /**
     * Stops the recording, setting end time and end tick.
     */
    public void stopRecording() {
        this.recording.setEndTime(System.currentTimeMillis());
        this.recording.setEndTick(TickTrackerTask.getCurrentTick());
        this.scanEntitiesTask.cancel();
        this.trackLocationTask.cancel();
    }

    /**
     * Checks if the entity is currently being tracked.
     *
     * @param bukkitEntityId The UUID of the entity to check.
     * @return true if the entity is being tracked, false otherwise.
     */
    public boolean isEntityBeingTracked(UUID bukkitEntityId) {
        synchronized (recordableEntities) {
            return recordableEntities.contains(bukkitEntityId);
        }
    }

    /**
     * Adds an entity to the tracking list if it is not already tracked.
     *
     * @param bukkitEntityId The UUID of the entity to add.
     * @return true if the entity was added, false if it was already tracked.
     */
    public boolean addEntityIfNotAlreadyTracked(UUID bukkitEntityId) {
        synchronized (recordableEntities) {
            if (!recordableEntities.contains(bukkitEntityId)) {
                recordableEntities.add(bukkitEntityId);
                return true;
            }
            return false;
        }
    }

    /**
     * Removes an entity from the tracking list.
     *
     * @param bukkitEntityId The UUID of the entity to remove.
     */
    public void removeEntityFromTracking(UUID bukkitEntityId) {
        synchronized (recordableEntities) {
            recordableEntities.remove(bukkitEntityId);
        }
    }

    /**
     * Attempts to start flushing the recordable buffer.
     *
     * @return true if flushing was successfully started, false if already flushing.
     */
    public synchronized boolean tryStartFlushing() {
        if (!isFlushing) {
            isFlushing = true;
            return true;
        }
        return false;
    }

    /**
     * Ends the flushing process for the recordable buffer.
     */
    public void endFlushing() {
        isFlushing = false;
    }
}