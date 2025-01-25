package de.bukkitnews.replay.module.replay.menu.recording;

import de.bukkitnews.replay.exception.MenuManagerException;
import de.bukkitnews.replay.exception.MenuManagerNotSetupException;
import de.bukkitnews.replay.module.replay.util.ItemUtil;
import de.bukkitnews.replay.module.replay.util.MessageUtil;
import de.bukkitnews.replay.module.replay.menu.MenuUtil;
import de.bukkitnews.replay.module.replay.menu.MultiMenu;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import de.bukkitnews.replay.module.replay.data.recording.Recording;
import de.bukkitnews.replay.module.replay.handle.RecordingHandler;
import de.bukkitnews.replay.module.replay.handle.ReplayHandler;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RecordingsMenu extends MultiMenu {

    @NonNull private static final NamespacedKey RECORDING_ID_KEY = new NamespacedKey(ReplayModule.instance.getReplaySystem(), "recordingId");

    @NonNull private final RecordingHandler recordingHandler;
    @NonNull private final ReplayHandler replayHandler;
    @NonNull private final RecordingArea recordingArea;

    public RecordingsMenu(@NonNull MenuUtil menuUtil) {
        super(menuUtil);

        this.recordingHandler = ReplayModule.instance.getRecordingHandler();
        this.replayHandler = ReplayModule.instance.getReplayHandler();
        this.recordingArea = menuUtil.getData("camera", RecordingArea.class);
    }

    @Override
    public String getMenuTitle() {
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
    public void onItemInteraction(@NonNull InventoryClickEvent event) throws MenuManagerNotSetupException, MenuManagerException {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();
        String recordingId = itemMeta.getPersistentDataContainer().get(RECORDING_ID_KEY, PersistentDataType.STRING);

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
    public List<ItemStack> dataToItems() {
        return this.recordingHandler.getRecordingsForCamera(recordingArea).stream()
                .map(recording -> createRecordingItem(recording))
                .toList();
    }

    /**
     * Creates an ItemStack representing a recording.
     *
     * @param recording The recording to be represented.
     * @return An ItemStack representing the recording.
     */
    private ItemStack createRecordingItem(@NonNull Recording recording) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String startDate = formatter.format(new Date(recording.getStartTime()));
        String endDate = formatter.format(new Date(recording.getEndTime()));

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
        itemMeta.getPersistentDataContainer().set(RECORDING_ID_KEY, PersistentDataType.STRING, recording.getId().toString());
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * Formats a duration (in seconds) as HH:mm:ss.
     *
     * @param duration The duration in seconds.
     * @return A formatted string representing the duration.
     */
    private String formatDuration(long duration) {
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