package de.malteee.citysystem.commands_admin;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Portal;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.utilities.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Portalcommand implements CommandExecutor {

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
                destination = CitySystem.spawnWorld.getSpawnLocation();
            }else if (args[0].equalsIgnoreCase("main")) {
                destination = CitySystem.mainWorld.getSpawnLocation();
            }else {
                World world = Bukkit.getWorld(args[0]);
                if (world == null) return false;
                int x = Integer.parseInt(args[1]), y = Integer.parseInt(args[1]), z = Integer.parseInt(args[1]);
                destination = new Location(world, x, y, z);
            }
            new Portal(loc1, loc2, destination);
            try {
                CitySystem.getDatabase().execute("INSERT INTO tbl_portal(LOC1, LOC2, DESTINATION) VALUES('" + Tools.locationToString(loc1) + "', '" + Tools.locationToString(loc2) + "', '" +
                        Tools.locationToString(destination) + "')");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
