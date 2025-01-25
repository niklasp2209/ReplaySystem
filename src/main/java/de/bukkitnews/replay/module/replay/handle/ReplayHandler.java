package de.bukkitnews.replay.module.replay.handle;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.util.MessageUtil;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.recording.Recording;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import de.bukkitnews.replay.module.replay.database.objects.CameraRepository;
import de.bukkitnews.replay.module.replay.database.objects.RecordableRepository;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReplayHandler {

    @NonNull
    private final RecordableRepository recordableRepository;
    @NonNull
    private final CameraRepository cameraRepository;
    @NonNull
    private final Set<Replay> replays = new HashSet<>();
    private final ReplayModule replayModule;

    public ReplayHandler(ReplayModule replayModule, @NonNull RecordableRepository recordableRepository, @NonNull CameraRepository cameraRepository) {
        this.replayModule = replayModule;
        this.recordableRepository = recordableRepository;
        this.cameraRepository = cameraRepository;
    }

    /**
     * Returns the replay for the given player, or null if none is found.
     */
    public Replay getReplayForPlayer(@NonNull Player player) {
        return replays.stream()
                .filter(replay -> replay.getPlayer().equals(player))
                .findFirst()
                .orElse(null);
    }

    /**
     * Starts a replay for a given recording and player.
     */
    public void replayRecording(@NonNull Recording recording, @NonNull Player player) {
        if (getReplayForPlayer(player) != null) {
            player.sendMessage(MessageUtil.getMessage("replay_already"));
            return;
        }

        Replay replay = new Replay(replayModule, recording, player);
        placeOriginalBlocks(replay.getRecording(), replay.getPlayer());
        loadRecordables(replay, 0);
        replays.add(replay);
    }

    /**
     * Loads recordables for the replay within the specified tick range.
     */
    public void loadRecordables(@NonNull Replay replay, long startTick) {
        if (!replay.tryLoadData()) {
            return;
        }

        try {
            long endTick = Math.min(startTick + 100, replay.getRecording().getTickDuration());
            Recording recording = replay.getRecording();

            List<Recordable> recordables = recordableRepository.findByRecordingIdAndTickBetween(recording.getId(), startTick, endTick);
            List<List<Recordable>> groupedRecordables = new ArrayList<>();
            long currentTick = 0;

            for (long tick = startTick; tick <= endTick; tick++) {
                List<Recordable> tickRecordables = new ArrayList<>();
                while (currentTick < recordables.size() && recordables.get((int) currentTick).getTick() == tick) {
                    tickRecordables.add(recordables.get((int) currentTick));
                    currentTick++;
                }
                groupedRecordables.add(tickRecordables);
            }

            groupedRecordables.forEach(replay.getRecordableQueue()::add);
        } finally {
            replay.doneLoadingData();
        }
    }

    /**
     * Stops the given replay and despawns all active entities.
     */
    public void stopReplay(Replay replay) {
        replay.endReplay();

        replay.getSpawnedEntities().forEach((uuid, entityId) -> {
            WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(entityId);
            PacketEvents.getAPI().getPlayerManager().getUser(replay.getPlayer()).sendPacket(destroyEntities);
        });
        replay.getSpawnedEntities().clear();

        placeActualBlocks(replay.getRecording(), replay.getPlayer());
        replays.remove(replay);
    }

    /**
     * Stops all active replays.
     */
    public void stopAllReplays() {
        replays.forEach(this::stopReplay);
        this.replays.clear();
    }

    /**
     * Restarts the given replay from the beginning.
     */
    public void restartReplay(@NonNull Replay replay) {
        placeOriginalBlocks(replay.getRecording(), replay.getPlayer());
        replay.getSpawnedEntities().forEach((uuid, entityId) -> {
            WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(entityId);
            PacketEvents.getAPI().getPlayerManager().getUser(replay.getPlayer()).sendPacket(destroyEntities);
        });
        replay.getSpawnedEntities().clear();
        replay.restartReplay();
        loadRecordables(replay, 0);
    }

    /**
     * Places the original blocks in the world based on the recording.
     */
    private void placeOriginalBlocks(@NonNull Recording recording, @NonNull Player viewer) {
        List<Material> originalBlocks = recording.getOriginalBlocks();
        RecordingArea recordingArea = cameraRepository.findById(recording.getCameraId());

        Location corner1 = recordingArea.getCorner1().orElse(null);
        Location corner2 = recordingArea.getCorner2().orElse(null);

        if (corner1 == null || corner2 == null) {
            viewer.sendMessage(MessageUtil.getMessage("camera_creation_incomplete"));
            return;
        }

        if (!corner1.getWorld().equals(corner2.getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world");
        }

        int startX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int endX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int startY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int endY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int startZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int endZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        int index = 0;
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    Material material = originalBlocks.get(index);
                    Vector3i position = new Vector3i(x, y, z);
                    StateType stateType = StateTypes.getByName("minecraft:" + material.name().toLowerCase());
                    WrappedBlockState wrappedBlockState = WrappedBlockState.getDefaultState(stateType);
                    WrapperPlayServerBlockChange blockChangePacket = new WrapperPlayServerBlockChange(position, wrappedBlockState.getGlobalId());
                    PacketEvents.getAPI().getPlayerManager().getUser(viewer).sendPacket(blockChangePacket);
                    index++;
                }
            }
        }
    }

    /**
     * Places the actual blocks in the world based on the recording.
     */
    private void placeActualBlocks(@NonNull Recording recording, @NonNull Player viewer) {
        RecordingArea recordingArea = cameraRepository.findById(recording.getCameraId());

        Location corner1 = recordingArea.getCorner1().orElse(null);
        Location corner2 = recordingArea.getCorner2().orElse(null);

        if (corner1 == null || corner2 == null) {
            viewer.sendMessage(MessageUtil.getMessage("camera_creation_incomplete"));
            return;
        }
        World world = corner1.getWorld();

        if (!corner1.getWorld().equals(corner2.getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world");
        }

        int startX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int endX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int startY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int endY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int startZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int endZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    WrappedBlockState actualBlockState = SpigotConversionUtil.fromBukkitBlockData(block.getBlockData());
                    Vector3i position = new Vector3i(x, y, z);
                    WrapperPlayServerBlockChange blockChangePacket = new WrapperPlayServerBlockChange(position, actualBlockState.getGlobalId());
                    PacketEvents.getAPI().getPlayerManager().getUser(viewer).sendPacket(blockChangePacket);
                }
            }
        }
    }
}