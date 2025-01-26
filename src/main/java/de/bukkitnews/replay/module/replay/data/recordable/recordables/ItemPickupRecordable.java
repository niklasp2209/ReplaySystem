package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCollectItem;
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
@BsonDiscriminator(key = "type", value = "ItemPickup")
public class ItemPickupRecordable extends Recordable {

    private @NotNull UUID collectorBukkitEntityId;
    private @NotNull UUID itemBukkitEntityId;

    /**
     * Replays the item pickup action by sending the collect item packet.
     *
     * @param replay the replay instance handling the replay process
     * @param user   the user to whom the packet should be sent
     */
    @Override
    public void replay(@NotNull Replay replay, @NotNull User user) {
        Integer collectorEntityId = replay.getSpawnedEntities().get(collectorBukkitEntityId);
        Integer itemEntityId = replay.getSpawnedEntities().get(itemBukkitEntityId);

        user.sendPacket(new WrapperPlayServerCollectItem(itemEntityId, collectorEntityId, 1));
    }
}
