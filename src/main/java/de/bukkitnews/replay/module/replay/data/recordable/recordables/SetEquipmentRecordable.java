package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
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

    private @NotNull UUID bukkitEntityId;
    private @Nullable ItemStack mainHand;
    private @Nullable ItemStack offHand;
    private @Nullable ItemStack helmet;
    private @Nullable ItemStack chest;
    private @Nullable ItemStack legs;
    private @Nullable ItemStack boots;

    /**
     * Replays the equipment change for an entity by sending the appropriate packets.
     *
     * @param replay the replay instance that manages the replayed events
     * @param user   the user to whom the equipment change packets should be sent
     */
    @Override
    public void replay(@NotNull Replay replay, @NotNull User user) {
        Integer entityId = replay.getSpawnedEntities().get(bukkitEntityId);

        List<Equipment> equipment = new ArrayList<>();
        addEquipmentToList(equipment, EquipmentSlot.MAIN_HAND, mainHand);
        addEquipmentToList(equipment, EquipmentSlot.OFF_HAND, offHand);
        addEquipmentToList(equipment, EquipmentSlot.HELMET, helmet);
        addEquipmentToList(equipment, EquipmentSlot.CHEST_PLATE, chest);
        addEquipmentToList(equipment, EquipmentSlot.LEGGINGS, legs);
        addEquipmentToList(equipment, EquipmentSlot.BOOTS, boots);

        user.sendPacket(new WrapperPlayServerEntityEquipment(entityId, equipment));
    }

    /**
     * Adds an equipment item to the list if it is not null.
     *
     * @param equipmentList the list of equipment items
     * @param slot          the equipment slot for the item
     * @param item          the item to add to the equipment list
     */
    private void addEquipmentToList(@NotNull List<Equipment> equipmentList, @NotNull EquipmentSlot slot, @Nullable ItemStack item) {
        if (item == null) {
            return;
        }

        equipmentList.add(new Equipment(slot, SpigotConversionUtil.fromBukkitItemStack(item)));
    }
}
