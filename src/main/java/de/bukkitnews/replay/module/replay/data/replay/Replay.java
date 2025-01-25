package de.bukkitnews.replay.module.replay.data.replay;

import de.bukkitnews.replay.module.replay.util.ItemUtil;
import de.bukkitnews.replay.module.replay.util.MessageUtil;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.recording.Recording;
import de.bukkitnews.replay.module.replay.task.ReplayTask;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Optional;

/**
 * Represents a Replay for a given player, managing the playback and control actions for the recording.
 */
@Data
public class Replay {

    private final Player player;
    private final Recording recording;
    private final ReplayTask replayTask;

    private final Map<UUID, Integer> spawnedEntities = new ConcurrentHashMap<>();
    private final Queue<List<Recordable>> recordableQueue = new ConcurrentLinkedQueue<>();
    private volatile boolean loadingData = false;

    /**
     * Constructor initializes the replay for the given recording and viewer.
     * It also sets up the inventory items and starts the replay asynchronously.
     *
     * @param recording The recording associated with this replay.
     * @param player The player who will be viewing the replay.
     */
    public Replay(Recording recording, Player player) {
        this.recording = recording;
        this.player = player;

        player.getInventory().clear();

        setupInventory();

        player.sendMessage(MessageUtil.getMessage("replay_loading"));
        player.setExp(0F);
        player.setLevel(0);

        this.replayTask = new ReplayTask(this);
        replayTask.runTaskTimerAsynchronously(ReplayModule.instance.getReplaySystem(), 0L, 1L);
    }

    /**
     * Sets up the player's inventory with controls for managing the replay.
     */
    private void setupInventory() {
        player.getInventory().setItem(0, createControlItem(Material.REPEATER, "Restart"));
        player.getInventory().setItem(3, createControlItem(Material.GREEN_BANNER, "Play"));
        player.getInventory().setItem(4, createControlItem(Material.YELLOW_BANNER, "Pause"));
        player.getInventory().setItem(8, createControlItem(Material.LIME_BANNER, "Done"));
    }

    /**
     * Helper method to create an item with a specific material and display name.
     *
     * @param material The material of the item.
     * @param displayName The display name for the item.
     * @return The created item stack.
     */
    private ItemStack createControlItem(Material material, String displayName) {
        return new ItemUtil(material).setDisplayname(displayName).build();
    }

    /**
     * Restarts the replay, resetting its state and starting over.
     */
    public void restartReplay() {
        replayTask.restart();
    }

    /**
     * Starts or resumes the replay playback.
     */
    public void playReplay() {
        replayTask.play();
    }

    /**
     * Pauses the replay playback.
     */
    public void pauseReplay() {
        replayTask.pause();
    }

    /**
     * Ends the replay, cancels any active tasks, and clears the player's inventory.
     */
    public void endReplay() {
        Optional.ofNullable(replayTask).ifPresent(ReplayTask::cancel);
        player.getInventory().clear();
        player.setExp(0F); // Reset experience bar
    }

    /**
     * Attempts to load data for the replay. If data is already being loaded, it returns false.
     * This method is synchronized to ensure thread-safety when checking and setting the loadingData flag.
     *
     * @return True if data is not already loading, false if it is.
     */
    public synchronized boolean tryLoadData() {
        if (loadingData) {
            return false;
        }
        loadingData = true;
        return true;
    }

    /**
     * Marks the data loading process as complete.
     */
    public void doneLoadingData() {
        loadingData = false;
    }
}