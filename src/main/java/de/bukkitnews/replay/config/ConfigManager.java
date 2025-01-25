package de.bukkitnews.replay.config;

import de.bukkitnews.replay.ReplaySystem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final @NotNull ReplaySystem replaySystem;
    private final @NotNull String fileName;
    private File configFile;
    private FileConfiguration fileConfiguration;

    public ConfigManager(@NotNull ReplaySystem replaySystem, @NotNull String fileName) {
        this.replaySystem = replaySystem;
        this.fileName = fileName;
        setup();
    }

    /**
     * Initializes the configuration file by checking if it exists, creating the necessary directories,
     * and loading the configuration. If the configuration file doesn't exist, it will attempt to save the
     * default configuration resource from the plugin's JAR file.
     */
    private void setup() {
        this.configFile = new File(replaySystem.getDataFolder(), fileName);

        if (!configFile.exists()) {
            replaySystem.getDataFolder().mkdirs();
            replaySystem.saveResource(fileName, false);
        }

        this.fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Gets the FileConfiguration object, which provides access to the configuration data.
     * @return The FileConfiguration object containing the loaded configuration data
     */
    public @NotNull FileConfiguration getConfig() {
        return fileConfiguration;
    }

    /**
     * Saves the current configuration to the file system. If an error occurs while saving, it logs the exception.
     */
    public void save() {
        try {
            fileConfiguration.save(configFile);
        } catch (IOException e) {
            replaySystem.getLogger().severe("Could not save config file: " + fileName);
            e.printStackTrace();
        }
    }

    /**
     * Reloads the configuration from the file, refreshing the configuration data.
     */
    public void reload() {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Checks if the configuration file exists on the disk.
     * @return true if the configuration file exists, false otherwise
     */
    public boolean configExists() {
        return configFile.exists();
    }
}