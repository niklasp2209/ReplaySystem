package de.bukkitnews.replay.module.replay.data.recordable;

import com.github.retrooper.packetevents.protocol.player.User;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.types.ObjectId;

@Getter
@Setter
@BsonDiscriminator(key = "type", value = "Recordable")
public abstract class Recordable {

    private ObjectId id;
    private ObjectId recordingId;
    private long tick;

    /**
     * This method is called to replay the recorded action for a user.
     *
     * @param replay The replay instance to use.
     * @param user   The user for whom the replay will be played.
     */
    public abstract void replay(Replay replay, User user) throws Exception;
}