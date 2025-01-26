package de.bukkitnews.replay.module.replay.command;

import de.bukkitnews.replay.module.replay.util.InventoryUtil;
import de.bukkitnews.replay.module.replay.util.MessageUtil;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.menu.recording.RecordCamerasMenu;
import de.bukkitnews.replay.module.replay.menu.replay.ReplayCamerasMenu;
import de.bukkitnews.replay.module.replay.handler.CameraHandler;
import de.bukkitnews.replay.module.replay.handler.RecordingHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the "replay" command, providing functionalities such as
 * creating, starting, and stopping replays, as well as opening related menus.
 */
public class ReplayCommand implements CommandExecutor {

    private final @NotNull CameraHandler cameraHandler;
    private final @NotNull RecordingHandler recordingHandler;

    public ReplayCommand(@NotNull ReplayModule replayModule) {
        this.cameraHandler = replayModule.getCameraHandler();
        this.recordingHandler = replayModule.getRecordingHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
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
    private void handleCreateCommand(@NotNull Player player, @NotNull String[] args) {
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
    private void handleStartCommand(@NotNull Player player) {
        if (this.recordingHandler.getPlayerActiveRecording(player).isPresent()) {
            player.sendMessage(MessageUtil.getMessage("command_record_already"));
            return;
        }

        InventoryUtil.openMenu(RecordCamerasMenu.class, player);
    }

    /**
     * Handles the "stop" subcommand to stop an active recording session.
     *
     * @param player the player executing the command
     */
    private void handleStopCommand(@NotNull Player player) {
        this.recordingHandler.stopRecording(player);
    }

    /**
     * Opens the replay menu for the player.
     *
     * @param player the player executing the command
     */
    private void openReplayMenu(@NotNull Player player) {
        InventoryUtil.openMenu(ReplayCamerasMenu.class, player);
    }
}
