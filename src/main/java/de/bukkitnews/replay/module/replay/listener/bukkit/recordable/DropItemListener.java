package de.bukkitnews.replay.module.replay.listener.bukkit.recordable;

import de.bukkitnews.replay.module.replay.data.recordable.recordables.ItemDropRecordable;
import de.bukkitnews.replay.module.replay.handler.RecordingHandler;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

public class DropItemListener extends RecordableEvent<PlayerDropItemEvent> {

    public DropItemListener(@NotNull RecordingHandler recordingHandler) {
        super(recordingHandler, ((event, activeRecording) -> {
            Item itemDrop = event.getItemDrop();
            activeRecording.addEntityIfNotAlreadyTracked(itemDrop.getUniqueId());

            return new ItemDropRecordable(itemDrop.getUniqueId(), itemDrop.getLocation(), itemDrop.getItemStack());
        }));
    }
}
