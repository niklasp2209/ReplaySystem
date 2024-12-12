package de.bukkitnews.replay.module.replay.menu.recording;

import de.bukkitnews.replay.framework.exception.MenuManagerException;
import de.bukkitnews.replay.framework.exception.MenuManagerNotSetupException;
import de.bukkitnews.replay.framework.util.ItemUtil;
import de.bukkitnews.replay.framework.util.MessageUtil;
import de.bukkitnews.replay.framework.util.inventory.MenuUtil;
import de.bukkitnews.replay.framework.util.inventory.MultiMenu;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class RecordCamerasMenu extends MultiMenu {

    private final NamespacedKey cameraIdKey;

    public RecordCamerasMenu(MenuUtil menuUtil) {
        super(menuUtil);
        cameraIdKey = new NamespacedKey(ReplayModule.instance.getReplaySystem(), "camera_id");
    }

    @Override
    public String getMenuTitle() {
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
     * @throws MenuManagerNotSetupException If the menu manager is not properly set up.
     * @throws MenuManagerException If an error occurs during the interaction.
     */
    @Override
    public void onItemInteraction(InventoryClickEvent event) throws MenuManagerNotSetupException, MenuManagerException {
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
            Optional<RecordingArea> optionalCamera = ReplayModule.instance.getCameraHandler().findById(cameraId);

            optionalCamera.ifPresentOrElse(camera -> handleCameraInteraction(event, camera),
                    () -> player.sendMessage(MessageUtil.getMessage("inventory_error1")));
        }
    }


    /**
     * Handles camera interaction based on the player's click type.
     *
     * @param event The InventoryClickEvent triggered by the player.
     * @param recordingArea The camera object the player interacted with.
     */
    private void handleCameraInteraction(InventoryClickEvent event, RecordingArea recordingArea) {
        if (event.isLeftClick()) {
            ReplayModule.instance.getRecordingHandler().startRecording(player, recordingArea);
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
    public List<ItemStack> dataToItems() {
        return ReplayModule.instance.getCameraHandler().getCamerasForPlayer(player)
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
    private ItemStack createCameraItem(RecordingArea recordingArea) {
        ItemStack itemStack = new ItemUtil(Material.ENDER_EYE)
                .setDisplayname(recordingArea.getName())
                .setLore(" ", MessageUtil.getMessage("item_replays_lore1"), MessageUtil.getMessage("item_replays_lore2"))
                .build();

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(cameraIdKey, PersistentDataType.STRING, recordingArea.getId().toString());
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public @Nullable HashMap<Integer, ItemStack> getCustomMenuBorderItems() {
        return null;
    }
}