package de.bukkitnews.replay.module.replay.menu.replay;

import de.bukkitnews.replay.framework.exception.MenuManagerException;
import de.bukkitnews.replay.framework.exception.MenuManagerNotSetupException;
import de.bukkitnews.replay.framework.util.InventoryUtil;
import de.bukkitnews.replay.framework.util.ItemUtil;
import de.bukkitnews.replay.framework.util.MessageUtil;
import de.bukkitnews.replay.framework.util.inventory.MenuUtil;
import de.bukkitnews.replay.framework.util.inventory.MultiMenu;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import de.bukkitnews.replay.module.replay.menu.recording.RecordingsMenu;
import jline.internal.Nullable;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ReplayCamerasMenu extends MultiMenu {

    private static final NamespacedKey CAMERA_ID_KEY = new NamespacedKey(ReplayModule.instance.getReplaySystem(), "camera_id");

    public ReplayCamerasMenu(@NonNull MenuUtil menuUtil) {
        super(menuUtil);
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

    @Override
    public void onItemInteraction(@NonNull InventoryClickEvent event) throws MenuManagerNotSetupException, MenuManagerException {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();
        String cameraId = itemMeta.getPersistentDataContainer().get(CAMERA_ID_KEY, PersistentDataType.STRING);

        if (cameraId == null) {
            player.sendMessage(MessageUtil.getMessage("inventory_error1"));
            return;
        }

        if (event.getCurrentItem().getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        Optional<RecordingArea> optionalCamera = ReplayModule.instance.getCameraHandler().findById(cameraId);

        if (optionalCamera.isEmpty()) {
            player.sendMessage(MessageUtil.getMessage("inventory_error2"));
            return;
        }

        RecordingArea recordingArea = optionalCamera.get();

        if (event.isLeftClick()) {
            menuUtil.setData("camera", recordingArea);
            InventoryUtil.openMenu(RecordingsMenu.class, player);
        } else if (event.isRightClick()) {
            // TODO: Teleportiere zum Kamerastandort
        }
    }


    @Override
    public List<ItemStack> dataToItems() {
        return ReplayModule.instance.getCameraHandler().getCamerasForPlayer(player)
                .stream()
                .map(camera -> createCameraItem(camera))
                .toList();
    }

    /**
     * Creates an ItemStack representing a camera.
     *
     * @param recordingArea The camera to be represented.
     * @return An ItemStack representing the camera.
     */
    private ItemStack createCameraItem(@NonNull RecordingArea recordingArea) {
        ItemStack itemStack = new ItemUtil(Material.ENDER_EYE)
                .setDisplayname(recordingArea.getName())
                .setLore(" ", MessageUtil.getMessage("item_replays_lore1"), MessageUtil.getMessage("item_replays_lore2"))
                .build();

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(CAMERA_ID_KEY, PersistentDataType.STRING, recordingArea.getId().toString());
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    @Override
    public @Nullable HashMap<Integer, ItemStack> getCustomMenuBorderItems() {
        return null;
    }
}