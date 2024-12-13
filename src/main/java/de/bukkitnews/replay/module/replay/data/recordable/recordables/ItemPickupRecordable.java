package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCollectItem;
import de.bukkitnews.replay.framework.exception.EntityNotFoundException;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "ItemPickup")
public class ItemPickupRecordable extends Recordable {

    private UUID collectorBukkitEntityId;
    private UUID itemBukkitEntityId;

    /**
     * Replays the item pickup action by sending the collect item packet.
     *
     * @param replay the replay instance handling the replay process
     * @param user the user to whom the packet should be sent
     * @throws EntityNotFoundException if the entities for the collector or item are not found in the replay
     */
    @Override
    public void replay(@NonNull Replay replay, @NonNull User user) throws EntityNotFoundException {
        Integer collectorEntityId = replay.getSpawnedEntities().get(collectorBukkitEntityId);
        Integer itemEntityId = replay.getSpawnedEntities().get(itemBukkitEntityId);

        if (collectorEntityId == null || itemEntityId == null) {
            throw new EntityNotFoundException("Entities for collector or item not found in replay.");
        }

        WrapperPlayServerCollectItem packet = new WrapperPlayServerCollectItem(itemEntityId, collectorEntityId, 1);
        user.sendPacket(packet);
    }
}
