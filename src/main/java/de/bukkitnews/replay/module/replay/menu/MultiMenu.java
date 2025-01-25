package de.bukkitnews.replay.module.replay.menu;

import de.bukkitnews.replay.module.replay.util.ItemUtil;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public abstract class MultiMenu extends Menu {

    protected int page = 0;

    @Getter
    protected final int maxItemsPerPage = 28;

    public MultiMenu(@NotNull MenuUtil menuUtil) {
        super(menuUtil);
    }

    /**
     * Converts the data to ItemStacks to display in the menu.
     *
     * @return A list of ItemStacks to be displayed.
     */
    public abstract @NotNull List<ItemStack> dataToItems();

    /**
     * Provides custom items for the menu border, if needed.
     *
     * @return A map with slot indices as keys and custom ItemStacks as values.
     */
    public abstract @Nullable HashMap<Integer, ItemStack> getCustomMenuBorderItems();

    /**
     * Adds a border and navigation buttons to the menu.
     * Override this method for a custom border layout.
     */
    protected void addMenuBorder() {
        inventory.setItem(47, new ItemUtil(Material.DARK_OAK_BUTTON)
                .setDisplayname(ChatColor.GREEN + "Vorherige Seite").build());
        inventory.setItem(49, new ItemUtil(Material.BARRIER)
                .setDisplayname(ChatColor.DARK_RED + "Zurück").build());
        inventory.setItem(51, new ItemUtil(Material.DARK_OAK_BUTTON)
                .setDisplayname(ChatColor.GREEN + "Nächste Seite").build());

        int[] borderSlots = {0, 1, 7, 8, 9, 17, 36, 44, 45, 46, 52, 53};
        for (int slot : borderSlots) {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, FILLER_ITEM);
            }
        }

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

            if (itemIndex >= items.size()) {
                break;
            }
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

}
