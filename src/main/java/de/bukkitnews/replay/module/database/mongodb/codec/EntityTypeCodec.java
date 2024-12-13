package de.bukkitnews.replay.module.database.mongodb.codec;

import lombok.NonNull;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.entity.EntityType;

import java.util.Objects;

/**
 * Codec for encoding and decoding {@link EntityType} objects to and from BSON.
 */
public class EntityTypeCodec implements Codec<EntityType> {

    @Override
    public EntityType decode(@NonNull BsonReader bsonReader, @NonNull DecoderContext decoderContext) {
        bsonReader.readStartDocument();
        String entityTypeString = bsonReader.readString("entityType");
        bsonReader.readEndDocument();

        try {
            return EntityType.valueOf(entityTypeString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid entity type found in BSON: " + entityTypeString, e);
        }
    }

    @Override
    public void encode(@NonNull BsonWriter bsonWriter, @NonNull EntityType entityType, @NonNull EncoderContext encoderContext) {
        Objects.requireNonNull(entityType, "EntityType cannot be null");

        bsonWriter.writeStartDocument();
        bsonWriter.writeString("entityType", entityType.name());
        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<EntityType> getEncoderClass() {
        return EntityType.class;
    }
}