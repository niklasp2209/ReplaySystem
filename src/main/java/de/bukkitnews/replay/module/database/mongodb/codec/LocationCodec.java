package de.bukkitnews.replay.module.database.mongodb.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Codec implementation for serializing and deserializing Bukkit's {@link Location} objects.
 */
public class LocationCodec implements Codec<Location> {

    @Override
    public Location decode(BsonReader reader, DecoderContext context) {
        Document document = new DocumentCodec().decode(reader, context);
        World world = Bukkit.getWorld(document.getString("world"));
        if (world == null) {
            throw new IllegalArgumentException("World not found: " + document.getString("world"));
        }

        double x = document.getDouble("x");
        double y = document.getDouble("y");
        double z = document.getDouble("z");
        float yaw = document.getDouble("yaw").floatValue();
        float pitch = document.getDouble("pitch").floatValue();

        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public void encode(BsonWriter writer, Location location, EncoderContext context) {
        writer.writeStartDocument();
        writer.writeString("world", location.getWorld().getName());
        writer.writeDouble("x", location.getX());
        writer.writeDouble("y", location.getY());
        writer.writeDouble("z", location.getZ());
        writer.writeDouble("yaw", location.getYaw());
        writer.writeDouble("pitch", location.getPitch());
        writer.writeEndDocument();
    }

    @Override
    public Class<Location> getEncoderClass() {
        return Location.class;
    }
}