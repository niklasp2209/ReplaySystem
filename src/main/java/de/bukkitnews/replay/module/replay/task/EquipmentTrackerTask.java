package de.bukkitnews.replay.module.replay.task;

import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recordable.recordables.SetEquipmentRecordable;
import de.bukkitnews.replay.module.replay.data.recording.ActiveRecording;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.UUID;

@RequiredArgsConstructor
public class EquipmentTrackerTask implements Runnable {

    private final @NotNull ActiveRecording activeRecording;
    private final @NotNull ReplayModule replayModule;

    @Override
    public void run() {
        Queue<UUID> recordableEntities = activeRecording.getRecordableEntities();

        synchronized (recordableEntities) {
            recordableEntities.forEach(entityUUID -> {
                Entity entity = Bukkit.getEntity(entityUUID);
                if (entity instanceof LivingEntity livingEntity && livingEntity.getEquipment() != null) {
                    SetEquipmentRecordable setEquipmentRecordable = new SetEquipmentRecordable(
                            livingEntity.getUniqueId(),
                            livingEntity.getEquipment().getItemInMainHand(),
                            livingEntity.getEquipment().getItemInOffHand(),
                            livingEntity.getEquipment().getHelmet(),
                            livingEntity.getEquipment().getChestplate(),
                            livingEntity.getEquipment().getLeggings(),
                            livingEntity.getEquipment().getBoots()
                    );
                    replayModule.getRecordingHandler().addRecordable(activeRecording, setEquipmentRecordable);
                }
            });
        }
    }
}