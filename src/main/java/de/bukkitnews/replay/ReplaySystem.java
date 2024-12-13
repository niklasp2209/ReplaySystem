package de.bukkitnews.replay;

import de.bukkitnews.replay.framework.database.ConfigManager;
import de.bukkitnews.replay.framework.util.MessageUtil;
import de.bukkitnews.replay.module.ModuleManager;
import de.bukkitnews.replay.module.database.mongodb.MongoConnectionManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is the main class for the "ReplaySystem" plugin,
 * developed as part of the BukkitNews project.
 * Source: <a href="https://hypixel.net/threads/dev-blog-10-replay-system-technical-rundown.3234748/">...</a>
 *
 * Created on: 11.12.2024
 */
@Getter
public class ReplaySystem extends JavaPlugin {

    private ConfigManager mongoConfig;
    private ConfigManager messagesConfig;

    private ModuleManager moduleManager;

    @Setter private MongoConnectionManager mongoConnectionManager;

    /**
     * This method is called when the server starts and the plugin is loaded.
     * It initializes configuration managers, loads messages, and prepares mongodb and module managers.
     */
    @Override
    public void onLoad(){
        this.mongoConfig = new ConfigManager(this, "database.yml");
        this.messagesConfig = new ConfigManager(this, "messages.yml");
        MessageUtil.loadMessages(messagesConfig);

        this.moduleManager = new ModuleManager(this);
    }

    @Override
    public void onEnable(){
        this.moduleManager.activateModules();
    }

    @Override
    public void onDisable(){
        this.moduleManager.deactivateModules();
    }
}
