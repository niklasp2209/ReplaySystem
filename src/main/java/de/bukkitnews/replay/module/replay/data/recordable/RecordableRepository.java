package de.bukkitnews.replay.module.replay.data.recordable;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import de.bukkitnews.replay.module.database.AbstractMongoRepository;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RecordableRepository extends AbstractMongoRepository<Recordable> {

    public RecordableRepository(@NotNull MongoCollection<Recordable> collection) {
        super(collection);
    }


    @Override
    public @Nullable Recordable findById(@NotNull ObjectId id) {
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
    public @NotNull List<Recordable> findInTickRange(@NotNull ObjectId id, long startTick, long endTick) {
        return List.copyOf(collection.find(Filters.and(
                        Filters.eq("recordingId", id),
                        Filters.gte("tick", startTick),
                        Filters.lte("tick", endTick)))
                .sort(Sorts.ascending("tick"))
                .into(new ArrayList<>()));
    }

    public void insertMany(@NotNull List<Recordable> recordables) {
        collection.insertMany(recordables);
    }

    @Override
    protected @NotNull ObjectId extractId(@NotNull Recordable entity) {
        return entity.getId();
    }
}