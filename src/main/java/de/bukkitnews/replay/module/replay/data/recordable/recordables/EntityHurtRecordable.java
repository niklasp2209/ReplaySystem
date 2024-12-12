package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerHurtAnimation;
import de.bukkitnews.replay.framework.exception.EntityNotFoundException;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.Optional;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "EntityHurt")
public class EntityHurtRecordable extends Recordable {

    private UUID bukkitEntityId;

    /**
     * Replays the entity hurt action by sending a hurt animation packet.
     *
     * @param replay the replay instance handling the replay process
     * @param user the user to whom the hurt animation packet should be sent
     * @throws EntityNotFoundException if the entity is not found in the replay's spawned entities
     */
    @Override
    public void replay(Replay replay, User user) throws EntityNotFoundException {
        Optional<Integer> entityOptional = Optional.ofNullable(replay.getSpawnedEntities().get(bukkitEntityId));

        if (!entityOptional.isPresent()) {
            throw new EntityNotFoundException("Entity with UUID " + bukkitEntityId + " not found.");
        }

        WrapperPlayServerHurtAnimation damagePacket = new WrapperPlayServerHurtAnimation(entityOptional.get(), 90F);
        user.sendPacket(damagePacket);
    }
}