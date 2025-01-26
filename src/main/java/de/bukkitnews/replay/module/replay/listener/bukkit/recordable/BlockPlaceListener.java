package de.bukkitnews.replay.module.replay.listener.bukkit.recordable;

import de.bukkitnews.replay.module.replay.data.recordable.recordables.BlockPlaceRecordable;
import de.bukkitnews.replay.module.replay.handler.RecordingHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

public class BlockPlaceListener extends RecordableEvent<BlockPlaceEvent> {

    public BlockPlaceListener(@NotNull RecordingHandler recordingHandler) {
        super(recordingHandler, ((event, activeRecording) ->
                new BlockPlaceRecordable(event.getBlock().getType(), event.getBlock().getLocation())));
    }
}
