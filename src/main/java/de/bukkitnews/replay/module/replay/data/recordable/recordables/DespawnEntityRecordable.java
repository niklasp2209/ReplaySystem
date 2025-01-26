package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "DespawnEntity")
public class DespawnEntityRecordable extends Recordable {

    private @NotNull UUID bukkitEntityId;

    /**
     * Replays the despawn action of an entity by sending a destroy entity packet.
     *
     * @param replay the replay instance handling the replay process
     * @param user the user to whom the entity despawn packet should be sent
     */
    @Override
    public void replay(@NotNull Replay replay, @NotNull User user) {
        Optional<Integer> entityOptional = Optional.ofNullable(replay.getSpawnedEntities().get(bukkitEntityId));

        if(entityOptional.isEmpty()){
            return;
        }

        user.sendPacket(new WrapperPlayServerDestroyEntities(entityOptional.get()));
        replay.getSpawnedEntities().remove(bukkitEntityId);
    }
}