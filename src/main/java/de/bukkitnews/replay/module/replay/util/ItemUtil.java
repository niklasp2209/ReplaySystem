package de.bukkitnews.replay.module.replay.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ItemUtil {

    private final @NotNull ItemStack itemStack;
    private final @NotNull ItemMeta itemMeta;

    /**
     * Constructs an ItemBuilder with the specified material.
     *
     * @param material The material of the item to be created.
     */
    public ItemUtil(@NotNull Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    /**
     * Sets the display name of the item.
     *
     * @param name The display name to set.
     * @return The current ItemBuilder instance for chaining.
     */
    public @NotNull ItemUtil setDisplayname(@NotNull final String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    /**
     * Adds an enchantment to the item.
     *
     * @param enchantment The enchantment to add.
     * @param level       The level of the enchantment.
     * @return The current ItemBuilder instance for chaining.
     */
    public @NotNull ItemUtil addEnchantment(@NotNull Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Sets the amount of the item.
     *
     * @param amount The amount to set.
     * @return The current ItemBuilder instance for chaining.
     */
    public @NotNull ItemUtil setAmount(final int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    /**
     * Sets the lore (description) of the item.
     *
     * @param lore The lore to set.
     * @return The current ItemBuilder instance for chaining.
     */
    public @NotNull ItemUtil setLore(@NotNull final String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    /**
     * Makes the item unbreakable.
     */
    public @NotNull ItemUtil setUnbreakable() {
        itemMeta.setUnbreakable(true);
        return this;
    }

    /**
     * Makes the item glow by adding a hidden enchantment.
     */
    public @NotNull ItemUtil setGlowing() {
        itemMeta.addEnchant(Enchantment.UNBREAKING, 0, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    /**
     * Builds the final ItemStack with the applied settings.
     *
     * @return The resulting ItemStack.
     */
    public @NotNull ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}