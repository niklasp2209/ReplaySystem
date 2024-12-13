package de.bukkitnews.replay.module.replay.database.objects;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import de.bukkitnews.replay.api.DatabaseAPI;
import de.bukkitnews.replay.module.database.mongodb.MongoConnectionManager;
import de.bukkitnews.replay.module.replay.data.recording.Recording;
import lombok.NonNull;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Database object for managing recordings in MongoDB.
 */
public class RecordingObject implements DatabaseAPI<Recording> {

    private final MongoCollection<Recording> collection;

    /**
     * Constructor to initialize the collection.
     *
     * @param mongoDatabaseService The MongoService instance for accessing the database.
     */
    public RecordingObject(@NonNull MongoConnectionManager mongoDatabaseService) {
        this.collection = mongoDatabaseService.getDatabase().getCollection("recordings", Recording.class);
    }

    /**
     * Inserts a new recording into the database.
     *
     * @param entity The recording to be inserted.
     */
    @Override
    public void insert(@NonNull Recording entity) {
        collection.insertOne(entity);
    }

    /**
     * Finds a recording by its ID.
     *
     * @param id The ObjectId of the recording to be found.
     * @return The found recording or null if not found.
     */
    @Override
    public Recording findById(@NonNull ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    /**
     * Retrieves all recordings from the database.
     *
     * @return A list of all recordings.
     */
    @Override
    public List<Recording> findAll() {
        return List.copyOf(collection.find().into(new ArrayList<>()));
    }

    /**
     * Updates an existing recording's information in the database.
     *
     * @param entity The recording with updated data.
     */
    @Override
    public void update(@NonNull Recording entity) {
        Bson updates = Updates.combine(
                Updates.set("endTime", entity.getEndTime()),
                Updates.set("endTick", entity.getEndTick())
        );
        collection.updateOne(Filters.eq("_id", entity.getId()), updates);
    }



    /**
     * Deletes a recording from the database.
     *
     * @param entity The recording to be deleted.
     */
    @Override
    public void delete(@NonNull Recording entity) {
        collection.deleteOne(Filters.eq("_id", entity.getId()));
    }

    /**
     * Retrieves all recordings associated with a specific camera.
     *
     * @param cameraId The ObjectId of the camera.
     * @return A list of recordings for the specified camera.
     */
    public List<Recording> getCameraRecordings(@NonNull ObjectId cameraId) {
        return List.copyOf(collection.find(Filters.eq("cameraId", cameraId))
                .sort(Sorts.descending("startTime"))
                .into(new ArrayList<>()));
    }
}
