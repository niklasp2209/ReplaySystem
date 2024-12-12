package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import de.bukkitnews.replay.framework.exception.EntityNotFoundException;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "SwingHand")
public class SwingHandRecordable extends Recordable {

    private UUID bukkitEntityId;
    private int handId;

    /**
     * Replays the hand swing animation for the entity (either main or off-hand).
     *
     * @param replay the replay instance
     * @param user   the user receiving the packet
     * @throws EntityNotFoundException if an error occurs during replay
     */
    @Override
    public void replay(Replay replay, User user) throws EntityNotFoundException {
        var entityId = replay.getSpawnedEntities().get(bukkitEntityId);

        if (entityId == null) {
            throw new EntityNotFoundException("Entity with the given bukkitEntityId not found in replay.");
        }

        var entityAnimation = handId == 0 ? WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM :
                WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_OFF_HAND;

        var animationPacket = new WrapperPlayServerEntityAnimation(entityId, entityAnimation);
        user.sendPacket(animationPacket);
    }
}