package de.bukkitnews.replay.module.replay.handle;

import de.bukkitnews.replay.framework.util.ItemUtil;
import de.bukkitnews.replay.framework.util.MessageUtil;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import de.bukkitnews.replay.module.replay.data.recording.Recording;
import de.bukkitnews.replay.module.replay.database.objects.CameraObject;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CameraHandler {

    private final CameraObject cameraObject;
    private final ConcurrentMap<Player, RecordingArea> createdCameras = new ConcurrentHashMap<>();

    public CameraHandler(@NonNull CameraObject cameraObject) {
        this.cameraObject = cameraObject;
    }

    /**
     * Retrieves the camera created by the player, wrapped in an Optional to handle absent cameras.
     */
    public Optional<RecordingArea> getCreatedCamera(@Nullable Player player) {
        return Optional.ofNullable(createdCameras.get(player));
    }

    /**
     * Finds a camera by its ID.
     */
    public Optional<RecordingArea> findById(@Nullable String id) {
        return Optional.ofNullable(cameraObject.findById(new ObjectId(id)));
    }

    /**
     * Retrieves a list of cameras owned by the player, or all cameras if the player is an admin.
     */
    public List<RecordingArea> getCamerasForPlayer(@NonNull Player player) {
        if (player.hasPermission("replay.command.admin")) {
            return cameraObject.findAll();
        } else {
            return cameraObject.findAllByOwnerId(player.getUniqueId());
        }
    }

    /**
     * Starts the camera creation process for the player with the given name.
     */
    public void startCreatingCamera(@NonNull Player player, @NonNull String name) {
        if (createdCameras.containsKey(player)) {
            player.sendMessage(MessageUtil.getMessage("camera_creating_already"));
            return;
        }

        RecordingArea recordingArea = new RecordingArea(name, player.getUniqueId());
        createdCameras.put(player, recordingArea);

        player.getInventory().clear();
        player.sendMessage(MessageUtil.getMessage("camera_creating_started", name));

        ItemStack cancel = new ItemUtil(Material.BARRIER).setDisplayname(MessageUtil.getMessage("item_cancel")).build();
        ItemStack finish = new ItemUtil(Material.BLAZE_POWDER).setDisplayname(MessageUtil.getMessage("item_finish")).build();
        ItemStack corner1 = new ItemUtil(Material.RED_WOOL).setDisplayname(MessageUtil.getMessage("item_corner1")).build();
        ItemStack corner2 = new ItemUtil(Material.GREEN_WOOL).setDisplayname(MessageUtil.getMessage("item_corner2")).build();

        player.getInventory().setItem(0, cancel);
        player.getInventory().setItem(2, corner1);
        player.getInventory().setItem(3, corner2);
        player.getInventory().setItem(8, finish);
    }

    /**
     * Completes the camera creation process and stores the camera in the database.
     */
    public void finishCreatingCamera(@NonNull Player player) {
        RecordingArea recordingArea = createdCameras.remove(player);

        if (recordingArea == null) {
            player.sendMessage(MessageUtil.getMessage("camera_creating_nocreating"));
            return;
        }

        cameraObject.insert(recordingArea);
        player.getInventory().clear();
        player.sendMessage(MessageUtil.getMessage("camera_creating_finished", recordingArea.getName()));
    }

    /**
     * Cancels the camera creation process for the player.
     */
    public void cancelCreatingCamera(@NonNull Player player) {
        if (createdCameras.remove(player) == null) {
            player.sendMessage(MessageUtil.getMessage("camera_creating_nocreating"));
            return;
        }

        player.getInventory().clear();
        player.sendMessage(MessageUtil.getMessage("camera_creating_cancel"));
    }

    /**
     * Starts recording with the given camera for the player.
     */
    public void startRecording(@NonNull Player player, @NonNull RecordingArea recordingArea) {
        Recording recording = new Recording(recordingArea, player);

        player.sendMessage(MessageUtil.getMessage("recording_started1", recordingArea.getName()));
        player.sendMessage(MessageUtil.getMessage("recording_started2"));
    }
}