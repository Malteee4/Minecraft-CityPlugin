package de.malteee.citysystem.utilities;

import de.malteee.citysystem.CitySystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InventoryBuilder implements Listener {

    private final @Nonnull Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> clickActions;
    private Runnable closeAction;
    private final Player player;

    public InventoryBuilder(final int inventorySize, final @Nonnull String inventoryName, Player player) {
        this.inventory = Bukkit.createInventory(null, inventorySize, inventoryName);
        this.clickActions = new HashMap<>();
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, CitySystem.getPlugin());
    }

    public InventoryBuilder setSlot(final int slot, final @Nonnull ItemStack itemStack) {
        this.clickActions.remove(slot);
        this.inventory.setItem(slot, itemStack);
        return this;
    }

    public InventoryBuilder setSlot(final int slot, final @Nonnull ItemStack itemStack, final @Nonnull Consumer<InventoryClickEvent> inventoryClickEvent) {
        this.clickActions.remove(slot);
        this.inventory.setItem(slot, itemStack);
        this.clickActions.put(slot, inventoryClickEvent);
        return this;
    }

    public InventoryBuilder setSlots(final @Nonnull ItemStack itemStack, final @Nonnull int... slots) {
        for (int i : slots)
            this.inventory.setItem(i, itemStack);
        return this;
    }

    public InventoryBuilder setSlot(final int slot, final @Nonnull ItemStack itemStack, boolean set) {
        if (set) {
            this.clickActions.remove(slot);
            this.inventory.setItem(slot, itemStack);
        }
        return this;
    }

    public InventoryBuilder addItem(final @Nonnull ItemStack itemStack) {
        this.inventory.addItem(itemStack);
        this.clickActions.remove(this.inventory.first(itemStack));
        return this;
    }

    public InventoryBuilder addItem(final @Nonnull ItemStack itemStack, final @Nonnull Consumer<InventoryClickEvent> inventoryClickEvent) {
        this.inventory.addItem(itemStack);
        final int slot = this.inventory.first(itemStack);
        this.clickActions.remove(slot);
        this.clickActions.put(slot, inventoryClickEvent);
        return this;
    }

    public void clear(final int slot) {
        this.clickActions.remove(slot);
        this.inventory.clear(slot);
    }

    public void clear() {
        this.clickActions.clear();
        this.inventory.clear();
    }

    public void addCloseAction(final @Nonnull Runnable closeAction) {
        this.closeAction = closeAction;
    }

    public void removeCloseAction() {
        this.closeAction = null;
    }

    public void open() {
        player.openInventory(this.inventory);
    }

    public void close() {
        player.closeInventory();
    }

    public void unregister() {
        this.clickActions.clear();
        this.closeAction = null;
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }

    @EventHandler
    public void onInventoryClickEvent(final InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getInventory() != this.inventory) return;
        if (this.clickActions.isEmpty()) return;
        if (!(inventoryClickEvent.getWhoClicked() instanceof Player actor)) return;
        if (this.player != actor) return;
        final int slot = inventoryClickEvent.getSlot();
        if (!this.clickActions.containsKey(slot)) return;
        this.clickActions.get(slot).accept(inventoryClickEvent);
    }

    @EventHandler
    public void onInventoryCloseEvent(final InventoryCloseEvent inventoryCloseEvent) {
        if (inventoryCloseEvent.getInventory() != this.inventory) return;
        if (this.closeAction == null) return;
        if (!(inventoryCloseEvent.getPlayer() instanceof Player actor)) return;
        if (this.player != actor) return;
        this.closeAction.run();
        this.closeAction = null;
    }
}
