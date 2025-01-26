package de.bukkitnews.replay.module.replay.data.recordable;

import com.github.retrooper.packetevents.protocol.player.User;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@BsonDiscriminator(key = "type", value = "Recordable")
public abstract class Recordable {

    private @NotNull ObjectId id;
    private @NotNull ObjectId recordingId;
    private long tick;

    /**
     * This method is called to replay the recorded action for a user.
     *
     * @param replay The replay instance to use.
     * @param user   The user for whom the replay will be played.
     */
    public abstract void replay(Replay replay, User user);
}