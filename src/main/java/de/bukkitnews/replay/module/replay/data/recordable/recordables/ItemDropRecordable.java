package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import de.bukkitnews.replay.exception.EntityCreationException;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private @NotNull UUID bukkitEntityId;
    private @Nullable Location location;
    private @Nullable ItemStack itemStack;

    /**
     * Replays the item drop action by spawning the item and sending metadata.
     *
     * @param replay the replay instance handling the replay process
     * @param user   the user to whom the item drop packets should be sent
     */
    @SneakyThrows
    @Override
    public void replay(@NotNull Replay replay, @NotNull User user) {
        int entityId = generateEntityId();

        if (location == null || itemStack == null) {
            return;
        }

        replay.getSpawnedEntities().put(bukkitEntityId, entityId);

        user.sendPacket(new WrapperPlayServerSpawnEntity(
                entityId, UUID.randomUUID(), EntityTypes.ITEM,
                SpigotConversionUtil.fromBukkitLocation(location),
                0, 0, null));
        user.sendPacket(new WrapperPlayServerEntityMetadata(
                entityId, List.of(new EntityData(0, EntityDataTypes.ITEMSTACK,
                SpigotConversionUtil.fromBukkitItemStack(itemStack)))));
    }

    /**
     * Generates a unique entity ID for the item drop.
     *
     * @return the generated entity ID
     */
    private int generateEntityId() throws EntityCreationException {
        try {
            Class<?> entityClass = Class.forName("net.minecraft.world.entity.Entity");
            Field field = entityClass.getDeclaredField("c");
            field.setAccessible(true);

            AtomicInteger ENTITY_COUNTER = (AtomicInteger) field.get(null);
            return ENTITY_COUNTER.incrementAndGet();
        } catch (ClassNotFoundException | NoSuchFieldException |
                 IllegalAccessException e) {
            throw new EntityCreationException("Failed to generate entity ID", e);
        }
    }
}
