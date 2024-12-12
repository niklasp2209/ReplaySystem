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
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "BlockBreak")
public class BlockBreakRecordable extends Recordable {

    private Location location;

    /**
     * Replays the block break event for the specified user within the given replay.
     *
     * @param replay the replay context
     * @param user   the user to send the packet to
     * @throws IllegalArgumentException if the location is null
     */
    @Override
    public void replay(Replay replay, User user) {
        Objects.requireNonNull(location, "Location must not be null");

        Vector3i position = new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        WrappedBlockState wrappedBlockState = WrappedBlockState.getDefaultState(StateTypes.AIR);

        WrapperPlayServerBlockChange blockChangePacket =
                new WrapperPlayServerBlockChange(position, wrappedBlockState.getGlobalId());

        user.sendPacket(blockChangePacket);
    }
}