package de.bukkitnews.replay.module.replay.menu;

import de.bukkitnews.replay.module.replay.util.ItemUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Menu implements InventoryHolder {

    protected final @NotNull MenuUtil menuUtil;
    protected final @NotNull Player player;

    @Getter
    protected @Nullable Inventory inventory;

    public static final ItemStack FILLER_ITEM = new ItemUtil(Material.BLACK_STAINED_GLASS_PANE).build();

    /**
     * Constructor for the Menu.
     * Initializes with a MenuUtil to link the menu to a specific player.
     *
     * @param menuUtil Utility object for managing player-specific menu details.
     */
    public Menu(@NotNull MenuUtil menuUtil) {
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
     */
    public abstract void onItemInteraction(InventoryClickEvent event);

    /**
     * Sets up the menu's inventory items.
     */
    public abstract void populateMenu();

    /**
     * Opens the menu for the associated player.
     */
    public void open() {
        this.inventory = Bukkit.createInventory(this, getMenuSize(), getMenuTitle());

        populateMenu();

        menuUtil.getPlayer().openInventory(inventory);
        menuUtil.getPlayer().playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 1F);
        menuUtil.pushMenu(this);
    }
}
