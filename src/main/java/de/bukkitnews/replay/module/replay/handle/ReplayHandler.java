package de.bukkitnews.replay.module.replay.handle;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import de.bukkitnews.replay.framework.util.MessageUtil;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.recording.Recording;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import de.bukkitnews.replay.module.replay.database.objects.CameraObject;
import de.bukkitnews.replay.module.replay.database.objects.RecordableObject;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReplayHandler {

    private final RecordableObject recordableObject;
    private final CameraObject cameraObject;
    private final Set<Replay> replays = new HashSet<>();

    public ReplayHandler(RecordableObject recordableObject, CameraObject cameraObject) {
        this.recordableObject = recordableObject;
        this.cameraObject = cameraObject;
    }

    /**
     * Returns the replay for the given player, or null if none is found.
     */
    public Replay getReplayForPlayer(Player player) {
        return replays.stream()
                .filter(replay -> replay.getViewer().equals(player))
                .findFirst()
                .orElse(null);
    }

    /**
     * Starts a replay for a given recording and player.
     */
    public void replayRecording(Recording recording, Player player) {
        if (getReplayForPlayer(player) != null) {
            player.sendMessage(MessageUtil.getMessage("replay_already"));
            return;
        }

        Replay replay = new Replay(recording, player);
        placeOriginalBlocks(replay.getRecording(), replay.getViewer());
        loadRecordables(replay, 0);
        replays.add(replay);
    }

    /**
     * Loads recordables for the replay within the specified tick range.
     */
    public void loadRecordables(Replay replay, long startTick) {
        if (!replay.tryLoadData()) {
            return;
        }

        try {
            long endTick = Math.min(startTick + 100, replay.getRecording().getTickDuration());
            Recording recording = replay.getRecording();

            List<Recordable> recordables = recordableObject.findByRecordingIdAndTickBetween(recording.getId(), startTick, endTick);
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
            PacketEvents.getAPI().getPlayerManager().getUser(replay.getViewer()).sendPacket(destroyEntities);
        });
        replay.getSpawnedEntities().clear();

        placeActualBlocks(replay.getRecording(), replay.getViewer());
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
    public void restartReplay(Replay replay) {
        placeOriginalBlocks(replay.getRecording(), replay.getViewer());
        replay.getSpawnedEntities().forEach((uuid, entityId) -> {
            WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(entityId);
            PacketEvents.getAPI().getPlayerManager().getUser(replay.getViewer()).sendPacket(destroyEntities);
        });
        replay.getSpawnedEntities().clear();
        replay.restartReplay();
        loadRecordables(replay, 0);
    }

    /**
     * Places the original blocks in the world based on the recording.
     */
    private void placeOriginalBlocks(Recording recording, Player viewer) {
        List<Material> originalBlocks = recording.getOriginalBlocks();
        var camera = cameraObject.findById(recording.getCameraId());
        var corner1 = camera.getCorner1();
        var corner2 = camera.getCorner2();

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
                    var stateType = StateTypes.getByName("minecraft:" + material.name().toLowerCase());
                    var wrappedBlockState = WrappedBlockState.getDefaultState(stateType);
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
    private void placeActualBlocks(Recording recording, Player viewer) {
        var camera = cameraObject.findById(recording.getCameraId());
        var corner1 = camera.getCorner1();
        var corner2 = camera.getCorner2();
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
                    var actualBlockState = SpigotConversionUtil.fromBukkitBlockData(block.getBlockData());
                    Vector3i position = new Vector3i(x, y, z);
                    WrapperPlayServerBlockChange blockChangePacket = new WrapperPlayServerBlockChange(position, actualBlockState.getGlobalId());
                    PacketEvents.getAPI().getPlayerManager().getUser(viewer).sendPacket(blockChangePacket);
                }
            }
        }
    }
}