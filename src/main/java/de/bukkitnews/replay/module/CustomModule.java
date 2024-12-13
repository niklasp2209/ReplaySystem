package de.bukkitnews.replay.module;

import de.bukkitnews.replay.ReplaySystem;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public abstract class CustomModule {

    private final ReplaySystem replaySystem;
    private final String moduleName;
    private List<Listener> listeners;
    private Map<String, CommandExecutor> commandExecutors;

    public CustomModule(@NonNull ReplaySystem replaySystem, @NonNull String name) {
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
        logToConsole("Initializing module: " + this.moduleName);
        registerEventListeners();
        bindCommands();
        logToConsole("Module: " + this.moduleName + " loaded successfully!");
    }

    /**
     * Disables the module by unregistering event listeners and performing any necessary cleanup.
     * This method should be called when the module is unloaded.
     */
    public void stop() {
        logToConsole("Disabling module: " + this.moduleName);
        unregisterEventListeners();
        logToConsole("Module: " + this.moduleName + " has been disabled.");
    }

    /**
     * Binds the commands for this module. It iterates over the command map and registers each
     * command with the corresponding executor.
     */
    private void bindCommands() {
        if (this.commandExecutors == null || this.commandExecutors.isEmpty()) {
            return;
        }

        this.commandExecutors.forEach((command, executor) -> {
            this.replaySystem.getCommand(command).setExecutor(executor);
            logToConsole("Module: " + this.moduleName + " has registered command: " + command);
        });
    }

    /**
     * Registers the event listeners for this module. It iterates over the list of listeners
     * and registers each with the plugin's event manager.
     */
    private void registerEventListeners() {
        if (this.listeners == null || this.listeners.isEmpty()) {
            return;
        }

        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        this.listeners.forEach(listener -> {
            pluginManager.registerEvents(listener, this.replaySystem);
            logToConsole("Module: " + this.moduleName + " has registered event listener: " + listener);
        });
    }

    /**
     * Unregisters the event listeners for this module. It iterates over the list of listeners
     * and removes all of them from the event handler list.
     */
    private void unregisterEventListeners() {
        if (this.listeners == null || this.listeners.isEmpty()) {
            return;
        }

        this.listeners.forEach(listener -> {
            HandlerList.unregisterAll(listener);
            logToConsole("Module: " + this.moduleName + " has unregistered event listener: " + listener);
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
