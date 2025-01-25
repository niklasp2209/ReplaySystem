package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import de.bukkitnews.replay.exception.EntityNotFoundException;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.Optional;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "DespawnEntity")
public class DespawnEntityRecordable extends Recordable {

    private UUID bukkitEntityId;

    /**
     * Replays the despawn action of an entity by sending a destroy entity packet.
     *
     * @param replay the replay instance handling the replay process
     * @param user the user to whom the entity despawn packet should be sent
     * @throws EntityNotFoundException if the entity is not found in the replay's spawned entities
     */
    @Override
    public void replay(@NonNull Replay replay, @NonNull User user) throws EntityNotFoundException {
        Optional<Integer> entityOptional = Optional.ofNullable(replay.getSpawnedEntities().get(bukkitEntityId));

        if (!entityOptional.isPresent()) {
            throw new EntityNotFoundException("Entity with UUID " + bukkitEntityId + " not found.");
        }

        WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(entityOptional.get());
        user.sendPacket(destroyEntities);

        replay.getSpawnedEntities().remove(bukkitEntityId);
    }
}