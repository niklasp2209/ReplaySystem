package de.bukkitnews.replay.module.replay.listener.bukkit.recordable;

import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.recording.ActiveRecording;
import de.bukkitnews.replay.module.replay.handle.RecordingHandler;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public abstract class RecordableEvent<E extends Event> implements Listener {

    private final @NotNull RecordingHandler recordingHandler;
    private final @NotNull BiFunction<E, ActiveRecording, Recordable> biFunction;

    @EventHandler
    public void on(E event) {
        if (!(event instanceof PlayerEvent || event instanceof EntityEvent)) {
            return;
        }

        Entity entity = (event instanceof PlayerEvent
                ? ((PlayerEvent) event).getPlayer()
                : ((EntityEvent) event).getEntity());
        Optional<ActiveRecording> activeRecordingOpt = recordingHandler.getEntities(entity);

        activeRecordingOpt.ifPresent(activeRecording -> {
            Recordable recordable = biFunction.apply(event, activeRecording);
            recordingHandler.addRecordable(activeRecording, recordable);
        });
    }
}
