package de.bukkitnews.replay.module.database.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import de.bukkitnews.replay.config.ConfigManager;
import de.bukkitnews.replay.module.database.mongodb.codec.EntityTypeCodec;
import de.bukkitnews.replay.module.database.mongodb.codec.ItemStackCodec;
import de.bukkitnews.replay.module.database.mongodb.codec.LocationCodec;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.util.Optional;

/**
 * Manages the MongoDB connection and provides access to the database.
 */
public class MongoConnectionManager {

    private final @NotNull MongoClient mongoClient;
    private final @NotNull MongoDatabase mongoDatabase;

    /**
     * Constructs a MongoConnectionManager and initializes the MongoDB connection.
     *
     * @param configManager the configuration file containing MongoDB settings
     */
    public MongoConnectionManager(@NotNull ConfigManager configManager) {
        FileConfiguration fileConfiguration = configManager.getConfig();
        String databaseName = getConfigValue(fileConfiguration, "mongodb.database")
                .orElseThrow(() -> new IllegalArgumentException("Database is not configured!"));
        String username = getConfigValue(fileConfiguration, "mongodb.username")
                .orElseThrow(() -> new IllegalArgumentException("Database is not configured!"));
        String password = getConfigValue(fileConfiguration, "mongodb.password")
                .orElseThrow(() -> new IllegalArgumentException("Database is not configured!"));

        String connectionString = String.format("mongodb+srv://%s:%s@%s.kqzly.mongodb.net/?retryWrites=true&w=majority&appName=%s",
                username, password, databaseName, databaseName);

        // Configure codecs
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(new LocationCodec(), new EntityTypeCodec(), new ItemStackCodec()),
                CodecRegistries.fromProviders(
                        PojoCodecProvider.builder()
                                .register(findRecordableClasses())
                                .automatic(true)
                                .build()
                )
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(codecRegistry)
                .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                .build();

        this.mongoClient = MongoClients.create(settings);
        this.mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    /**
     * Returns the connected MongoDatabase instance.
     *
     * @return the MongoDatabase
     */
    public @NotNull MongoDatabase getDatabase() {
        return mongoDatabase;
    }

    /**
     * Closes the MongoClient connection.
     */
    public void close() {
        mongoClient.close();
    }


    /**
     * Retrieves the configuration value for the given key if it exists.
     *
     * @param config The configuration file to search in. Can be null.
     * @param key    The key to look up in the configuration. Must not be null.
     * @return An Optional containing the value if present, or an empty Optional if the
     * configuration is null or the key does not exist.
     */
    private @NotNull Optional<String> getConfigValue(@Nullable FileConfiguration config, @NotNull String key) {
        if (config == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(config.getString(key));
    }

    /**
     * Finds all subclasses of the {@link Recordable} class within a specific package.
     *
     * @return A {@link Class} object representing the collection of Recordable subclasses found.
     * This class encapsulates the metadata of the subclasses.
     */
    private @NotNull Class<?> findRecordableClasses() {
        Reflections reflections = new Reflections("de.bukkitnews.replay.module.replay.data.recordable.recordables");
        return reflections.getSubTypesOf(Recordable.class).getClass();
    }
}