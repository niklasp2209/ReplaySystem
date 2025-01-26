package de.bukkitnews.replay.module.replay;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.mongodb.client.MongoCollection;
import de.bukkitnews.replay.ReplaySystem;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.recording.Recording;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import de.bukkitnews.replay.module.replay.listener.bukkit.*;
import de.bukkitnews.replay.module.CustomModule;
import de.bukkitnews.replay.module.replay.command.ReplayCommand;
import de.bukkitnews.replay.module.replay.data.camera.CameraRepository;
import de.bukkitnews.replay.module.replay.listener.bukkit.recordable.*;
import de.bukkitnews.replay.module.replay.listener.packet.ReplayPacketListener;
import de.bukkitnews.replay.module.replay.handler.CameraHandler;
import de.bukkitnews.replay.module.replay.handler.RecordingHandler;
import de.bukkitnews.replay.module.replay.handler.ReplayHandler;
import de.bukkitnews.replay.module.replay.data.recordable.RecordableRepository;
import de.bukkitnews.replay.module.replay.data.recording.RecordingRepository;
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

    private CameraRepository cameraRepository;
    private RecordingRepository recordingRepository;
    private RecordableRepository recordableRepository;

    public ReplayModule(@NonNull ReplaySystem replaySystem) {
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
        MongoCollection<RecordingArea> cameraCollection = getReplaySystem().getMongoConnectionManager().getDatabase()
                .getCollection("cameras", RecordingArea.class);
        MongoCollection<Recording> recordingCollection = getReplaySystem().getMongoConnectionManager().getDatabase()
                .getCollection("recordings", Recording.class);
        MongoCollection<Recordable> recordableCollection = getReplaySystem().getMongoConnectionManager().getDatabase()
                .getCollection("recordables", Recordable.class);

        this.cameraRepository = new CameraRepository(cameraCollection);
        this.recordingRepository = new RecordingRepository(recordingCollection);
        this.recordableRepository = new RecordableRepository(recordableCollection);

        this.cameraHandler = new CameraHandler(cameraRepository);
        this.recordingHandler = new RecordingHandler(recordingRepository, recordableRepository, this);
        this.replayHandler = new ReplayHandler(this, recordableRepository, cameraRepository);

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

    private void initListener(@NonNull PluginManager pluginManager) {
        pluginManager.registerEvents(new CameraCreationListener(this), getReplaySystem());
        pluginManager.registerEvents(new BlockBreakListener(recordingHandler), getReplaySystem());
        pluginManager.registerEvents(new BlockPlaceListener(recordingHandler), getReplaySystem());
        pluginManager.registerEvents(new DropItemListener(recordingHandler), getReplaySystem());
        pluginManager.registerEvents(new EntityDamageListener(recordingHandler), getReplaySystem());
        pluginManager.registerEvents(new PickupItemListener(recordingHandler), getReplaySystem());
        pluginManager.registerEvents(new PlayerSprintListener(recordingHandler), getReplaySystem());
        pluginManager.registerEvents(new ReplayListener(this.replayHandler), getReplaySystem());
        pluginManager.registerEvents(new MenuListener(), getReplaySystem());
    }

    private void initCommands() {
        getReplaySystem().getCommand("replay").setExecutor(new ReplayCommand(this));
    }
}
