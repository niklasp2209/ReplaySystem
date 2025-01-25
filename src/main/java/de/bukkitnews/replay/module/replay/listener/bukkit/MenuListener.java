package de.bukkitnews.replay.module.replay.listener.bukkit;

import de.bukkitnews.replay.exception.MenuManagerException;
import de.bukkitnews.replay.exception.MenuManagerNotSetupException;
import de.bukkitnews.replay.module.replay.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MenuListener implements Listener {

    /**
     * Event handler for inventory click events. Handles interactions with menus.
     *
     * @param event The inventory click event triggered by a player.
     */
    @EventHandler
    public void onMenuClick(@NotNull InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof Menu menu) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (menu.cancelAllInteractions()) {
                event.setCancelled(true);
            }

            try {
                menu.onItemInteraction(event);
            } catch (MenuManagerNotSetupException e) {
                System.err.println(ChatColor.RED + "InventoryUtil not initialized.");
            } catch (MenuManagerException e) {
                e.printStackTrace();
            }
        }
    }
}