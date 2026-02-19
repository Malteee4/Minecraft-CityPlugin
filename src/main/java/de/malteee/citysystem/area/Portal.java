package de.malteee.citysystem.area;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.core.Database;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class Portal implements Listener {

    private static HashSet<Portal> portals = new HashSet<>();
    private Location location1, location2, destination;

    static {
        Database db = CitySystem.getDatabase();
        ResultSet rs = db.getResult("SELECT * FROM tbl_portal");
        try {
            while (rs.next()) {

            }
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Portal(Location loc1, Location loc2, Location destination) {

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

    @EventHandler
    public static void handlePlayerMove(PlayerMoveEvent event) {

    }

}
