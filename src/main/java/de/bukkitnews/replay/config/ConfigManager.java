package de.bukkitnews.replay.config;

import de.bukkitnews.replay.ReplaySystem;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    @NonNull private final ReplaySystem replaySystem;
    @NonNull private final String fileName;
    private File configFile;
    private FileConfiguration fileConfiguration;

    public ConfigManager(@NonNull ReplaySystem replaySystem, @NonNull String fileName) {
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
        this.configFile = new File(this.replaySystem.getDataFolder(), this.fileName);

        if (!this.configFile.exists()) {
            this.replaySystem.getDataFolder().mkdirs();
            this.replaySystem.saveResource(this.fileName, false);
        }

        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.configFile);
    }

    /**
     * Gets the FileConfiguration object, which provides access to the configuration data.
     * @return The FileConfiguration object containing the loaded configuration data
     */
    public FileConfiguration getConfig() {
        return this.fileConfiguration;
    }

    /**
     * Saves the current configuration to the file system. If an error occurs while saving, it logs the exception.
     */
    public void save() {
        try {
            this.fileConfiguration.save(this.configFile);
        } catch (IOException e) {
            // Log an error if saving fails
            this.replaySystem.getLogger().severe("Could not save config file: " + this.fileName);
            e.printStackTrace();
        }
    }

    /**
     * Reloads the configuration from the file, refreshing the configuration data.
     */
    public void reload() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.configFile);
    }

    /**
     * Checks if the configuration file exists on the disk.
     * @return true if the configuration file exists, false otherwise
     */
    public boolean configExists() {
        return this.configFile.exists();
    }
}