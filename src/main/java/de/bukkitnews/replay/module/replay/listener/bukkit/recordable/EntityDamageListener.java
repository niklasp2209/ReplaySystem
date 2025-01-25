package de.bukkitnews.replay.module.replay.listener.bukkit.recordable;

import de.bukkitnews.replay.module.replay.data.recordable.recordables.EntityHurtRecordable;
import de.bukkitnews.replay.module.replay.handle.RecordingHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class EntityDamageListener extends RecordableEvent<EntityDamageEvent> {

    public EntityDamageListener(@NotNull RecordingHandler recordingHandler) {
        super(recordingHandler, ((event, activeRecording) ->
                new EntityHurtRecordable(event.getEntity().getUniqueId())));
    }
}
