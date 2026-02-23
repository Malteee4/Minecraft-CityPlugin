package de.malteee.citysystem.commands_admin;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Portal;
import de.malteee.citysystem.commands_general.WorldSpawnCommand;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.utilities.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PortalCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (!player.hasPermission("CitySystem.createPortal")) return false;
        CityPlayer cityPlayer = CitySystem.getCityPlayer(player);
        if (cityPlayer == null) return false;
        if (args.length < 4) return false;
        if (cityPlayer.isMarked(0) && cityPlayer.isMarked(1)) {
            Location loc1 = cityPlayer.getMarkedLocations()[0], loc2 = cityPlayer.getMarkedLocations()[1];
            Location destination = loc1;
            if (args[0].equalsIgnoreCase("spawn")) {
                destination = WorldSpawnCommand.worldSpawn.get(CitySystem.spawnWorld);
            }else if (args[0].equalsIgnoreCase("main")) {
                destination = WorldSpawnCommand.worldSpawn.get(CitySystem.mainWorld);
            }else {
                World world = Bukkit.getWorld(args[0]);
                if (world == null) return false;
                int x = Integer.parseInt(args[1]), y = Integer.parseInt(args[1]), z = Integer.parseInt(args[1]);
                destination = new Location(world, x, y, z);
            }
            new Portal(loc1, loc2, destination, true);
            try {
                CitySystem.getDatabase().execute("INSERT INTO tbl_portal(LOC1, LOC2, DESTINATION) VALUES('" + Tools.locationToString(loc1) + "', '" + Tools.locationToString(loc2) + "', '" +
                        Tools.locationToString(destination) + "')");
            } catch (Exception e) {
                e.printStackTrace();
            }
            player.sendMessage("§aPortal has been created!");
        }
        return false;
    }

    @EventHandler
    public static void handlePlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        for (Portal portal : Portal.portals) {
            if (portal.isPlayerInside(player)) {
                if (portal.getDestination().getWorld().equals(CitySystem.farmWorld)) {
                    player.performCommand("farmworld");
                }else if(portal.getDestination().getWorld().equals(CitySystem.netherWorld)) {
                    player.performCommand("nether");
                }else {
                    player.teleport(portal.getDestination());
                }
                return;
            }
        }
    }
}
