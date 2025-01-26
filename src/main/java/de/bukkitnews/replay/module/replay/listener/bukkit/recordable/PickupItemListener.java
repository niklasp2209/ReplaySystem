package de.bukkitnews.replay.module.replay.listener.bukkit.recordable;

import de.bukkitnews.replay.module.replay.data.recordable.recordables.ItemPickupRecordable;
import de.bukkitnews.replay.module.replay.handler.RecordingHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.jetbrains.annotations.NotNull;

public class PickupItemListener extends RecordableEvent<EntityPickupItemEvent> {

    public PickupItemListener(@NotNull RecordingHandler recordingHandler) {
        super(recordingHandler, ((event, activeRecording) -> new
                ItemPickupRecordable(event.getEntity().getUniqueId(), event.getItem().getUniqueId())));
    }
}
