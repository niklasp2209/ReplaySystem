package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Location;
import org.bukkit.Material;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "BlockPlace")
public class BlockPlaceRecordable extends Recordable {

    private Material material;
    private Location location;

    /**
     * Replays the block placement action to the specified user by sending
     * a block change packet.
     *
     * @param replay the replay instance handling the replay process
     * @param user the user to whom the block change should be sent
     * @throws IllegalArgumentException if material or location is null
     */
    @Override
    public void replay(Replay replay, User user) throws IllegalArgumentException {
        if (material == null || location == null) {
            throw new IllegalArgumentException("Material or Location cannot be null.");
        }

        Vector3i position = new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        var stateType = StateTypes.getByName("minecraft:" + material.name().toLowerCase());
        var wrappedBlockState = WrappedBlockState.getDefaultState(stateType);

        WrapperPlayServerBlockChange blockChangePacket = new WrapperPlayServerBlockChange(position, wrappedBlockState.getGlobalId());
        user.sendPacket(blockChangePacket);
    }
}
