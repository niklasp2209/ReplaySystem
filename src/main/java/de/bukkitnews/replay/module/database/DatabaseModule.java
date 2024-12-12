package de.bukkitnews.replay.module.database;

import de.bukkitnews.replay.ReplaySystem;
import de.bukkitnews.replay.module.CustomModule;
import de.bukkitnews.replay.module.database.mongodb.MongoConnectionManager;
import lombok.Getter;

@Getter
public class DatabaseModule extends CustomModule {

    private MongoConnectionManager mongoConnectionManager;

    public DatabaseModule(ReplaySystem replaySystem){
        super(replaySystem, "Database");
    }

    @Override
    public void activate() {
        try {
            mongoConnectionManager = new MongoConnectionManager(this.getReplaySystem().getMongoConfig());
            this.getReplaySystem().getLogger().info("MongoDB connection initialized successfully!");
        } catch (Exception exception){
            this.getReplaySystem().getLogger().severe("Failed to initialize MongoDB connection: " + exception.getMessage());
            this.getReplaySystem().getServer().getPluginManager().disablePlugin(this.getReplaySystem());
        }

        getReplaySystem().setMongoConnectionManager(mongoConnectionManager);

    }

    @Override
    public void deactivate() {

    }
}
