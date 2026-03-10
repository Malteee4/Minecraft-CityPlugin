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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class BukkitGlobalInventory implements Listener {
    private final Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> clickActions;
    private final Map<Player, Runnable> closeActions;
    private final Collection<Player> playersOpened;

    public BukkitGlobalInventory(final int inventorySize, final String inventoryName) {
        this.inventory = Bukkit.createInventory(null, inventorySize, inventoryName);
        this.clickActions = new HashMap<>();
        this.closeActions = new HashMap<>();
        this.playersOpened = new ArrayList<>();
        Bukkit.getPluginManager().registerEvents(this, CitySystem.getPlugin());
    }

    public BukkitGlobalInventory setSlot(final int slot, final ItemStack itemStack) {
        this.clickActions.remove(slot);
        this.inventory.setItem(slot, itemStack);
        return this;
    }

    public BukkitGlobalInventory setSlot(final int slot, final ItemStack itemStack, final Consumer<InventoryClickEvent> inventoryClickEvent) {
        this.clickActions.remove(slot);
        this.inventory.setItem(slot, itemStack);
        this.clickActions.put(slot, inventoryClickEvent);
        return this;
    }

    public BukkitGlobalInventory addItem(final ItemStack itemStack) {
        this.inventory.addItem(itemStack);
        this.clickActions.remove(this.inventory.first(itemStack));
        return this;
    }

    public BukkitGlobalInventory addItem(final ItemStack itemStack, final Consumer<InventoryClickEvent> inventoryClickEvent) {
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

    public void addCloseAction(final Player player, final Runnable closeAction) {
        this.closeActions.put(player, closeAction);
    }

    public void removeCloseAction(final Player playerKey) {
        this.closeActions.remove(playerKey);
    }

    public void open(final Player player) {
        player.openInventory(this.inventory);
        this.playersOpened.add(player);
    }

    public void clearCloseActions() {
        this.closeActions.clear();
    }

    public void clearCloseActions(final Player player) {
        if (!this.playersOpened.contains(player)) return;
        this.closeActions.remove(player);
    }

    public void close(final Player player) {
        player.closeInventory();
        this.playersOpened.remove(player);
    }

    public void unregister() {
        this.clickActions.clear();
        this.closeActions.clear();
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }

    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClickEvent(final InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getInventory() != this.inventory) return;
        if (this.clickActions.isEmpty()) return;
        if (!(inventoryClickEvent.getWhoClicked() instanceof Player player)) return;
        if (!this.playersOpened.contains(player)) return;
        final int slot = inventoryClickEvent.getSlot();
        if (!this.clickActions.containsKey(slot)) return;
        this.clickActions.get(slot).accept(inventoryClickEvent);
    }

    @EventHandler
    public void onInventoryCloseEvent(final InventoryCloseEvent inventoryCloseEvent) {
        if (inventoryCloseEvent.getInventory() != this.inventory) return;
        if (this.closeActions.isEmpty()) return;
        if (!(inventoryCloseEvent.getPlayer() instanceof Player player)) return;
        if (!this.playersOpened.contains(player)) return;
        this.closeActions.get(player).run();
        this.closeActions.remove(player);
    }
}
