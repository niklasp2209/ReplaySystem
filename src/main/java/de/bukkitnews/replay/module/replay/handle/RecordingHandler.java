package de.bukkitnews.replay.module.replay.handle;

import de.bukkitnews.replay.framework.util.MessageUtil;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.recording.ActiveRecording;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import de.bukkitnews.replay.module.replay.data.recording.Recording;
import de.bukkitnews.replay.module.replay.database.objects.RecordableObject;
import de.bukkitnews.replay.module.replay.database.objects.RecordingObject;
import de.bukkitnews.replay.module.replay.task.TickTrackerTask;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class RecordingHandler {

    private final RecordingObject recordingObject;
    private final RecordableObject recordableObject;
    // Recordings currently being recorded
    private final List<ActiveRecording> activeRecordings = new ArrayList<>();
    private static final int MAX_RECORDABLES = 250;

    public RecordingHandler(RecordingObject recordingObject, RecordableObject recordableObject) {
        this.recordingObject = recordingObject;
        this.recordableObject = recordableObject;
    }

    /**
     * Gets the active recording for a player, if any.
     *
     * @param player the player whose active recording to find
     * @return the active recording, if any, for the player
     */
    public Optional<ActiveRecording> getPlayerActiveRecording(Player player) {
        return activeRecordings.stream()
                .filter(recording -> recording.getRecording().getOwner().equals(player.getUniqueId()))
                .findFirst();
    }

    /**
     * Gets the active recording for an entity, if any.
     *
     * @param entity the entity being tracked
     * @return the active recording, if any, for the entity
     */
    public Optional<ActiveRecording> getEntities(Entity entity) {
        return activeRecordings.stream()
                .filter(recording -> recording.isEntityBeingTracked(entity.getUniqueId()))
                .findFirst();
    }

    /**
     * Starts recording for a player with the specified camera.
     *
     * @param player the player who starts recording
     * @param recordingArea the camera used to record
     */
    public void startRecording(Player player, RecordingArea recordingArea) {
        ActiveRecording activeRecording = new ActiveRecording(recordingArea, player);
        activeRecordings.add(activeRecording);

        recordingObject.insert(activeRecording.getRecording());

        player.sendMessage(MessageUtil.getMessage("recording_started1", recordingArea.getName()));
        player.sendMessage(MessageUtil.getMessage("recording_started2"));
    }

    /**
     * Stops the recording for the specified player.
     *
     * @param player the player who is stopping the recording
     */
    public void stopRecording(Player player) {
        Optional<ActiveRecording> activeRecordingOpt = getPlayerActiveRecording(player);

        if (activeRecordingOpt.isEmpty()) {
            player.sendMessage(MessageUtil.getMessage("recording_stop_no"));
            return;
        }

        ActiveRecording activeRecording = activeRecordingOpt.get();
        activeRecordings.remove(activeRecording);
        activeRecording.stopRecording();

        flushRecordables(activeRecording);
        recordingObject.update(activeRecording.getRecording());

        player.sendMessage(MessageUtil.getMessage("recording_stopped"));
    }

    /**
     * Adds a recordable object to the active recording.
     * If the buffer exceeds the maximum size, flushes the recordables.
     *
     * @param activeRecording the active recording
     * @param recordable the recordable object to add
     */
    public void addRecordable(ActiveRecording activeRecording, Recordable recordable) {
        Recording recording = activeRecording.getRecording();
        long currentTick = TickTrackerTask.getCurrentTick() - recording.getStartTick();

        recordable.setTick(currentTick);
        recordable.setRecordingId(activeRecording.getRecording().getId());
        activeRecording.getRecordableBuffer().add(recordable);

        // See if the buffer should be flushed
        if (activeRecording.getRecordableBuffer().size() >= MAX_RECORDABLES && activeRecording.tryStartFlushing()) {
            flushRecordables(activeRecording);
        }
    }

    /**
     * Flushes the recordable buffer asynchronously to the database.
     *
     * @param activeRecording the active recording whose buffer to flush
     */
    public void flushRecordables(ActiveRecording activeRecording) {
        Bukkit.getScheduler().runTaskAsynchronously(ReplayModule.instance.getReplaySystem(), () -> {
            Queue<Recordable> bufferCopy;
            synchronized (this) {
                bufferCopy = new LinkedList<>(activeRecording.getRecordableBuffer());
                activeRecording.getRecordableBuffer().clear();
            }
            List<Recordable> listToFlush = new ArrayList<>(bufferCopy);
            recordableObject.insertMany(listToFlush);
            activeRecording.endFlushing();
            System.out.println("Flushed RecordableBuffer");
        });
    }

    /**
     * Gets the list of recordings associated with a camera.
     *
     * @param recordingArea the camera whose recordings to find
     * @return the list of recordings for the camera
     */
    public List<Recording> getRecordingsForCamera(RecordingArea recordingArea) {
        return recordingObject.getCameraRecordings(recordingArea.getId());
    }

    /**
     * Finds a recording by its ID.
     *
     * @param id the ID of the recording
     * @return the recording if found, otherwise null
     */
    public Recording findById(String id) {
        return recordingObject.findById(new ObjectId(id));
    }
}