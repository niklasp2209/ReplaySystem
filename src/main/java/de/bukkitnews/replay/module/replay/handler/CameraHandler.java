package de.bukkitnews.replay.module.replay.handler;

import de.bukkitnews.replay.module.replay.util.ItemUtil;
import de.bukkitnews.replay.module.replay.util.MessageUtil;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import de.bukkitnews.replay.module.replay.data.camera.CameraRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
public class CameraHandler {

    private final @NotNull CameraRepository cameraRepository;
    @Getter
    private final @NotNull ConcurrentMap<Player, RecordingArea> createdCameras = new ConcurrentHashMap<>();

    /**
     * Retrieves the camera created by the player, wrapped in an Optional to handle absent cameras.
     */
    public @NotNull Optional<RecordingArea> getCreatedCamera(@Nullable Player player) {
        return Optional.ofNullable(createdCameras.get(player));
    }


    /**
     * Retrieves a list of cameras owned by the player, or all cameras if the player is an admin.
     */
    public @NotNull List<RecordingArea> getCamerasForPlayer(@NotNull Player player) {
        if (player.hasPermission("replay.command.admin")) {
            return cameraRepository.findAll();
        }

        return cameraRepository.findAllByOwnerId(player.getUniqueId());
    }

    public @NotNull Optional<RecordingArea> findById(@Nullable String id){
        return Optional.ofNullable(cameraRepository.findById(new ObjectId(id)));
    }

    /**
     * Starts the camera creation process for the player with the given name.
     */
    public void startCreatingCamera(@NotNull Player player, @NotNull String name) {
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
    public void finishCreatingCamera(@NotNull Player player) {
        RecordingArea recordingArea = createdCameras.remove(player);
        if (recordingArea == null) {
            player.sendMessage(MessageUtil.getMessage("camera_creating_nocreating"));
            return;
        }

        cameraRepository.insert(recordingArea);
        player.getInventory().clear();
        player.sendMessage(MessageUtil.getMessage("camera_creating_finished", recordingArea.getName()));
    }

    /**
     * Cancels the camera creation process for the player.
     */
    public void cancelCreatingCamera(@NotNull Player player) {
        if (createdCameras.remove(player) == null) {
            player.sendMessage(MessageUtil.getMessage("camera_creating_nocreating"));
            return;
        }

        player.getInventory().clear();
        player.sendMessage(MessageUtil.getMessage("camera_creating_cancel"));
    }
}