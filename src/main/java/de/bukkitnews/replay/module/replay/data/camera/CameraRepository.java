package de.bukkitnews.replay.module.replay.data.camera;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.bukkitnews.replay.module.database.AbstractMongoRepository;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CameraRepository extends AbstractMongoRepository<RecordingArea> {

    public CameraRepository(@NotNull MongoCollection<RecordingArea> collection) {
        super(collection);
    }

    @Override
    protected @NotNull ObjectId extractId(@NotNull RecordingArea entity) {
        return entity.getId();
    }

    /**
     * Finds all RecordingArea entities owned by a specific user.
     *
     * @param owner The UUID of the owner.
     * @return A list of RecordingArea entities owned by the user.
     */
    public @NotNull List<RecordingArea> findAllByOwnerId(@NotNull UUID owner) {
        return List.copyOf(collection.find(Filters.eq("owner", owner)).into(new ArrayList<>()));
    }
}