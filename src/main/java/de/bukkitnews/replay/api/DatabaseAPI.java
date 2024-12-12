package de.bukkitnews.replay.api;

import org.bson.types.ObjectId;

import java.util.List;

/**
 * Generic interface for interacting with a database.
 *
 * @param <T> the type of the entity to manage
 */
public interface DatabaseAPI<T> {

    /**
     * Inserts a new entity into the database.
     *
     * @param entity the entity to insert, must not be null
     */
    void insert(T entity);

    /**
     * Finds an entity by its unique identifier.
     *
     * @param id the unique identifier of the entity, must not be null
     * @return the found entity, or {@link java.util.Optional#empty()} if not found
     */
    T findById(ObjectId id);

    /**
     * Retrieves all entities of this type from the database.
     *
     * @return a list of all entities, never null
     */
    List<T> findAll();

    /**
     * Updates an existing entity in the database.
     *
     * @param entity the entity to update, must not be null
     */
    void update(T entity);

    /**
     * Deletes an entity from the database.
     *
     * @param entity the entity to delete, must not be null
     */
    void delete(T entity);
}