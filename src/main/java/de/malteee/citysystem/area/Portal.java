package de.malteee.citysystem.area;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.core.Database;
import de.malteee.citysystem.utilities.Tools;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class Portal implements Listener {

    private static HashSet<Portal> portals = new HashSet<>();
    private Location location1, location2, destination;
    int x_min, x_max, y_min, y_max, z_min, z_max;

    static {
        Database db = CitySystem.getDatabase();
        ResultSet rs = db.getResult("SELECT * FROM tbl_portal");
        try {
            while (rs.next()) {
                Plugin plugin = CitySystem.getPlugin();
                Location loc1 = Tools.getLocFromString(rs.getString("LOC1"), plugin), loc2 = Tools.getLocFromString(rs.getString("LOC2"), plugin),
                        destination = Tools.getLocFromString(rs.getString("DESTINATION"), plugin);
                new Portal(loc1, loc2, destination);
            }
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Portal(Location loc1, Location loc2, Location destination) {
        this.location1 = loc1; this.location2 = loc2; this.destination = destination;
        x_min = Math.min(loc1.getBlockX(), loc2.getBlockX()); y_min = Math.min(loc1.getBlockY(), loc2.getBlockY()); z_min = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        x_max = Math.max(loc1.getBlockX(), loc2.getBlockX()); y_max = Math.max(loc1.getBlockY(), loc2.getBlockY()); z_max = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        for(int i1 = x_min; i1 < x_max; i1++) {
            for (int i2 = y_min; i2 < y_min; i2++) {
                for (int i3 = z_min; i3 < z_max; i3++) {
                    Location loc = new Location(loc1.getWorld(), i1, i2, i3);
                    loc.getBlock().setType(Material.NETHER_PORTAL);
                }
            }
        }
        portals.add(this);
    }

    public Location getLoc1() {
        return location1;
    }

    public Location getLoc2() {
        return location2;
    }

    public Location getDestination() {
        return destination;
    }

    public boolean isPlayerInside(Player player) {
        Location loc = player.getLocation();
        return ((loc.getBlockX() <= x_max && loc.getBlockX() >= x_min)
                && ((loc.getBlockY() <= y_max && loc.getBlockY() >= y_min))
                && (loc.getBlockZ() <= z_max && loc.getBlockZ() >= z_min));
    }

    @EventHandler
    public static void handlePlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        for (Portal portal : portals) {
            if (portal.isPlayerInside(player))
                player.teleport(portal.getDestination());
        }
    }

}
