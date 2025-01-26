package de.bukkitnews.replay.module.replay.data.recording;

import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.task.CameraTrackingTask;
import de.bukkitnews.replay.module.replay.task.EquipmentTrackerTask;
import de.bukkitnews.replay.module.replay.task.LocationTrackingTask;
import de.bukkitnews.replay.module.replay.task.TickTrackerTask;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
public class ActiveRecording {

    private final @NotNull Recording recording;
    private final @NotNull RecordingArea recordingArea;
    private final @NotNull Queue<UUID> recordableEntities;
    private final @NotNull BukkitTask trackLocationTask;
    private final @NotNull BukkitTask scanEntitiesTask;
    private final @NotNull BukkitTask trackEquipmentTask;
    private final @NotNull Queue<Recordable> recordableBuffer = new ConcurrentLinkedQueue<>();
    private final @NotNull ReplayModule replayModule;
    private volatile boolean isProcessingBuffer = false;

    /**
     * Creates a new active recording for the given camera and owner.
     *
     * @param recordingArea The camera being used for recording.
     * @param owner         The player who owns this recording.
     */
    public ActiveRecording(@NotNull ReplayModule replayModule, @NotNull RecordingArea recordingArea, @NotNull Player owner) {
        this.replayModule = replayModule;
        this.recording = new Recording(recordingArea, owner);
        this.recordingArea = recordingArea;
        recording.setOriginalBlocks(recordingArea.getMaterialsInRegion());
        this.recordableEntities = new ConcurrentLinkedQueue<>();

        this.scanEntitiesTask = Bukkit.getScheduler().runTaskTimer(
                replayModule.getReplaySystem(),
                new CameraTrackingTask(this, replayModule),
                0L,
                20L
        );

        this.trackLocationTask = Bukkit.getScheduler().runTaskTimer(
                replayModule.getReplaySystem(),
                new LocationTrackingTask(replayModule, this),
                0L,
                1L
        );

        this.trackEquipmentTask = Bukkit.getScheduler().runTaskTimer(
                replayModule.getReplaySystem(),
                new EquipmentTrackerTask(this, replayModule),
                0L,
                20L
        );
    }

    /**
     * Stops the recording, setting end time and end tick.
     */
    public void stopRecording() {
        recording.setEndTime(System.currentTimeMillis());
        recording.setEndTick(TickTrackerTask.getCurrentTick());
        scanEntitiesTask.cancel();
        trackLocationTask.cancel();
        trackEquipmentTask.cancel();
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
     * Attempts to initiate the buffer processing task, ensuring it's not already running.
     *
     * @return true if the process was started, false if it's already running.
     */
    public synchronized boolean initiateBufferProcessing() {
        if (isProcessingBuffer) {
            return false;
        }

        this.isProcessingBuffer = true;
        return true;
    }

    /**
     * Completes the buffer processing task.
     */
    public void completeBufferProcessing() {
        this.isProcessingBuffer = false;
    }
}