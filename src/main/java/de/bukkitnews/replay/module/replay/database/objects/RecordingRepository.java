package de.bukkitnews.replay.module.replay.database.objects;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import de.bukkitnews.replay.module.database.AbstractMongoRepository;
import de.bukkitnews.replay.module.replay.data.recording.Recording;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for "recordings" in MongoDB.
 */
public class RecordingRepository extends AbstractMongoRepository<Recording> {

    public RecordingRepository(@NotNull MongoCollection<Recording> collection) {
        super(collection);
    }

    @Override
    protected ObjectId extractId(@NotNull Recording entity) {
        return entity.getId();
    }

    /**
     * Retrieves recordings by camera ID, sorted by start time (descending).
     *
     * @param cameraId The camera's ObjectId.
     * @return A list of recordings for the camera.
     */
    public List<Recording> getCameraRecordings(@NotNull ObjectId cameraId) {
        return List.copyOf(collection.find(Filters.eq("cameraId", cameraId))
                .sort(Sorts.descending("startTime"))
                .into(new ArrayList<>()));
    }
}