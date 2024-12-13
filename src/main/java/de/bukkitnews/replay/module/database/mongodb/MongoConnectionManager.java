package de.bukkitnews.replay.module.database.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import de.bukkitnews.replay.framework.database.ConfigManager;
import de.bukkitnews.replay.module.database.mongodb.codec.EntityTypeCodec;
import de.bukkitnews.replay.module.database.mongodb.codec.ItemStackCodec;
import de.bukkitnews.replay.module.database.mongodb.codec.LocationCodec;
import de.bukkitnews.replay.module.replay.data.recordable.recordables.*;
import lombok.NonNull;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Manages the MongoDB connection and provides access to the database.
 */
public class MongoConnectionManager {

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    /**
     * Constructs a MongoConnectionManager and initializes the MongoDB connection.
     *
     * @param configManager the configuration file containing MongoDB settings
     */
    public MongoConnectionManager(@NonNull ConfigManager configManager) {
        Objects.requireNonNull(configManager, "ConfigManager must not be null");

        // Retrieve configuration values
        FileConfiguration fileConfiguration = configManager.getConfig();
        String databaseName = requireConfigValue(fileConfiguration, "mongodb.database");
        String username = requireConfigValue(fileConfiguration, "mongodb.username");
        String password = requireConfigValue(fileConfiguration, "mongodb.password");

        // Build connection string
        String connectionString = String.format("mongodb+srv://%s:%s@%s.kqzly.mongodb.net/?retryWrites=true&w=majority&appName=%s",
                username, password, databaseName, databaseName);

        // Configure codecs
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(new LocationCodec(), new EntityTypeCodec(), new ItemStackCodec()),
                CodecRegistries.fromProviders(PojoCodecProvider.builder()
                        .register(
                                SpawnEntityRecordable.class,
                                DespawnEntityRecordable.class,
                                LocationChangeRecordable.class,
                                SneakRecordable.class,
                                SprintRecordable.class,
                                SwingHandRecordable.class,
                                SetEquipmentRecordable.class,
                                EntityHurtRecordable.class,
                                ItemDropRecordable.class,
                                ItemPickupRecordable.class,
                                BlockBreakRecordable.class,
                                BlockPlaceRecordable.class
                        )
                        .automatic(true)
                        .build())
        );

        // Configure MongoClient settings
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(codecRegistry)
                .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                .build();

        // Initialize MongoClient and MongoDatabase
        this.mongoClient = MongoClients.create(settings);
        this.mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    /**
     * Returns the connected MongoDatabase instance.
     *
     * @return the MongoDatabase
     */
    public MongoDatabase getDatabase() {
        return mongoDatabase;
    }

    /**
     * Closes the MongoClient connection.
     */
    public void close() {
        mongoClient.close();
    }

    /**
     * Retrieves a required configuration value or throws an exception if missing.
     *
     * @param config the configuration object
     * @param key    the key to retrieve
     * @return the configuration value
     */
    private String requireConfigValue(@Nullable FileConfiguration config, @NonNull String key) {
        return Objects.requireNonNull(config.getString(key), "Missing required configuration: " + key);
    }
}