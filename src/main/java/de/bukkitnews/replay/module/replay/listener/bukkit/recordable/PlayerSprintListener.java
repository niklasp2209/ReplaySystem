package de.bukkitnews.replay.module.replay.listener.bukkit.recordable;

import de.bukkitnews.replay.module.replay.data.recordable.recordables.SprintRecordable;
import de.bukkitnews.replay.module.replay.handle.RecordingHandler;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerSprintListener extends RecordableEvent<PlayerToggleSprintEvent> {

    public PlayerSprintListener(@NotNull RecordingHandler recordingHandler) {
        super(recordingHandler, ((event, activeRecording) ->
                new SprintRecordable(event.getPlayer().getUniqueId(), event.isSprinting())));
    }
}
