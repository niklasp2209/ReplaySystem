package de.bukkitnews.replay.framework.util.inventory;

import de.bukkitnews.replay.framework.exception.MenuManagerException;
import de.bukkitnews.replay.framework.exception.MenuManagerNotSetupException;
import de.bukkitnews.replay.framework.util.InventoryUtil;
import de.bukkitnews.replay.framework.util.ItemUtil;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Menu implements InventoryHolder {

    protected final MenuUtil menuUtil;
    protected final Player player;
    protected Inventory inventory;

    public static final ItemStack FILLER_ITEM = new ItemUtil(Material.BLACK_STAINED_GLASS_PANE).build();

    /**
     * Constructor for the Menu.
     * Initializes with a MenuUtil to link the menu to a specific player.
     *
     * @param menuUtil Utility object for managing player-specific menu details.
     */
    public Menu(@NonNull MenuUtil menuUtil) {
        this.menuUtil = menuUtil;
        this.player = menuUtil.getPlayer();
    }

    /**
     * Gets the name of the menu.
     *
     * @return A string representing the menu's title.
     */
    public abstract String getMenuTitle();

    /**
     * Gets the size of the inventory slots.
     *
     * @return The number of slots in the menu.
     */
    public abstract int getMenuSize();

    /**
     * Determines if all inventory clicks should be cancelled by default.
     *
     * @return true if clicks should be cancelled, false otherwise.
     */
    public abstract boolean cancelAllInteractions();

    /**
     * Handles item interaction in the menu.
     *
     * @param event The click event.
     * @throws MenuManagerNotSetupException If the menu manager is not properly initialized.
     * @throws MenuManagerException         For generic menu-related errors.
     */
    public abstract void onItemInteraction(InventoryClickEvent event) throws MenuManagerNotSetupException, MenuManagerException;

    /**
     * Sets up the menu's inventory items.
     */
    public abstract void populateMenu();

    /**
     * Opens the menu for the associated player.
     */
    public void open() {
        inventory = Bukkit.createInventory(this, getMenuSize(), getMenuTitle());

        populateMenu();

        menuUtil.getPlayer().openInventory(inventory);
        menuUtil.getPlayer().playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 1F);
        menuUtil.pushMenu(this);
    }

    /**
     * Navigates back to the previous menu in the stack.
     *
     * @throws MenuManagerException         If the previous menu cannot be accessed.
     * @throws MenuManagerNotSetupException If the menu manager is not properly initialized.
     */
    public void navigateBack() throws MenuManagerException, MenuManagerNotSetupException {
        InventoryUtil.openMenu(menuUtil.lastMenu().getClass(), menuUtil.getPlayer());
    }

    /**
     * Reloads the current menu's items without closing the inventory.
     */
    protected void refreshMenuItems() {
        inventory.clear();
        populateMenu();
    }

    /**
     * Fully reloads the menu by closing and reopening it.
     *
     * @throws MenuManagerException         If the menu cannot be reopened.
     * @throws MenuManagerNotSetupException If the menu manager is not properly initialized.
     */
    protected void refreshMenu() throws MenuManagerException, MenuManagerNotSetupException {
        player.closeInventory();
        InventoryUtil.openMenu(this.getClass(), player);
    }

    /**
     * Retrieves the inventory associated with this menu.
     *
     * @return The current inventory instance.
     */
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    /**
     * Fills all empty slots in the inventory with the default filler item.
     */
    public void fillEmptySlots() {
        fillEmptySlots(FILLER_ITEM);
    }

    /**
     * Fills all empty slots in the inventory with a specified filler item.
     *
     * @param fillerItem The item to place in all empty slots.
     */
    public void fillEmptySlots(@NonNull ItemStack fillerItem) {
        for (int i = 0; i < getMenuSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, fillerItem);
            }
        }
    }
}
