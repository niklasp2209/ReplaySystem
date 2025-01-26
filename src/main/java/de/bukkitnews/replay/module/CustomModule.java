package de.bukkitnews.replay.module;

import de.bukkitnews.replay.ReplaySystem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class CustomModule {

    private final ReplaySystem replaySystem;
    private final String moduleName;
    private Map<String, CommandExecutor> commandExecutors;

    public CustomModule(@NotNull ReplaySystem replaySystem, @NotNull String name) {
        this.replaySystem = replaySystem;
        this.moduleName = name;
        this.commandExecutors = new HashMap<>();
    }

    /**
     * Abstract method to activate the module. Must be implemented by subclasses.
     */
    public abstract void activate();

    /**
     * Abstract method to deactivate the module. Must be implemented by subclasses.
     */
    public abstract void deactivate();

    /**
     * Initializes the module by registering event listeners and binding commands.
     * This method should be called when the module is loaded.
     */
    public void start() {
        logToConsole("Initializing module: " + moduleName);
        bindCommands();
        logToConsole("Module: " + moduleName + " loaded successfully!");
    }

    /**
     * Disables the module by unregistering event listeners and performing any necessary cleanup.
     * This method should be called when the module is unloaded.
     */
    public void stop() {
        logToConsole("Disabling module: " + moduleName);
        logToConsole("Module: " + moduleName + " has been disabled.");
    }

    /**
     * Binds the commands for this module. It iterates over the command map and registers each
     * command with the corresponding executor.
     */
    private void bindCommands() {
        if (commandExecutors == null || commandExecutors.isEmpty()) {
            return;
        }

        commandExecutors.forEach((command, executor) -> {
            replaySystem.getCommand(command).setExecutor(executor);
            logToConsole("Module: " + moduleName + " has registered command: " + command);
        });
    }

    /**
     * Logs a message to the console with the prefix "Module: ".
     *
     * @param message The message to log.
     */
    private void logToConsole(String message) {
        Bukkit.getLogger().info(message);
    }
}
