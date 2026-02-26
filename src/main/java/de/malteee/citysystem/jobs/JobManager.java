package de.malteee.citysystem.jobs;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.utilities.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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

    private final HashMap<UUID, Double> exp = new HashMap<>();
    private final HashMap<UUID, Double> moneyAllTime = new HashMap<>();
    //private HashMap<UUID, Double> moneyToSave = new HashMap<>();
    private final HashMap<UUID, Double> moneyToday = new HashMap<>();
    private final HashMap<UUID, Integer> tempPoints = new HashMap<>();
    private FileConfiguration config = CitySystem.getPlugin().getConfig();

    public JobManager() {
        updatePlayers();
        CitySystem.getPlugin().getServer().getPluginManager().registerEvents(this, CitySystem.getPlugin());
        //(exp, money)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CitySystem.getPlugin(), () -> {
            for (UUID uuid : tempPoints.keySet()) {
                CityPlayer cPlayer = CitySystem.getCityPlayer(uuid);
                if (cPlayer == null) continue;
                if (cPlayer.getJob() == Job.NONE) continue;
                int points = tempPoints.get(uuid);
                config.set("job." + uuid, config.getInt("job." + uuid) + points);
                CitySystem.getPlugin().saveConfig();
                moneyToday.put(uuid, (config.getInt("job." + uuid) / 8d));
                double moneyToAdd = points / 8d;
                double expToAdd = points / 4d;
                exp.put(uuid, exp.get(uuid) + expToAdd);
                moneyAllTime.put(uuid, moneyAllTime.get(uuid) + moneyToAdd);
                try {
                    CitySystem.getDatabase().execute("UPDATE tbl_jobs SET " + cPlayer.getJob().toString() + "_EXP='" + exp.get(uuid) + ", " + moneyAllTime.get(uuid) + "' WHERE PLAYER_ID='" + uuid + "'");
                    //Bukkit.broadcastMessage("UPDATE tbl_jobs SET " + cPlayer.getJob().toString() + "_EXP='" + exp.get(uuid) + ", " + moneyAllTime.get(uuid) + "' WHERE PLAYER_ID='" + uuid + "'");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Bukkit.broadcastMessage(moneyAllTime.get(uuid) + "   " + moneyToAdd + "   " + expToAdd + " Exp");
                CitySystem.getMm().getKonto(cPlayer).addMoney(moneyToAdd);
                tempPoints.put(uuid, 0);
            }
        }, 0, 20*60);
    }

    public void updatePlayers() {
        try {
            ResultSet rs = CitySystem.getDatabase().getResult("SELECT * FROM tbl_jobs INNER JOIN tbl_players ON tbl_jobs.PLAYER_ID = tbl_players.PLAYER_ID");
            while (rs.next()) {
                Job job = Job.valueOf(rs.getString("JOB"));
                UUID uuid = UUID.fromString(rs.getString("PLAYER_ID"));
                if (tempPoints.containsKey(uuid)) continue;
                if (job != Job.NONE) {
                    ArrayList<String> values = Tools.stringToList(rs.getString(job.toString() + "_EXP"));
                    exp.put(uuid, Double.parseDouble(values.get(0)));
                    moneyAllTime.put(uuid, Double.parseDouble(values.get(1)));
                }
                if (config.contains("job." + uuid)) {
                    moneyToday.put(uuid, (config.getInt("job." + uuid) / 8d));
                }else {
                    config.set("job." + uuid, 0);
                    CitySystem.getPlugin().saveConfig();
                }
                tempPoints.put(uuid, 0);
            }
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void changeJob(Player player, Job job) {
        try {
            ResultSet rs = CitySystem.getDatabase().getResult("SELECT * FROM tbl_jobs INNER JOIN tbl_players ON tbl_jobs.PLAYER_ID = tbl_players.PLAYER_ID WHERE tbl_players.PLAYER_ID='" + player.getUniqueId() + "'");
            rs.next();
            UUID uuid = player.getUniqueId();
            ArrayList<String> values = new ArrayList<>();
            values = Tools.stringToList(rs.getString(job.toString() + "_EXP"));
            exp.put(uuid, Double.parseDouble(values.get(0)));
            moneyAllTime.put(uuid, Double.parseDouble(values.get(1)));
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public double getMoneyToday(Player player) {
        return moneyToday.getOrDefault(player.getUniqueId(), 0d);
    }

    public double getExpByJob(Player player, Job job) {
        double exp = 0;
        try {
            ResultSet rs = CitySystem.getDatabase().getResult("SELECT * FROM tbl_jobs WHERE PLAYER_ID='" + player.getUniqueId() + "'");
            if (rs.next()) {
                ArrayList<String> values = Tools.stringToList(rs.getString(job.toString() + "_EXP"));
                exp = Double.parseDouble(values.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exp;
    }

    public double getMoneyByJob(Player player, Job job) {
        double money = 0;
        try {
            ResultSet rs = CitySystem.getDatabase().getResult("SELECT * FROM tbl_jobs WHERE PLAYER_ID='" + player.getUniqueId() + "'");
            if (rs.next()) {
                ArrayList<String> values = Tools.stringToList(rs.getString(job.toString() + "_EXP"));
                money = Double.parseDouble(values.get(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return money;
    }

    @EventHandler
    public void handlePlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        if (cPlayer == null) return;
        Job job = cPlayer.getJob();
        if (job != Job.NONE) {
            if (player.getWorld().equals(CitySystem.mainWorld) && (job != Job.BUILDER && job != Job.TRADER && job != Job.FISHER))
                return;
            Material block = event.getBlock().getType();
            for (String s : job.getBlocks()) {
                if (block.toString().contains(s)) {
                    tempPoints.put(player.getUniqueId(), tempPoints.get(player.getUniqueId()) + 1);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void handlePlayerPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        if (cPlayer == null) return;
        Job job = cPlayer.getJob();
        if (job != Job.NONE) {
            if (player.getWorld().equals(CitySystem.mainWorld) && (job != Job.BUILDER && job != Job.TRADER && job != Job.FISHER))
                return;
            Material block = event.getBlock().getType();
            for (String s : job.getBlocks()) {
                if (block.toString().contains(s)) {
                    tempPoints.put(player.getUniqueId(), tempPoints.get(player.getUniqueId()) + 1);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void handlePlayerKillEntity(EntityDeathEvent event) {

    }

    @EventHandler
    public void handlePlayerCraft(CrafterCraftEvent event) {
        
    }
}
