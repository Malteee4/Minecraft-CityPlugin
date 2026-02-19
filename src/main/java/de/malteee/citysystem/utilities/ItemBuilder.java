package de.malteee.citysystem.utilities;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ItemBuilder {

    private ItemMeta itemMeta;
    private ItemStack itemStack;

    public ItemBuilder(Material material, int amount) {
        itemStack = new ItemStack(material, amount);
        itemMeta  = itemStack.getItemMeta();
    }
    public ItemBuilder(final @Nonnull String itemsAdderNameSpace) {
        //this.customStack = CustomStack.getInstance(itemsAdderNameSpace);
        //this.itemStack = this.customStack.getItemStack();
        this.itemStack = new ItemStack(Material.PAPER);
        this.itemMeta = this.itemStack.getItemMeta();
    }
    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }
    public ItemBuilder setName(String name) {
        itemMeta.setDisplayName(name);
        return this;
    }
    public String getName() {
        return this.itemStack.getItemMeta().getDisplayName();
    }
    public ItemBuilder setLore(String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }
    public ItemBuilder setLore(ArrayList<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    public ItemBuilder addEnchantment(final @Nonnull Enchantment enchantment, final int level, final boolean add) {
        if (add)
            this.itemStack.addEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder removeEnchantment(final @Nonnull Enchantment enchantment) {
        this.itemStack.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder addEnchantments(final @Nonnull Map<Enchantment, Integer> enchantments) {
        this.itemStack.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder removeEnchantments(final @Nonnull Collection<Enchantment> enchantments) {
        enchantments.forEach(this.itemStack::removeEnchantment);
        return this;
    }

    public ItemBuilder setEnchantmentGlintOverride(boolean value) {
        this.itemMeta.setEnchantmentGlintOverride(value);
        return this;
    }

    public ItemBuilder setEnchantmentGlintOverride(boolean value, boolean add) {
        if (add) this.itemMeta.setEnchantmentGlintOverride(value);
        return this;
    }

    public ItemBuilder addFlag(ItemFlag flag) {
        this.itemMeta.addItemFlags(flag);
        return this;
    }

    public ItemBuilder removeFlag(ItemFlag flag) {
        this.itemMeta.removeItemFlags(flag);
        return this;
    }
}
