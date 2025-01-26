package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityHeadLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "LocationChange")
public class LocationChangeRecordable extends Recordable {

    private @Nullable Location location;
    private @NotNull UUID bukkitEntityId;

    /**
     * Replays the location and head rotation change for an entity.
     * Sends teleport and head look packets to the user.
     *
     * @param replay the replay instance handling the replay process
     * @param user the user to whom the packets should be sent
     */
    @Override
    public void replay(@NotNull Replay replay, @NotNull User user) {
        if(location == null){
            return;
        }

        Integer entityId = replay.getSpawnedEntities().get(bukkitEntityId);

        user.sendPacket(new WrapperPlayServerEntityTeleport(
                entityId, SpigotConversionUtil.fromBukkitLocation(location), false));
        user.sendPacket(new WrapperPlayServerEntityHeadLook(
                entityId, location.getYaw()));
    }
}