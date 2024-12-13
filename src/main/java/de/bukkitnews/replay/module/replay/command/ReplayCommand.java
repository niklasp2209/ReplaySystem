package de.bukkitnews.replay.module.replay.command;

import de.bukkitnews.replay.framework.exception.MenuManagerException;
import de.bukkitnews.replay.framework.exception.MenuManagerNotSetupException;
import de.bukkitnews.replay.framework.util.InventoryUtil;
import de.bukkitnews.replay.framework.util.MessageUtil;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.menu.recording.RecordCamerasMenu;
import de.bukkitnews.replay.module.replay.menu.replay.ReplayCamerasMenu;
import de.bukkitnews.replay.module.replay.handle.CameraHandler;
import de.bukkitnews.replay.module.replay.handle.RecordingHandler;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the "replay" command, providing functionalities such as
 * creating, starting, and stopping replays, as well as opening related menus.
 */
public class ReplayCommand implements CommandExecutor {

    private final CameraHandler cameraHandler;
    private final RecordingHandler recordingHandler;

    public ReplayCommand() {
        this.cameraHandler = ReplayModule.instance.getCameraHandler();
        this.recordingHandler = ReplayModule.instance.getRecordingHandler();
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label,
                             @NonNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }

        if (args.length == 0) {
            openReplayMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> handleCreateCommand(player, args);
            case "start" -> handleStartCommand(player);
            case "stop" -> handleStopCommand(player);
            default -> player.sendMessage(MessageUtil.getMessage("command_invalid"));
        }

        return true;
    }

    /**
     * Handles the "create" subcommand to start creating a replay camera.
     *
     * @param player the player executing the command
     * @param args   the arguments passed with the command
     */
    private void handleCreateCommand(@NonNull Player player, @NonNull String[] args) {
        if (!player.hasPermission("replay.command.create")) {
            player.sendMessage(MessageUtil.getMessage("noperm"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MessageUtil.getMessage("command_replay_name"));
            return;
        }

        String replayName = args[1];
        this.cameraHandler.startCreatingCamera(player, replayName);
        player.sendMessage(MessageUtil.getMessage("command_replay_create_success", replayName));
    }

    /**
     * Handles the "start" subcommand to start a recording session.
     *
     * @param player the player executing the command
     */
    private void handleStartCommand(@NonNull Player player) {
        if (this.recordingHandler.getPlayerActiveRecording(player).isPresent()) {
            player.sendMessage(MessageUtil.getMessage("command_record_already"));
            return;
        }

        try {
            InventoryUtil.openMenu(RecordCamerasMenu.class, player);
        } catch (MenuManagerException | MenuManagerNotSetupException exception) {
            handleMenuError(player, "recording");
        }
    }

    /**
     * Handles the "stop" subcommand to stop an active recording session.
     *
     * @param player the player executing the command
     */
    private void handleStopCommand(@NonNull Player player) {
        this.recordingHandler.stopRecording(player);
    }

    /**
     * Opens the replay menu for the player.
     *
     * @param player the player executing the command
     */
    private void openReplayMenu(@NonNull Player player) {
        try {
            InventoryUtil.openMenu(ReplayCamerasMenu.class, player);
        } catch (MenuManagerException | MenuManagerNotSetupException exception) {
            handleMenuError(player, "replay");
        }
    }

    /**
     * Handles menu errors by notifying the player and printing the stack trace.
     *
     * @param player the player involved
     * @param menu   the menu type (e.g., "recording" or "replay")
     */
    private void handleMenuError(@NonNull Player player, @NonNull String menu) {
        player.sendMessage(MessageUtil.getMessage("menu_error", menu));
    }
}
