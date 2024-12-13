package de.bukkitnews.replay.framework.util;

import de.bukkitnews.replay.framework.exception.MenuManagerException;
import de.bukkitnews.replay.framework.exception.MenuManagerNotSetupException;
import de.bukkitnews.replay.framework.util.inventory.Menu;
import de.bukkitnews.replay.framework.util.inventory.MenuListener;
import de.bukkitnews.replay.framework.util.inventory.MenuUtil;
import lombok.NonNull;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class InventoryUtil {

    private static final Map<Player, MenuUtil> playerMenuUtilityMap = new HashMap<>();
    private static boolean isInitialized = false;

    /**
     * Registers the MenuListener for handling menu events, ensuring it is only registered once.
     *
     * @param server The server instance.
     * @param plugin The plugin instance.
     */
    private static void registerMenuListener(@NonNull Server server, @NonNull Plugin plugin) {
        boolean listenerAlreadyRegistered = false;

        for (RegisteredListener registeredListener : InventoryClickEvent.getHandlerList().getRegisteredListeners()) {
            if (registeredListener.getListener() instanceof MenuListener) {
                listenerAlreadyRegistered = true;
                break;
            }
        }

        if (!listenerAlreadyRegistered) {
            server.getPluginManager().registerEvents(new MenuListener(), plugin);
        }
    }


    /**
     * Initializes the MenuManager by registering the required event listener.
     *
     * @param server The server instance.
     * @param plugin The plugin using this API.
     */
    public static void setup(@NonNull Server server, @NonNull Plugin plugin) {
        if (!isInitialized) {
            registerMenuListener(server, plugin);
            isInitialized = true;
        }
    }

    /**
     * Opens a menu for a player.
     *
     * @param menuClass The class reference of the Menu to open.
     * @param player    The player for whom the menu will be opened.
     * @throws MenuManagerException         If the menu cannot be instantiated or an error occurs during creation.
     * @throws MenuManagerNotSetupException If the MenuManager has not been properly initialized.
     */
    public static void openMenu(@NonNull Class<? extends Menu> menuClass, @NonNull Player player) throws MenuManagerException, MenuManagerNotSetupException {
        if (!isInitialized) {
            throw new MenuManagerNotSetupException("InventoryUtil.setup() has not been called.");
        }

        try {
            Menu menu = menuClass.getConstructor(MenuUtil.class).newInstance(getPlayerMenuUtility(player));
            menu.open();
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new MenuManagerException("Failed to create menu: " + menuClass.getName(), e);
        }
    }

    /**
     * Retrieves the PlayerMenuUtility for a player, creating one if it does not already exist.
     *
     * @param player The player whose PlayerMenuUtility is needed.
     * @return The PlayerMenuUtility associated with the player.
     * @throws MenuManagerNotSetupException If the MenuManager has not been properly initialized.
     */
    public static MenuUtil getPlayerMenuUtility(@NonNull Player player) throws MenuManagerNotSetupException {
        if (!isInitialized) {
            throw new MenuManagerNotSetupException("InventoryUtil.setup() has not been called.");
        }

        return playerMenuUtilityMap.computeIfAbsent(player, MenuUtil::new);
    }
}