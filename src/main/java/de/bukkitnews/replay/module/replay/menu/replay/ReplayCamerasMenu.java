package de.bukkitnews.replay.module.replay.menu.replay;

import de.bukkitnews.replay.module.replay.util.InventoryUtil;
import de.bukkitnews.replay.module.replay.util.ItemUtil;
import de.bukkitnews.replay.module.replay.util.MessageUtil;
import de.bukkitnews.replay.module.replay.menu.MenuUtil;
import de.bukkitnews.replay.module.replay.menu.MultiMenu;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import de.bukkitnews.replay.module.replay.menu.recording.RecordingsMenu;
import jline.internal.Nullable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ReplayCamerasMenu extends MultiMenu {

    private final @NotNull ReplayModule replayModule;

    public ReplayCamerasMenu(@NotNull ReplayModule replayModule, @NotNull MenuUtil menuUtil) {
        super(menuUtil);
        this.replayModule = replayModule;
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

    @Override
    public void onItemInteraction(@NotNull InventoryClickEvent event) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();

        NamespacedKey cameraIdKey = new NamespacedKey(replayModule.getReplaySystem(), "camera_id");
        String cameraId = itemMeta.getPersistentDataContainer().get(cameraIdKey, PersistentDataType.STRING);

        if (cameraId == null) {
            player.sendMessage(MessageUtil.getMessage("inventory_error1"));
            return;
        }

        if (event.getCurrentItem().getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        Optional<RecordingArea> optionalCamera = replayModule.getCameraHandler().findById(cameraId);
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
    public @NotNull List<ItemStack> dataToItems() {
        return replayModule.getCameraHandler().getCamerasForPlayer(player)
                .stream()
                .map(this::createCameraItem)
                .toList();
    }

    /**
     * Creates an ItemStack representing a camera.
     *
     * @param recordingArea The camera to be represented.
     * @return An ItemStack representing the camera.
     */
    private @NotNull ItemStack createCameraItem(@NotNull RecordingArea recordingArea) {
        ItemStack itemStack = new ItemUtil(Material.ENDER_EYE)
                .setDisplayname(recordingArea.getName())
                .setLore(" ", MessageUtil.getMessage("item_replays_lore1"), MessageUtil.getMessage("item_replays_lore2"))
                .build();

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            NamespacedKey cameraIdKey = new NamespacedKey(replayModule.getReplaySystem(), "camera_id");
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