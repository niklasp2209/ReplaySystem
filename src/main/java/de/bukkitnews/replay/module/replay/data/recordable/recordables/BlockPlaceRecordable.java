package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "BlockPlace")
public class BlockPlaceRecordable extends Recordable {

    private @Nullable Material material;
    private @Nullable Location location;

    /**
     * Replays the block placement action to the specified user by sending
     * a block change packet.
     *
     * @param replay the replay instance handling the replay process
     * @param user the user to whom the block change should be sent
     */
    @Override
    public void replay(@NotNull Replay replay, @NotNull User user) {
        if (material == null || location == null) {
            return;
        }

        Vector3i position = new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        StateType stateType = StateTypes.getByName("minecraft:" + material.name().toLowerCase());
        WrappedBlockState wrappedBlockState = WrappedBlockState.getDefaultState(stateType);

        user.sendPacket(new WrapperPlayServerBlockChange(position, wrappedBlockState.getGlobalId()));
    }
}
