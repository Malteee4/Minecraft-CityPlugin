package de.malteee.citysystem.jobs;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.utilities.Tools;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class JobManager implements Listener {

    private HashMap<UUID, Double> exp = new HashMap<>();
    private HashMap<UUID, Double> moneyAllTime = new HashMap<>();
    private HashMap<UUID, Double> moneyToSave = new HashMap<>();
    private HashMap<UUID, Double> moneyToday = new HashMap<>();

    public JobManager() {
        try {
            ResultSet rs = CitySystem.getDatabase().getResult("SELECT * FROM tbl_jobs INNER JOIN tbl_players ON tbl_jobs.PLAYER_ID = tbl_players.PLAYER_ID");
            while (rs.next()) {
                Job job = Job.valueOf(rs.getString("JOB"));
                if (!job.equals(Job.NONE)) {
                    UUID uuid = UUID.fromString(rs.getString("PLAYER_ID"));
                    ArrayList<String> values = new ArrayList<>();
                    values = Tools.stringToList(rs.getString(job.toString() + "_EXP"));
                    exp.put(uuid, Double.parseDouble(values.get(0)));
                   moneyAllTime.put(uuid, Double.parseDouble(values.get(1)));
                }
            }
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void changeJob(CityPlayer cPlayer, Job job) {

    }

    public void saveMoney() {

    }



    @EventHandler
    public void handlePlayerBreakBlock(BlockBreakEvent event) {

    }

    @EventHandler
    public void handlePlayerPlaceBlock(BlockPlaceEvent event) {

    }

    @EventHandler
    public void handlePlayerKillEntity(EntityDeathEvent event) {

    }

    @EventHandler
    public void handlePlayerCraft(CrafterCraftEvent event) {
        
    }
}
