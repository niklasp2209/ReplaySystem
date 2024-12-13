package de.bukkitnews.replay.module.replay.listener;

import de.bukkitnews.replay.framework.util.MessageUtil;
import de.bukkitnews.replay.framework.util.RegionUtil;
import de.bukkitnews.replay.framework.util.region.Region;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import de.bukkitnews.replay.module.replay.handle.CameraHandler;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class CameraCreationListener implements Listener {

    private final CameraHandler cameraHandler;

    public CameraCreationListener() {
        this.cameraHandler = ReplayModule.instance.getCameraHandler();
    }

    /**
     * Handles right-click events during camera creation.
     *
     * @param event the player interaction event
     */
    @EventHandler
    public void handleRightClick(@NonNull PlayerInteractEvent event) {
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
            case RED_WOOL, GREEN_WOOL -> handleCornerSelection(player, recordingArea, event);
        }
    }


    /**
     * Cancels the camera creation process and cleans up associated data.
     *
     * @param player the player cancelling the creation
     * @param recordingArea the camera being created
     */
    private void handleCancelCreation(@NonNull Player player, @NonNull RecordingArea recordingArea) {
        RegionUtil.removeSelectorsByTag(recordingArea.getName());
        cameraHandler.cancelCreatingCamera(player);
        player.sendMessage(MessageUtil.getMessage("camera_creation_cancelled"));
    }

    /**
     * Finalizes the camera creation process if both corners are set.
     *
     * @param player the player finalizing the creation
     * @param recordingArea the camera being created
     */
    private void handleFinishCreation(@NonNull Player player, @NonNull RecordingArea recordingArea) {
        if (recordingArea.getCorner1() == null) {
            player.sendMessage(MessageUtil.getMessage("camera_creating_nocorner1"));
            return;
        }

        if (recordingArea.getCorner2() == null) {
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
     * @param player the player setting a corner
     * @param recordingArea the camera being created
     * @param event  the interaction event
     */
    private void handleCornerSelection(@NonNull Player player, @NonNull RecordingArea recordingArea, @NonNull PlayerInteractEvent event) {
        Location location = getClickedLocation(player, event);

        if (location == null) {
            return; // Exit if no valid location was clicked
        }

        if (event.getItem().getType() == Material.RED_WOOL) {
            recordingArea.setCorner1(location);
            player.sendMessage(MessageUtil.getMessage("camera_created_setcorner1"));
        } else {
            recordingArea.setCorner2(location);
            player.sendMessage(MessageUtil.getMessage("camera_created_setcorner2"));
        }

        updateRegionSelectors(recordingArea, location, player);
    }

    /**
     * Determines the location where the player clicked.
     *
     * @param player the player who clicked
     * @param event  the interaction event
     * @return the location of the clicked block or the player's location
     */
    private Location getClickedLocation(@NonNull Player player, @NonNull PlayerInteractEvent event) {
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
     * @param recordingArea   the camera being created
     * @param location the location to update selectors
     */
    private void updateRegionSelectors(@NonNull RecordingArea recordingArea, @NonNull Location location, @NonNull Player player) {
        Region region = new Region(recordingArea.getCorner1(), recordingArea.getCorner2());

        RegionUtil.removeSelectorsByTag(recordingArea.getName());
        RegionUtil.createSelector(region, location.getWorld(), recordingArea.getName(), ChatColor.GREEN);

        if (recordingArea.getCorner1() != null && recordingArea.getCorner2() != null) {
            player.sendMessage(MessageUtil.getMessage("camera_created_glow"));
        }
    }

    /**
     * Prevents inventory interactions during camera creation.
     *
     * @param event the inventory click event
     */
    @EventHandler
    public void handleInventoryClick(@NonNull InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            Optional<RecordingArea> optionalCamera = cameraHandler.getCreatedCamera(player);

            if (optionalCamera.isPresent()) {
                RecordingArea recordingArea = optionalCamera.get();
                event.setCancelled(true);
                player.sendMessage(MessageUtil.getMessage("camera_creation_no_inventory"));
            }
        }
    }

}
