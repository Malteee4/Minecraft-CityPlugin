package de.malteee.citysystem.core;

import de.malteee.citysystem.CitySystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

public class StatsSaver implements Listener {

    private HashMap<UUID, Integer> temp_block_breaks = new HashMap<>();
    private HashMap<UUID, Integer> temp_block_places = new HashMap<>();
    private HashMap<UUID, Integer> temp_entity_kills = new HashMap<>();
    private HashMap<UUID, Integer> temp_player_kills = new HashMap<>();

    public static void initializeStats(UUID player) {
        if (statsExist(player)) {

        }else {
            try {
                CitySystem.getDatabase().execute("INSERT INTO tbl_player_stats(PLAYER_ID, BLOCK_BREAK, BLOCK_PLACE, ENTITY_KILL, PLAYER_KILL, DEATHS, DISTANCE)" +
                        "VALUES('" + player.toString() + "', 0, 0, 0, 0, 0, 0)");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private static boolean statsExist(UUID player) {
        try {
            ResultSet rs = CitySystem.getDatabase().getCon().prepareStatement(
                    "SELECT * FROM tbl_player_stats WHERE PLAYER_ID = '" + player.toString() + "'").executeQuery();
            if (rs.next()) {
                rs.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @EventHandler
    public void handlePlayerBreakBlock(BlockBreakEvent event) {
        if (event.getPlayer().getWorld().equals(CitySystem.spawnWorld)) return;
        if (!temp_block_breaks.containsKey(event.getPlayer())) temp_block_breaks.put(event.getPlayer().getUniqueId(), 1);
        else temp_block_breaks.put(event.getPlayer().getUniqueId(), temp_block_breaks.get(event.getPlayer()) + 1);
    }

    @EventHandler
    public void handlePlayerPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (event.getPlayer().getWorld().equals(CitySystem.spawnWorld)) return;
        if (!temp_block_places.containsKey(event.getPlayer())) temp_block_places.put(event.getPlayer().getUniqueId(), 1);
        else temp_block_places.put(event.getPlayer().getUniqueId(), temp_block_places.get(event.getPlayer()) + 1);
    }

    @EventHandler
    public void handlePlayerKillEntity(EntityDeathEvent event) {

    }

    public static void safeStats(Player player) {
        try {

        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
