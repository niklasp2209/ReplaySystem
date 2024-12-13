package de.bukkitnews.replay.module.replay;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import de.bukkitnews.replay.ReplaySystem;
import de.bukkitnews.replay.framework.util.InventoryUtil;
import de.bukkitnews.replay.module.CustomModule;
import de.bukkitnews.replay.module.database.mongodb.MongoConnectionManager;
import de.bukkitnews.replay.module.replay.command.ReplayCommand;
import de.bukkitnews.replay.module.replay.database.DatabaseObjects;
import de.bukkitnews.replay.module.replay.listener.BukkitListener;
import de.bukkitnews.replay.module.replay.listener.CameraCreationListener;
import de.bukkitnews.replay.module.replay.listener.ReplayListener;
import de.bukkitnews.replay.module.replay.listener.ReplayPacketHandler;
import de.bukkitnews.replay.module.replay.handle.CameraHandler;
import de.bukkitnews.replay.module.replay.handle.RecordingHandler;
import de.bukkitnews.replay.module.replay.handle.ReplayHandler;
import de.bukkitnews.replay.module.replay.task.TickTrackerTask;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.plugin.PluginManager;

import java.util.Optional;

@Getter
public class ReplayModule extends CustomModule {

    public static ReplayModule instance;

    private CameraHandler cameraHandler;
    private RecordingHandler recordingHandler;
    private ReplayHandler replayHandler;

    private DatabaseObjects databaseObjects;
    private MongoConnectionManager mongoConnectionManager;


    public ReplayModule(@NonNull ReplaySystem replaySystem){
        super(replaySystem, "Replay");

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(getReplaySystem()));
        PacketEvents.getAPI().getSettings()
                .reEncodeByDefault(false)
                .checkForUpdates(true)
                .bStats(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void activate() {
        instance = this;

        this.databaseObjects = new DatabaseObjects(getReplaySystem().getMongoConnectionManager());

        this.cameraHandler = new CameraHandler(this.databaseObjects.getCameraObject());
        this.recordingHandler = new RecordingHandler(this.databaseObjects.getRecordingObject(), this.databaseObjects.getRecordableObject());
        this.replayHandler = new ReplayHandler(this.databaseObjects.getRecordableObject(), this.databaseObjects.getCameraObject());

        PacketEvents.getAPI().getEventManager()
                .registerListener(new ReplayPacketHandler(), PacketListenerPriority.NORMAL);
        PacketEvents.getAPI().init();

        initListener(getReplaySystem().getServer().getPluginManager());
        initCommands();

        TickTrackerTask.startTracking();
        InventoryUtil.setup(getReplaySystem().getServer(), getReplaySystem());
    }

    @Override
    public void deactivate() {
        PacketEvents.getAPI().terminate();
    }

    private void initListener(@NonNull PluginManager pluginManager){
        pluginManager.registerEvents(new CameraCreationListener(), getReplaySystem());
        pluginManager.registerEvents(new BukkitListener(), getReplaySystem());
        pluginManager.registerEvents(new ReplayListener(this.replayHandler), getReplaySystem());
    }

    private void initCommands(){
        getReplaySystem().getCommand("replay").setExecutor(new ReplayCommand());
    }
}
