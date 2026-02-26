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

public class NetherCommand implements CommandExecutor {

    private static HashMap<Player, Location> cooldown = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reset")) {
                if (!player.hasPermission("CitySystem.resetNetherWorld")) return false;

            }
        }
        if ((CitySystem.netherWorld == null)) {return false;}
        if (player.getWorld().equals(CitySystem.netherWorld)) {
            player.sendMessage("§cYou are already in the nether!");
            return false;
        }
        if (cooldown.containsKey(player)) {
            //player.sendMessage("§cThis command is still on cooldown!\n You have to wait some seconds until you can use it again!");
            player.teleport(cooldown.get(player));
            return false;
        }
        int counter = 10;
        boolean search = true;
        while (counter > 0) {
            double x = -500 + Math.random() * 1000;
            double z = -500 + Math.random() * 1000;
            double y = 300;
            Location loc = new Location(CitySystem.netherWorld, x, y, z);
            cooldown.put(player, loc);

            while (((loc.getBlock().getType().equals(Material.AIR)) || loc.getBlock().getType().equals(Material.LAVA) ||
                    (loc.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) || (loc.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType() != Material.AIR)
                    || (loc.getBlock().getType().equals(Material.BEDROCK)))
                    && (loc.getY() > 5)) {
                loc.setY(loc.getY() - 1);
            }
            if (counter == 1) {
                player.sendMessage("§cAn error occurred! Please try again!");
                break;
            }
            if (loc.getY() <= 5) {
                counter--;
                continue;
            }
            loc.setY(loc.getY() + 1);
            player.teleport(loc);
            break;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(CitySystem.getPlugin(), () -> {
            cooldown.remove(player);
        }, 20 * 60);
        return false;
    }

    public static void resetNetherCooldown(Player player) {
        cooldown.remove(player);
    }
}
