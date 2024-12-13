package de.bukkitnews.replay.module.replay.database.objects;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.bukkitnews.replay.api.DatabaseAPI;
import de.bukkitnews.replay.module.database.mongodb.MongoConnectionManager;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import lombok.NonNull;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CameraObject implements DatabaseAPI<RecordingArea> {

    private final MongoCollection<RecordingArea> collection;

    /**
     * Constructor to initialize the collection.
     *
     * @param mongoDatabaseService The MongoService instance for accessing the database.
     */
    public CameraObject(@NonNull MongoConnectionManager mongoDatabaseService) {
        this.collection = mongoDatabaseService.getDatabase().getCollection("cameras", RecordingArea.class);
    }

    /**
     * Inserts a new camera into the database.
     *
     * @param recordingArea The camera to be inserted.
     */
    @Override
    public void insert(@NonNull RecordingArea recordingArea) {
        collection.insertOne(recordingArea);
    }

    /**
     * Finds a camera by its ID.
     *
     * @param id The ObjectId of the camera to be found.
     * @return The found camera or null if not found.
     */
    @Override
    public RecordingArea findById(@NonNull ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    /**
     * Retrieves all cameras from the database.
     *
     * @return A list of all cameras.
     */
    @Override
    public List<RecordingArea> findAll() {
        return new ArrayList<>(collection.find().into(new ArrayList<>()));
    }

    /**
     * Updates an existing camera's information in the database.
     *
     * @param recordingArea The camera with updated data.
     */
    @Override
    public void update(@NonNull RecordingArea recordingArea) {
        Bson updates = Updates.combine(
                Updates.set("name", recordingArea.getName())
        );
        collection.updateOne(Filters.eq("_id", recordingArea.getId()), updates);
    }

    /**
     * Deletes a camera from the database.
     *
     * @param recordingArea The camera to be deleted.
     */
    @Override
    public void delete(@NonNull RecordingArea recordingArea) {
        collection.deleteOne(Filters.eq("_id", recordingArea.getId()));
    }

    /**
     * Finds all cameras owned by a specific player.
     *
     * @param owner The UUID of the owner.
     * @return A list of cameras owned by the specified player.
     */
    public List<RecordingArea> findAllByOwnerId(@NonNull UUID owner) {
        return new ArrayList<>(collection.find(Filters.eq("owner", owner)).into(new ArrayList<>()));
    }
}