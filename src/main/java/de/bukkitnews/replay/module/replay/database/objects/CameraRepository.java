package de.bukkitnews.replay.module.replay.database.objects;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.bukkitnews.replay.module.database.AbstractMongoRepository;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import lombok.NonNull;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CameraRepository extends AbstractMongoRepository<RecordingArea> {

    public CameraRepository(@NonNull MongoCollection<RecordingArea> collection) {
        super(collection);
    }

    @Override
    protected ObjectId extractId(@NonNull RecordingArea entity) {
        return entity.getId();
    }

    /**
     * Finds all RecordingArea entities owned by a specific user.
     *
     * @param owner The UUID of the owner.
     * @return A list of RecordingArea entities owned by the user.
     */
    public List<RecordingArea> findAllByOwnerId(@NonNull UUID owner) {
        return List.copyOf(collection.find(Filters.eq("owner", owner)).into(new ArrayList<>()));
    }
}