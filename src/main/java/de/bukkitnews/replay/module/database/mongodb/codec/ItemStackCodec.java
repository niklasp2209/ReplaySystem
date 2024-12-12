package de.bukkitnews.replay.module.database.mongodb.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Codec implementation for serializing and deserializing ItemStack objects to and from BSON.
 */
public class ItemStackCodec implements Codec<ItemStack> {

    @Override
    public ItemStack decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();
        String materialName = bsonReader.readString("type");
        int amount = bsonReader.readInt32("amount");
        bsonReader.readEndDocument();

        Material material = Material.getMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("Unknown material type: " + materialName);
        }

        return new ItemStack(material, amount);
    }

    @Override
    public void encode(BsonWriter bsonWriter, ItemStack itemStack, EncoderContext encoderContext) {
        if (itemStack == null) {
            throw new IllegalArgumentException("ItemStack cannot be null during encoding.");
        }

        bsonWriter.writeStartDocument();
        bsonWriter.writeString("type", itemStack.getType().name());
        bsonWriter.writeInt32("amount", itemStack.getAmount());
        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<ItemStack> getEncoderClass() {
        return ItemStack.class;
    }
}