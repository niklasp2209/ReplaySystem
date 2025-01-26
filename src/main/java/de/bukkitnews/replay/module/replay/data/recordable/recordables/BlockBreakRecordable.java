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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "BlockBreak")
public class BlockBreakRecordable extends Recordable {

    private @Nullable Location location;

    /**
     * Replays the block break event for the specified user within the given replay.
     *
     * @param replay the replay context
     * @param user   the user to send the packet to
     */
    @Override
    public void replay(@NotNull Replay replay, @NotNull User user) {
        if (location == null) {
            return;
        }

        Vector3i position = new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        WrappedBlockState wrappedBlockState = WrappedBlockState.getDefaultState(StateTypes.AIR);

        user.sendPacket(new WrapperPlayServerBlockChange(position, wrappedBlockState.getGlobalId()));
    }
}