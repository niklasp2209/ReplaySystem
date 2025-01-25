package de.bukkitnews.replay.module.replay.util;

import de.bukkitnews.replay.exception.MenuManagerException;
import de.bukkitnews.replay.exception.MenuManagerNotSetupException;
import de.bukkitnews.replay.module.replay.menu.Menu;
import de.bukkitnews.replay.module.replay.menu.MenuUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class InventoryUtil {

    private static final @NotNull Map<Player, MenuUtil> playerMenuUtilityMap = new HashMap<>();

    /**
     * Opens a menu for a player.
     *
     * @param menuClass The class reference of the Menu to open.
     * @param player    The player for whom the menu will be opened.
     * @throws MenuManagerException         If the menu cannot be instantiated or an error occurs during creation.
     * @throws MenuManagerNotSetupException If the MenuManager has not been properly initialized.
     */
    public static void openMenu(@NotNull Class<? extends Menu> menuClass, @NotNull Player player) throws MenuManagerException, MenuManagerNotSetupException {

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
    public static MenuUtil getPlayerMenuUtility(@NotNull Player player) throws MenuManagerNotSetupException {
        return playerMenuUtilityMap.computeIfAbsent(player, MenuUtil::new);
    }
}