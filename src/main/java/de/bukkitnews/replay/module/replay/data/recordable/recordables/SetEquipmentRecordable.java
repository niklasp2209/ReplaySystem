package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import de.bukkitnews.replay.framework.exception.EntityNotFoundException;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "SetEquipment")
public class SetEquipmentRecordable extends Recordable {

    private UUID bukkitEntityId;
    private ItemStack mainHand;
    private ItemStack offHand;
    private ItemStack helmet;
    private ItemStack chest;
    private ItemStack legs;
    private ItemStack boots;

    /**
     * Replays the equipment change for an entity by sending the appropriate packets.
     *
     * @param replay the replay instance that manages the replayed events
     * @param user the user to whom the equipment change packets should be sent
     * @throws EntityNotFoundException if the entity with the given bukkitEntityId is not found
     */
    @Override
    public void replay(@NonNull Replay replay, @NonNull User user) throws EntityNotFoundException {
        Integer entityId = replay.getSpawnedEntities().get(bukkitEntityId);

        if (entityId == null) {
            throw new EntityNotFoundException("Entity with bukkitEntityId " + bukkitEntityId + " not found.");
        }

        List<Equipment> equipment = new ArrayList<>();
        addEquipmentToList(equipment, EquipmentSlot.MAIN_HAND, mainHand);
        addEquipmentToList(equipment, EquipmentSlot.OFF_HAND, offHand);
        addEquipmentToList(equipment, EquipmentSlot.HELMET, helmet);
        addEquipmentToList(equipment, EquipmentSlot.CHEST_PLATE, chest);
        addEquipmentToList(equipment, EquipmentSlot.LEGGINGS, legs);
        addEquipmentToList(equipment, EquipmentSlot.BOOTS, boots);

        WrapperPlayServerEntityEquipment entityEquipmentPacket = new WrapperPlayServerEntityEquipment(entityId, equipment);
        user.sendPacket(entityEquipmentPacket);
    }

    /**
     * Adds an equipment item to the list if it is not null.
     *
     * @param equipmentList the list of equipment items
     * @param slot the equipment slot for the item
     * @param item the item to add to the equipment list
     */
    private void addEquipmentToList(@NonNull List<Equipment> equipmentList, @NonNull EquipmentSlot slot, @Nullable ItemStack item) {
        if (item != null) {
            equipmentList.add(new Equipment(slot, SpigotConversionUtil.fromBukkitItemStack(item)));
        }
    }
}
