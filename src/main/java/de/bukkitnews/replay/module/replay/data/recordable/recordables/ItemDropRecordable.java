package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import de.bukkitnews.replay.framework.exception.EntityCreationException;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "ItemDrop")
public class ItemDropRecordable extends Recordable {

    private UUID bukkitEntityId;
    private Location location;
    private ItemStack itemStack;

    /**
     * Replays the item drop action by spawning the item and sending metadata.
     *
     * @param replay the replay instance handling the replay process
     * @param user the user to whom the item drop packets should be sent
     * @throws EntityCreationException if an error occurs while creating the entity
     */
    @Override
    public void replay(Replay replay, User user) throws EntityCreationException {
        try {
            int entityId = generateEntityId();

            WrapperPlayServerSpawnEntity spawnEntityPacket = new WrapperPlayServerSpawnEntity(
                    entityId, UUID.randomUUID(), EntityTypes.ITEM,
                    SpigotConversionUtil.fromBukkitLocation(location),
                    0, 0, null);

            WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(
                    entityId, List.of(new EntityData(0, EntityDataTypes.ITEMSTACK,
                    SpigotConversionUtil.fromBukkitItemStack(itemStack))));

            user.sendPacket(spawnEntityPacket);
            replay.getSpawnedEntities().put(bukkitEntityId, entityId);
            user.sendPacket(metadataPacket);

        } catch (Exception e) {
            throw new EntityCreationException("Error creating entity for item drop: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a unique entity ID for the item drop.
     *
     * @return the generated entity ID
     * @throws EntityCreationException if the entity ID generation fails
     */
    private int generateEntityId() throws EntityCreationException {
        try {
            Class<?> entityClass = Class.forName("net.minecraft.world.entity.Entity");
            Field field = entityClass.getDeclaredField("c");
            field.setAccessible(true);
            AtomicInteger ENTITY_COUNTER = (AtomicInteger) field.get(null);
            return ENTITY_COUNTER.incrementAndGet();
        } catch (Exception e) {
            throw new EntityCreationException("Failed to generate entity ID", e);
        }
    }
}