package de.bukkitnews.replay.module.replay.database.objects;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import de.bukkitnews.replay.api.DatabaseAPI;
import de.bukkitnews.replay.module.database.mongodb.MongoConnectionManager;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Database object for managing recordables in MongoDB.
 */
public class RecordableObject implements DatabaseAPI<Recordable> {

    private final MongoCollection<Recordable> collection;

    /**
     * Constructor to initialize the collection.
     *
     * @param mongoDatabaseService The MongoService instance for accessing the database.
     */
    public RecordableObject(MongoConnectionManager mongoDatabaseService) {
        this.collection = mongoDatabaseService.getDatabase()
                .getCollection("recordables", Recordable.class);
    }

    /**
     * Inserts a new recordable into the database.
     *
     * @param entity The recordable to be inserted.
     */
    @Override
    public void insert(Recordable entity) {
        collection.insertOne(entity);
    }

    /**
     * Finds a recordable by its ID.
     *
     * @param id The ObjectId of the recordable to be found.
     * @return The found recordable or null if not found.
     */
    @Override
    public Recordable findById(ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    /**
     * Retrieves all recordables from the database.
     *
     * @return A list of all recordables.
     */
    @Override
    public List<Recordable> findAll() {
        return List.copyOf(collection.find().into(new ArrayList<>()));
    }

    /**
     * Updates an existing recordable in the database.
     * Currently not implemented.
     *
     * @param entity The recordable to be updated.
     */
    @Override
    public void update(Recordable entity) {
        // Not implemented yet
    }

    /**
     * Deletes a recordable from the database.
     * Currently not implemented.
     *
     * @param entity The recordable to be deleted.
     */
    @Override
    public void delete(Recordable entity) {
        // Not implemented yet
    }

    /**
     * Finds recordables by recording ID and tick range.
     *
     * @param id The ObjectId of the recording.
     * @param startTick The starting tick of the range.
     * @param endTick The ending tick of the range.
     * @return A list of recordables within the specified tick range for the given recording ID.
     */
    public List<Recordable> findByRecordingIdAndTickBetween(ObjectId id, long startTick, long endTick) {
        return List.copyOf(collection.find(Filters.and(
                        Filters.eq("recordingId", id),
                        Filters.gte("tick", startTick),
                        Filters.lte("tick", endTick)))
                .sort(Sorts.ascending("tick"))
                .into(new ArrayList<>()));
    }

    /**
     * Inserts many recordables into the database.
     *
     * @param recordables A list of recordables to be inserted.
     */
    public void insertMany(List<Recordable> recordables) {
        collection.insertMany(recordables);
    }
}