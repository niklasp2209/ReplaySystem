package de.bukkitnews.replay.framework.util.inventory;

import de.bukkitnews.replay.framework.util.ItemUtil;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public abstract class MultiMenu extends Menu {

    protected List<Object> data;

    protected int page = 0;

    /**
     * -- GETTER --
     *
     * @return The maximum number of items displayed per page.
     */
    @Getter
    protected final int maxItemsPerPage = 28;

    public MultiMenu(@NonNull MenuUtil menuUtil) {
        super(menuUtil);
    }

    /**
     * Converts the data to ItemStacks to display in the menu.
     *
     * @return A list of ItemStacks to be displayed.
     */
    public abstract List<ItemStack> dataToItems();

    /**
     * Provides custom items for the menu border, if needed.
     *
     * @return A map with slot indices as keys and custom ItemStacks as values.
     */
    @Nullable
    public abstract HashMap<Integer, ItemStack> getCustomMenuBorderItems();

    /**
     * Adds a border and navigation buttons to the menu.
     * Override this method for a custom border layout.
     */
    protected void addMenuBorder() {
        // Add navigation buttons
        inventory.setItem(47, new ItemUtil(Material.DARK_OAK_BUTTON)
                .setDisplayname(ChatColor.GREEN + "Vorherige Seite").build());
        inventory.setItem(49, new ItemUtil(Material.BARRIER)
                .setDisplayname(ChatColor.DARK_RED + "Zurück").build());
        inventory.setItem(51, new ItemUtil(Material.DARK_OAK_BUTTON)
                .setDisplayname(ChatColor.GREEN + "Nächste Seite").build());

        // Add filler items to predefined slots
        int[] borderSlots = {0, 1, 7, 8, 9, 17, 36, 44, 45, 46, 52, 53};
        for (int slot : borderSlots) {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, FILLER_ITEM);
            }
        }

        // Place custom border items if provided
        if (getCustomMenuBorderItems() != null) {
            getCustomMenuBorderItems().forEach(inventory::setItem);
        }
    }

    /**
     * Fills the menu with items for the current page and applies the border.
     * Override to customize how items are set.
     */
    @Override
    public void populateMenu() {
        addMenuBorder();

        List<ItemStack> items = dataToItems();
        int startIndex = page * maxItemsPerPage;
        int currentSlot = 10;

        for (int i = 0; i < maxItemsPerPage; i++) {
            int itemIndex = startIndex + i;

            if (itemIndex >= items.size()) break;
            if (currentSlot == 18 || currentSlot == 26 || currentSlot == 27 || currentSlot == 35) {
                currentSlot++;
            }

            inventory.setItem(currentSlot, items.get(itemIndex));
            currentSlot++;

            if (currentSlot >= inventory.getSize()) {
                break;
            }
        }
    }


    /**
     * Navigates to the previous page if possible.
     *
     * @return true if the page changed, false if already on the first page.
     */
    public boolean prevPage() {
        if (page > 0) {
            page--;
            refreshMenuItems();
            return true;
        }
        return false;
    }

    /**
     * Navigates to the next page if possible.
     *
     * @return true if the page changed, false if already on the last page.
     */
    public boolean nextPage() {
        if ((page + 1) * maxItemsPerPage < dataToItems().size()) {
            page++;
            refreshMenuItems();
            return true;
        }
        return false;
    }

}
