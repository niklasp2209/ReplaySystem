package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.jetbrains.annotations.NotNull;

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

    private @NotNull UUID bukkitEntityId;
    private boolean isSprinting;

    /**
     * Replays the sprinting status of the entity by sending the appropriate metadata packet.
     *
     * @param replay the replay instance
     * @param user   the user receiving the packet
     */
    @Override
    public void replay(@NotNull Replay replay, @NotNull User user) {
        Integer entityId = replay.getSpawnedEntities().get(bukkitEntityId);

        List<EntityData> entityDataList = new ArrayList<>();
        byte sprintingByte = isSprinting ? SPRINTING_FLAG : 0;
        entityDataList.add(new EntityData(0, EntityDataTypes.BYTE, sprintingByte));

        user.sendPacket(new WrapperPlayServerEntityMetadata(entityId, entityDataList));
    }
}