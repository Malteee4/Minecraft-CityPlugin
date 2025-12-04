package de.malteee.citysystem.jobs;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.utilities.InventoryBuilder;
import de.malteee.citysystem.utilities.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JobCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        Job currentJob = cPlayer.getJob();
        new InventoryBuilder(3*9, "Jobs", player)
                .setSlot(10, new ItemBuilder(Material.GOLDEN_AXE,1 ).setName("§3Lumberjack").build(), event -> {
                    event.setCancelled(true);
                })
                .setSlot(11, new ItemBuilder(Material.GOLDEN_PICKAXE, 1).setName("§3Miner").build(), event -> {
                    event.setCancelled(true);
                })
                .setSlot(12, new ItemBuilder(Material.GOLDEN_SWORD, 1).setName("§3Hunter").build(), event -> {
                    event.setCancelled(true);
                })
                .setSlot(13, new ItemBuilder(Material.FISHING_ROD, 1).setName("§3Fisher").build(), event -> {
                    event.setCancelled(true);
                })
                .setSlot(14, new ItemBuilder(Material.EMERALD, 1).setName("§3Trader").build(), event -> {
                    event.setCancelled(true);
                })
                .setSlot(15, new ItemBuilder(Material.WHITE_CONCRETE, 1).setName("§3Builder").build(), event -> {
                    event.setCancelled(true);
                })
                .setSlot(16, new ItemBuilder(Material.BARRIER, 1).setName("§3None").build(), event -> {
                    event.setCancelled(true);
                }).open();
        return false;
    }
}
