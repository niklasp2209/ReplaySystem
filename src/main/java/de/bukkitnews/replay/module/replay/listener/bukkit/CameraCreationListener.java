package de.bukkitnews.replay.module.replay.listener.bukkit;

import de.bukkitnews.replay.module.replay.util.MessageUtil;
import de.bukkitnews.replay.module.replay.region.RegionUtil;
import de.bukkitnews.replay.module.replay.region.region.Region;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import de.bukkitnews.replay.module.replay.handler.CameraHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CameraCreationListener implements Listener {

    private final @NotNull CameraHandler cameraHandler;

    public CameraCreationListener(@NotNull ReplayModule replayModule) {
        this.cameraHandler = replayModule.getCameraHandler();
    }

    /**
     * Handles right-click events during camera creation.
     *
     * @param event the player interaction event
     */
    @EventHandler
    public void handleRightClick(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Optional<RecordingArea> optionalCamera = cameraHandler.getCreatedCamera(player);
        if (optionalCamera.isEmpty() || event.getItem() == null) {
            return;
        }

        RecordingArea recordingArea = optionalCamera.get();
        event.setCancelled(true);

        switch (event.getItem().getType()) {
            case BARRIER -> handleCancelCreation(player, recordingArea);
            case BLAZE_POWDER -> handleFinishCreation(player, recordingArea);
            case RED_WOOL, GREEN_WOOL ->
                    handleCornerSelection(player, recordingArea, event);
        }
    }

    /**
     * Cancels the camera creation process and cleans up associated data.
     *
     * @param player        the player cancelling the creation
     * @param recordingArea the camera being created
     */
    private void handleCancelCreation(@NotNull Player player, @NotNull RecordingArea recordingArea) {
        RegionUtil.removeSelectorsByTag(recordingArea.getName());
        cameraHandler.cancelCreatingCamera(player);
        player.sendMessage(MessageUtil.getMessage("camera_creation_cancelled"));
    }

    /**
     * Finalizes the camera creation process if both corners are set.
     *
     * @param player        the player finalizing the creation
     * @param recordingArea the camera being created
     */
    private void handleFinishCreation(@NotNull Player player, @NotNull RecordingArea recordingArea) {
        if (recordingArea.getCorner1().isEmpty()) {
            player.sendMessage(MessageUtil.getMessage("camera_creating_nocorner1"));
            return;
        }

        if (recordingArea.getCorner2().isEmpty()) {
            player.sendMessage(MessageUtil.getMessage("camera_creating_nocorner2"));
            return;
        }

        RegionUtil.removeSelectorsByTag(recordingArea.getName());
        cameraHandler.finishCreatingCamera(player);
        player.sendMessage(MessageUtil.getMessage("camera_creation_finished"));
    }

    /**
     * Handles setting the corners of the camera's region.
     *
     * @param player        the player setting a corner
     * @param recordingArea the camera being created
     * @param event         the interaction event
     */
    private void handleCornerSelection(@NotNull Player player, @NotNull RecordingArea recordingArea, @NotNull PlayerInteractEvent event) {
        Location location = getClickedLocation(player, event);

        if (location == null) {
            return;
        }

        Optional.ofNullable(event.getItem()).ifPresent(item -> {
            if (event.getItem().getType() == Material.RED_WOOL) {
                recordingArea.setCorner1(Optional.of(location));
                player.sendMessage(MessageUtil.getMessage("camera_created_setcorner1"));
            } else {
                recordingArea.setCorner2(Optional.of(location));
                player.sendMessage(MessageUtil.getMessage("camera_created_setcorner2"));
            }

            updateRegionSelectors(recordingArea, location, player);
        });
    }

    /**
     * Determines the location where the player clicked.
     *
     * @param player the player who clicked
     * @param event  the interaction event
     * @return the location of the clicked block or the player's location
     */
    private @Nullable Location getClickedLocation(@NotNull Player player, @NotNull PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            return event.getClickedBlock().getLocation();
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            return player.getLocation();
        }

        return null;
    }

    /**
     * Updates the region selectors for visualizing the camera's region.
     *
     * @param recordingArea the camera being created
     * @param location      the location to update selectors
     */
    private void updateRegionSelectors(@NotNull RecordingArea recordingArea, @NotNull Location location, @NotNull Player player) {
        Location corner1 = recordingArea.getCorner1().orElse(null);
        Location corner2 = recordingArea.getCorner2().orElse(null);

        if (corner1 != null && corner2 != null) {
            Region region = new Region(corner1, corner2);
            RegionUtil.removeSelectorsByTag(recordingArea.getName());
            RegionUtil.createSelector(region, location.getWorld(), recordingArea.getName(), ChatColor.GREEN);
            player.sendMessage(MessageUtil.getMessage("camera_created_glow"));
        } else {
            player.sendMessage(MessageUtil.getMessage("camera_creation_incomplete"));
        }
    }


    /**
     * Prevents inventory interactions during camera creation.
     *
     * @param event the inventory click event
     */
    @EventHandler
    public void handleInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            Optional<RecordingArea> optionalCamera = cameraHandler.getCreatedCamera(player);

            if (optionalCamera.isPresent()) {
                event.setCancelled(true);
                player.sendMessage(MessageUtil.getMessage("camera_creation_no_inventory"));
            }
        }
    }

}
