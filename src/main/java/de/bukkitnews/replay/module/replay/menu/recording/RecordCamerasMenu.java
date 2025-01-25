package de.bukkitnews.replay.module.replay.menu.recording;

import de.bukkitnews.replay.module.replay.util.ItemUtil;
import de.bukkitnews.replay.module.replay.util.MessageUtil;
import de.bukkitnews.replay.module.replay.menu.MenuUtil;
import de.bukkitnews.replay.module.replay.menu.MultiMenu;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class RecordCamerasMenu extends MultiMenu {

    private final @NotNull NamespacedKey cameraIdKey;
    private final @NotNull ReplayModule replayModule;

    public RecordCamerasMenu(@NotNull ReplayModule replayModule, @NotNull MenuUtil menuUtil) {
        super(menuUtil);
        this.replayModule = replayModule;
        cameraIdKey = new NamespacedKey(replayModule.getReplaySystem(), "camera_id");
    }

    @Override
    public @NotNull String getMenuTitle() {
        return MessageUtil.getMessage("inventory_name");
    }

    @Override
    public int getMenuSize() {
        return 54;
    }

    @Override
    public boolean cancelAllInteractions() {
        return true;
    }

    /**
     * Handles player interaction with items in the camera selection menu.
     *
     * @param event The InventoryClickEvent that is triggered when a player clicks on an item.
     */
    @Override
    public void onItemInteraction(@NotNull InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        ItemMeta itemMeta = clickedItem.getItemMeta();
        String cameraId = itemMeta.getPersistentDataContainer().get(cameraIdKey, PersistentDataType.STRING);

        if (clickedItem.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        if (clickedItem.getType() == Material.ENDER_EYE && cameraId != null) {
            Optional<RecordingArea> optionalCamera = replayModule.getCameraHandler().findById(cameraId);
            optionalCamera.ifPresentOrElse(camera -> handleCameraInteraction(event, camera),
                    () -> player.sendMessage(MessageUtil.getMessage("inventory_error1")));
        }
    }


    /**
     * Handles camera interaction based on the player's click type.
     *
     * @param event         The InventoryClickEvent triggered by the player.
     * @param recordingArea The camera object the player interacted with.
     */
    private void handleCameraInteraction(@NotNull InventoryClickEvent event, @NotNull RecordingArea recordingArea) {
        if (event.isLeftClick()) {
            replayModule.getRecordingHandler().startRecording(player, recordingArea);
            player.closeInventory();
        } else if (event.isRightClick()) {
            // TODO: Add teleportation logic to the camera location
        }
    }

    /**
     * Converts the available cameras for the player into ItemStacks for the paginated menu.
     *
     * @return A list of ItemStacks representing the cameras.
     */
    @Override
    public @NotNull List<ItemStack> dataToItems() {
        return replayModule.getCameraHandler().getCamerasForPlayer(player)
                .stream()
                .map(this::createCameraItem)
                .toList();
    }

    /**
     * Creates an ItemStack representing a camera.
     *
     * @param recordingArea The camera to be represented by the ItemStack.
     * @return An ItemStack representing the camera.
     */
    private @NotNull ItemStack createCameraItem(@NotNull RecordingArea recordingArea) {
        ItemStack itemStack = new ItemUtil(Material.ENDER_EYE)
                .setDisplayname(recordingArea.getName())
                .setLore(" ", MessageUtil.getMessage("item_replays_lore1"), MessageUtil.getMessage("item_replays_lore2"))
                .build();

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(cameraIdKey, PersistentDataType.STRING, recordingArea.getId().toString());
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    @Override
    public @Nullable HashMap<Integer, ItemStack> getCustomMenuBorderItems() {
        return null;
    }
}