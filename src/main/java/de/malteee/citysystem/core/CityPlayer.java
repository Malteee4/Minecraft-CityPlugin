package de.malteee.citysystem.core;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.SuperiorArea;
import de.malteee.citysystem.jobs.Job;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.plots.Residential;
import de.malteee.citysystem.utilities.Rank;
import de.malteee.citysystem.utilities.Tools;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CityPlayer {

    private final Player player;
    private final UUID uuid;

    private Residential residential;
    private Area currentArea;
    private SuperiorArea superiorArea;
    private Job job;
    private Location homePoint;
    private Rank rank;

    public static final int BLOCKS_MAX = 60;
    private int blocksWild = 0, daysActive, jobCooldown;   //there is a maximum of how many blocks you're allowed to break and place in the wilderness
    private boolean buildAllowed, inWilderness;

    private Location[] markedLocations = new Location[2];

   public CityPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        try {
            ResultSet rs = CitySystem.getDatabase().getResult("SELECT * FROM tbl_players WHERE PLAYER_ID = '" + player.getUniqueId().toString() + "'");
            rs.next();
            if (!rs.getString("HOME").equalsIgnoreCase("NONE"))
                homePoint = Tools.getLocFromString(rs.getString("HOME"), CitySystem.getPlugin());
            this.job = Job.valueOf(rs.getString("JOB"));
            this.rank = Rank.valueOf(rs.getString("RANK"));
            if (rank == Rank.NONE) {
                CitySystem.getDatabase().execute("UPDATE tbl_players SET RANK='PLAYER' WHERE PLAYER_ID='" + player.getUniqueId().toString() + "'");
                rank = Rank.PLAYER;
            }
            FileConfiguration config = CitySystem.getPlugin().getConfig();
            if (!config.contains("active." + player.getUniqueId().toString()))
                config.set("active." + player.getUniqueId().toString(), 0);
            daysActive = config.getInt("active." + player.getUniqueId().toString());
            if (config.contains("job_cooldown." + uuid.toString()))
                jobCooldown = config.getInt("job_cooldown." + uuid.toString());
            else
                jobCooldown = 0;
            CitySystem.getPlugin().saveConfig();
            rs.close();
            player.setPlayerListName(" " + rank.getDisplay().replace("%player%", player.getName()) + " ");
        }catch (Exception exception) {
            exception.printStackTrace();
        }

    }
    public boolean isBuildAllowed() {
        return buildAllowed;
    }

    public void setBuildAllowed(boolean b) {
        buildAllowed = b;
    }

    public boolean isInWilderness() {
        return inWilderness;
    }

    public void setInWilderness(boolean b) {
        this.inWilderness = b;
    }

    public int getBlocksInWilderness() {
        return blocksWild;
    }

    public void setBlocksWilderness(int i) {
        blocksWild = i;
    }

    public void setSuperiorArea(SuperiorArea area) {
        this.superiorArea = area;
    }

    public SuperiorArea getSuperiorArea() {
        return superiorArea;
    }

    public void setCurrentArea(Area area) {
        this.currentArea = area;
    }

    public Area getCurrentArea() {
        return currentArea;
    }

    public boolean isInArea() {
        return  currentArea == null;
    }

    public Player toPlayer() {
        return player;
    }

    public Job getJob() {
       return job;
    }

    public boolean hasJob() {
       return job != null;
    }

    public int getJobCooldown() {
       return jobCooldown;
    }

    public void setJobCooldown(FileConfiguration config) {
        if (config.contains("job_cooldown." + uuid.toString()))
            jobCooldown = config.getInt("job_cooldown." + uuid.toString());
        else
            jobCooldown = 0;
    }

    public void setJob(Job job) {
        FileConfiguration config = CitySystem.getPlugin().getConfig();
        if (!config.contains("job_cooldown." + uuid.toString())) {
            if (!config.contains("job_cooldown.list")) {
                List<String> list = new ArrayList<>();
                list.add(uuid.toString());
                config.set("job_cooldown.list", list);
            }else {
                List<String> list = config.getStringList("job_cooldown.list");
                list.add(uuid.toString());
                config.set("job_cooldown.list", list);
            }
        }
        if (job != Job.NONE) {
            config.set("job_cooldown." + uuid.toString(), 5);
            CitySystem.getPlugin().saveConfig();
            jobCooldown = 5;
        }
        try {
            CitySystem.getDatabase().execute("UPDATE tbl_players SET JOB='" + job.toString() + "' WHERE PLAYER_ID='" + this.uuid.toString() + "'");
        }catch (Exception exception) {
            exception.printStackTrace();
        }
        this.job = job;
        CitySystem.getJm().changeJob(player, job);
    }

    public void setMarked(Location loc, int index) {
       markedLocations[index] = loc;
    }

    public boolean isMarked(int index) {
       return !(markedLocations[index] == null);
    }

    public Location[] getMarkedLocations() {
       return markedLocations;
    }

    public int getDaysActive() {
        return daysActive;
    }

    public void setHomePoint(Location home) {
       try {
           CitySystem.getDatabase().execute("UPDATE tbl_players SET HOME = '" + Tools.locationToString(home) + "' WHERE PLAYER_ID = '" + this.player.getUniqueId() + "'");
           this.homePoint = home;
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    public Location getHomePoint() {
       return homePoint;
    }

    public boolean hasHomePoint() {
       return homePoint != null;
    }

    public void setRank(Rank rank) {
       this.rank = rank;
       try {
           CitySystem.getDatabase().execute("UPDATE tbl_players SET RANK='" + rank.toString() + "' WHERE PLAYER_ID='" + player.getUniqueId() + "'");
       } catch (Exception e) {
           e.printStackTrace();
       }
       player.setPlayerListName(" " + rank.getDisplay().replace("%player%", player.getName()) + " ");
    }

    public Rank getRank() {
       return rank;
    }
}
