package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import de.bukkitnews.replay.framework.exception.EntityNotFoundException;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "EntitySprint")
public class SprintRecordable extends Recordable {

    private static final byte SPRINTING_FLAG = 0x08;

    private UUID bukkitEntityId;
    private boolean isSprinting;

    /**
     * Replays the sprinting status of the entity by sending the appropriate metadata packet.
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

        List<EntityData> entityDataList = new ArrayList<>();
        byte sprintingByte = isSprinting ? SPRINTING_FLAG : 0;
        entityDataList.add(new EntityData(0, EntityDataTypes.BYTE, sprintingByte));

        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(entityId, entityDataList);
        user.sendPacket(metadataPacket);
    }
}