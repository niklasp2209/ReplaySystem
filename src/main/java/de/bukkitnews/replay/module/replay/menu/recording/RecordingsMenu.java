package de.bukkitnews.replay.module.replay.menu.recording;

import de.bukkitnews.replay.module.replay.util.ItemUtil;
import de.bukkitnews.replay.module.replay.util.MessageUtil;
import de.bukkitnews.replay.module.replay.menu.MenuUtil;
import de.bukkitnews.replay.module.replay.menu.MultiMenu;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import de.bukkitnews.replay.module.replay.data.recording.Recording;
import de.bukkitnews.replay.module.replay.handler.RecordingHandler;
import de.bukkitnews.replay.module.replay.handler.ReplayHandler;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RecordingsMenu extends MultiMenu {

    private final @NotNull RecordingHandler recordingHandler;
    private final @NotNull ReplayHandler replayHandler;
    private final @NotNull RecordingArea recordingArea;

    private final @NotNull ReplayModule replayModule;

    public RecordingsMenu(@NotNull ReplayModule replayModule, @NotNull MenuUtil menuUtil) {
        super(menuUtil);

        this.replayModule = replayModule;
        this.recordingHandler = replayModule.getRecordingHandler();
        this.replayHandler = replayModule.getReplayHandler();
        this.recordingArea = menuUtil.getData("camera", RecordingArea.class);
    }

    @Override
    public @NotNull String getMenuTitle() {
        return "Recordings from: " + recordingArea.getName();
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
        NamespacedKey recordingIdKey = new NamespacedKey(replayModule.getReplaySystem(), "recordingId");
        String recordingId = itemMeta.getPersistentDataContainer().get(recordingIdKey, PersistentDataType.STRING);

        if (recordingId == null) {
            return;
        }

        if (event.getCurrentItem().getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        Recording recording = recordingHandler.findById(recordingId);

        if (recording == null) {
            player.sendMessage(MessageUtil.getMessage("recording_find"));
            return;
        }

        replayHandler.replayRecording(recording, player);
        player.closeInventory();
    }

    @Override
    public @NotNull List<ItemStack> dataToItems() {
        return recordingHandler.getRecordingsForCamera(recordingArea).stream()
                .map(this::createRecordingItem)
                .toList();
    }

    /**
     * Creates an ItemStack representing a recording.
     *
     * @param recording The recording to be represented.
     * @return An ItemStack representing the recording.
     */
    private @NotNull ItemStack createRecordingItem(@NotNull Recording recording) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String startDate = formatter.format(new Date(recording.getStartTime()));

        long duration = (recording.getEndTime() - recording.getStartTime()) / 1000;
        String durationString = formatDuration(duration);

        ItemStack itemStack = new ItemUtil(Material.PAPER)
                .setDisplayname(recording.getId().toString())
                .setLore("",
                        "Start: " + startDate,
                        "Duration: " + durationString,
                        "",
                        "Click to replay")
                .build();

        ItemMeta itemMeta = itemStack.getItemMeta();
        NamespacedKey recordingIdKey = new NamespacedKey(replayModule.getReplaySystem(), "recordingId");
        itemMeta.getPersistentDataContainer().set(recordingIdKey, PersistentDataType.STRING, recording.getId().toString());
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * Formats a duration (in seconds) as HH:mm:ss.
     *
     * @param duration The duration in seconds.
     * @return A formatted string representing the duration.
     */
    private @NotNull String formatDuration(long duration) {
        long hours = duration / 3600;
        long minutes = (duration % 3600) / 60;
        long seconds = duration % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public @Nullable HashMap<Integer, ItemStack> getCustomMenuBorderItems() {
        return null;
    }
}