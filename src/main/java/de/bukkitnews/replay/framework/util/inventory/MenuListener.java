package de.bukkitnews.replay.framework.util.inventory;

import de.bukkitnews.replay.framework.exception.MenuManagerException;
import de.bukkitnews.replay.framework.exception.MenuManagerNotSetupException;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {

    /**
     * Event handler for inventory click events. Handles interactions with menus.
     *
     * @param event The inventory click event triggered by a player.
     */
    @EventHandler
    public void onMenuClick(@NonNull InventoryClickEvent event) {
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