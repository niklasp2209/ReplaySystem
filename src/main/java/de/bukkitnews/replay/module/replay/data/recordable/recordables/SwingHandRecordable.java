package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "SwingHand")
public class SwingHandRecordable extends Recordable {

    private @NotNull UUID bukkitEntityId;
    private int handId;

    /**
     * Replays the hand swing animation for the entity (either main or off-hand).
     *
     * @param replay the replay instance
     * @param user   the user receiving the packet
     */
    @Override
    public void replay(@NotNull Replay replay, @NotNull User user) {
        Integer entityId = replay.getSpawnedEntities().get(bukkitEntityId);

        WrapperPlayServerEntityAnimation.EntityAnimationType entityAnimation = handId == 0
                ? WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM
                : WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_OFF_HAND;

        user.sendPacket(new WrapperPlayServerEntityAnimation(entityId, entityAnimation));
    }
}
