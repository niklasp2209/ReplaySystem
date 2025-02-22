package de.bukkitnews.replay.module.replay.util;

import de.bukkitnews.replay.module.replay.menu.Menu;
import de.bukkitnews.replay.module.replay.menu.MenuUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class InventoryUtil {

    private static final @NotNull Map<Player, MenuUtil> PLAYER_MENU_UTIL_MAP = new HashMap<>();

    /**
     * Opens a menu for a player.
     *
     * @param menuClass The class reference of the Menu to open.
     * @param player    The player for whom the menu will be opened.
     */
    public static void openMenu(@NotNull Class<? extends Menu> menuClass, @NotNull Player player) {

        Menu menu;
        try {
            menu = menuClass.getConstructor(MenuUtil.class).newInstance(getPlayerMenuUtility(player));
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        menu.open();
    }

    /**
     * Retrieves the PlayerMenuUtility for a player, creating one if it does not already exist.
     *
     * @param player The player whose PlayerMenuUtility is needed.
     * @return The PlayerMenuUtility associated with the player.
     */
    public static @NotNull MenuUtil getPlayerMenuUtility(@NotNull Player player) {
        return PLAYER_MENU_UTIL_MAP.computeIfAbsent(player, MenuUtil::new);
    }
}