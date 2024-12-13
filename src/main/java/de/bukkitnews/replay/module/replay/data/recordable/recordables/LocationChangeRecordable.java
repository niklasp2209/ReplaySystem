package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityHeadLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import de.bukkitnews.replay.framework.exception.EntityNotFoundException;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Location;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "LocationChange")
public class LocationChangeRecordable extends Recordable {

    private Location location;
    private UUID bukkitEntityId;

    /**
     * Replays the location and head rotation change for an entity.
     * Sends teleport and head look packets to the user.
     *
     * @param replay the replay instance handling the replay process
     * @param user the user to whom the packets should be sent
     * @throws EntityNotFoundException if the entity with the given bukkitEntityId is not found
     */
    @Override
    public void replay(@NonNull Replay replay, @NonNull User user) throws EntityNotFoundException {
        Integer entityId = replay.getSpawnedEntities().get(bukkitEntityId);
        if (entityId == null) {
            throw new EntityNotFoundException("Entity with the given bukkitEntityId not found in replay.");
        }

        WrapperPlayServerEntityTeleport movePacket = new WrapperPlayServerEntityTeleport(
                entityId, SpigotConversionUtil.fromBukkitLocation(location), false);

        WrapperPlayServerEntityHeadLook headLookPacket = new WrapperPlayServerEntityHeadLook(
                entityId, location.getYaw());

        user.sendPacket(movePacket);
        user.sendPacket(headLookPacket);
    }
}