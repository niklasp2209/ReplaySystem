package de.bukkitnews.replay.module.replay.database.objects;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import de.bukkitnews.replay.module.database.AbstractMongoRepository;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import lombok.NonNull;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class RecordableRepository extends AbstractMongoRepository<Recordable> {

    public RecordableRepository(@NonNull MongoCollection<Recordable> collection) {
        super(collection);
    }


    @Override
    public Recordable findById(@NonNull ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    /**
     * Finds recordables by recording ID and tick range.
     *
     * @param id        The ObjectId of the recording.
     * @param startTick The start of the tick range.
     * @param endTick   The end of the tick range.
     * @return A list of matching recordables, sorted by tick.
     */
    public List<Recordable> findByRecordingIdAndTickBetween(@NonNull ObjectId id, long startTick, long endTick) {
        return List.copyOf(collection.find(Filters.and(
                        Filters.eq("recordingId", id),
                        Filters.gte("tick", startTick),
                        Filters.lte("tick", endTick)))
                .sort(Sorts.ascending("tick"))
                .into(new ArrayList<>()));
    }

    public void insertMany(@NonNull List<Recordable> recordables) {
        collection.insertMany(recordables);
    }

    @Override
    protected ObjectId extractId(@NonNull Recordable entity) {
        return entity.getId();
    }
}