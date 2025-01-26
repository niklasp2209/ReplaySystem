package de.bukkitnews.replay.module.database;

import de.bukkitnews.replay.ReplaySystem;
import de.bukkitnews.replay.module.CustomModule;
import de.bukkitnews.replay.module.database.mongodb.MongoConnectionManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class DatabaseModule extends CustomModule {

    private MongoConnectionManager mongoConnectionManager;

    public DatabaseModule(@NotNull ReplaySystem replaySystem) {
        super(replaySystem, "Database");
    }

    @Override
    public void activate() {
        this.mongoConnectionManager = new MongoConnectionManager(getReplaySystem().getMongoConfig());
        getReplaySystem().getLogger().info("MongoDB connection initialized successfully!");

        getReplaySystem().setMongoConnectionManager(mongoConnectionManager);

        start();
    }

    @Override
    public void deactivate() {
        mongoConnectionManager.close();
    }
}
