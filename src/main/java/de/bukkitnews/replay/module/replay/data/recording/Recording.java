package de.bukkitnews.replay.module.replay.data.recording;

import de.bukkitnews.replay.module.replay.task.TickTrackerTask;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Recording {

    private @NotNull ObjectId id;
    private final @NotNull ObjectId cameraId;
    private final @NotNull UUID owner;
    private long startTime;
    private long endTime;
    private long startTick;
    private long endTick;
    private @NotNull List<Material> originalBlocks;

    /**
     * Creates a new recording for the specified camera and owner.
     *
     * @param recordingArea The camera associated with this recording.
     * @param owner         The player who owns the recording.
     */
    public Recording(@NotNull RecordingArea recordingArea, @NotNull Player owner) {
        this.cameraId = recordingArea.getId();
        this.owner = owner.getUniqueId();
        this.startTime = System.currentTimeMillis();
        this.startTick = TickTrackerTask.getCurrentTick();
        this.originalBlocks = new ArrayList<>();
    }

    public long getTickDuration() {
        return endTick - startTick;
    }
}