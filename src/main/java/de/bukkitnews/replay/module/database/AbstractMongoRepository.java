package de.bukkitnews.replay.module.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for MongoDB repository operations.
 *
 * @param <T> the type of entity managed by this repository
 */
@RequiredArgsConstructor
public abstract class AbstractMongoRepository<T> {

    protected final @NotNull MongoCollection<T> collection;

    /**
     * Inserts a new entity into the database.
     *
     * @param entity the entity to insert
     */
    public void insert(@NotNull T entity) {
        collection.insertOne(entity);
    }

    /**
     * Finds an entity by its unique ObjectId.
     *
     * @param id the ObjectId of the entity
     * @return the found entity, or null if not found
     */
    public T findById(@NotNull ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    /**
     * Retrieves all entities in the collection.
     *
     * @return a list of all entities
     */
    public List<T> findAll() {
        return new ArrayList<>(collection.find().into(new ArrayList<>()));
    }

    /**
     * Deletes an entity from the collection.
     *
     * @param entity the entity to delete
     */
    public void delete(@NotNull T entity) {
        collection.deleteOne(Filters.eq("_id", extractId(entity)));
    }

    /**
     * Updates an entity in the database. Not implemented by default.
     *
     * @param entity the entity to update
     */
    public void update(@NotNull T entity) {
        throw new UnsupportedOperationException("Update method not implemented.");
    }

    /**
     * Extracts the ObjectId from the entity.
     * Must be implemented by subclasses to provide the correct ID extraction logic.
     *
     * @param entity the entity from which to extract the ID
     * @return the ObjectId of the entity
     */
    protected abstract ObjectId extractId(@NotNull T entity);
}
