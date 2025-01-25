package de.bukkitnews.replay.module.replay;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import de.bukkitnews.replay.ReplaySystem;
import de.bukkitnews.replay.module.replay.listener.bukkit.*;
import de.bukkitnews.replay.module.CustomModule;
import de.bukkitnews.replay.module.replay.command.ReplayCommand;
import de.bukkitnews.replay.module.replay.database.DatabaseRepositories;
import de.bukkitnews.replay.module.replay.listener.bukkit.recordable.*;
import de.bukkitnews.replay.module.replay.listener.packet.ReplayPacketListener;
import de.bukkitnews.replay.module.replay.handle.CameraHandler;
import de.bukkitnews.replay.module.replay.handle.RecordingHandler;
import de.bukkitnews.replay.module.replay.handle.ReplayHandler;
import de.bukkitnews.replay.module.replay.task.TickTrackerTask;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.plugin.PluginManager;

@Getter
public class ReplayModule extends CustomModule {

    private CameraHandler cameraHandler;
    private RecordingHandler recordingHandler;
    private ReplayHandler replayHandler;

    private DatabaseRepositories databaseRepositories;


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
        this.databaseRepositories = new DatabaseRepositories(getReplaySystem().getMongoConnectionManager());

        this.cameraHandler = new CameraHandler(this.databaseRepositories.getCameraRepository());
        this.recordingHandler = new RecordingHandler(this.databaseRepositories.getRecordingRepository(), this.databaseRepositories.getRecordableRepository(), this);
        this.replayHandler = new ReplayHandler(this.databaseRepositories.getRecordableRepository(), this.databaseRepositories.getCameraRepository());

        PacketEvents.getAPI().getEventManager()
                .registerListener(new ReplayPacketListener(this), PacketListenerPriority.NORMAL);
        PacketEvents.getAPI().init();

        initListener(getReplaySystem().getServer().getPluginManager());
        initCommands();

        TickTrackerTask.startTracking(this);
    }

    @Override
    public void deactivate() {
        PacketEvents.getAPI().terminate();
    }

    private void initListener(@NonNull PluginManager pluginManager){
        pluginManager.registerEvents(new CameraCreationListener(this), getReplaySystem());
        pluginManager.registerEvents(new BlockBreakListener(this), getReplaySystem());
        pluginManager.registerEvents(new BlockPlaceListener(this), getReplaySystem());
        pluginManager.registerEvents(new DropItemListener(this), getReplaySystem());
        pluginManager.registerEvents(new EntityDamageListener(this), getReplaySystem());
        pluginManager.registerEvents(new PickupItemListener(this), getReplaySystem());
        pluginManager.registerEvents(new PlayerSprintListener(this), getReplaySystem());
        pluginManager.registerEvents(new ReplayListener(this.replayHandler), getReplaySystem());
        pluginManager.registerEvents(new MenuListener(), getReplaySystem());
    }

    private void initCommands(){
        getReplaySystem().getCommand("replay").setExecutor(new ReplayCommand());
    }
}
