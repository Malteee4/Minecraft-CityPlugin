package de.malteee.citysystem.commands_farming;

import de.malteee.citysystem.CitySystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class FarmworldCommand implements CommandExecutor {

    private static HashMap<Player, Location> cooldown = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reset")) {
                if (!player.hasPermission("CitySystem.resetFarmWorld")) return false;

                return false;
            }
        }
        if ((CitySystem.farmWorld == null)) {return false;}
        if (player.getWorld().equals(CitySystem.farmWorld)) {
            player.sendMessage("§cYou are already in the farmworld!");
            return false;
        }
        if (cooldown.containsKey(player)) {
            //player.sendMessage("§cThis command is still on cooldown!\n You have to wait some seconds until you can use it again!");
            player.teleport(cooldown.get(player));
            return false;
        }
        double x = Math.random() * 1000;
        double z = Math.random() * 1000;
        double y = 300;
        Location loc = new Location(CitySystem.farmWorld, x, y, z);
        cooldown.put(player, loc);
        while (loc.getBlock().getType().equals(Material.AIR))
            loc.setY(loc.getY() - 1);
        loc.setY(loc.getY() + 1);
        player.teleport(loc);
        Bukkit.getScheduler().scheduleSyncDelayedTask(CitySystem.getPlugin(), () -> {
            cooldown.remove(player);
        }, 20 * 30);
        return false;
    }

    public static void resetFarmWorldCooldown(Player player) {
        cooldown.remove(player);
    }
}
