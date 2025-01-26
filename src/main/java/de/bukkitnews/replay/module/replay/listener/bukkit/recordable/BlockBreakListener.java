package de.bukkitnews.replay.module.replay.listener.bukkit.recordable;

import de.bukkitnews.replay.module.replay.data.recordable.recordables.BlockBreakRecordable;
import de.bukkitnews.replay.module.replay.handler.RecordingHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BlockBreakListener extends RecordableEvent<BlockBreakEvent> {

    public BlockBreakListener(@NotNull RecordingHandler recordingHandler) {
        super(recordingHandler, ((event, activeRecording) ->
                new BlockBreakRecordable(event.getBlock().getLocation())));
    }
}
