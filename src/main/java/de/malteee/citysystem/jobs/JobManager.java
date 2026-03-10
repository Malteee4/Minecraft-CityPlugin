package de.malteee.citysystem.jobs;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.utilities.Tools;
import io.papermc.paper.event.player.PlayerPurchaseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.MerchantRecipe;

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
    private final HashMap<UUID, Double> tempPoints = new HashMap<>();
    private final HashMap<UUID, Integer> level = new HashMap<>();
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
                double points = tempPoints.get(uuid);
                config.set("job." + uuid, config.getDouble("job." + uuid) + points);
                CitySystem.getPlugin().saveConfig();
                moneyToday.put(uuid, (config.getDouble("job." + uuid) / 11d));
                double moneyToAdd = points / 11d;
                double expToAdd = points / 6d;
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
                tempPoints.put(uuid, 0d);
                if (cPlayer.hasJob())
                    level.put(uuid, getLevelByJob(uuid, cPlayer.getJob()));
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
                    moneyToday.put(uuid, (config.getDouble("job." + uuid) / 11d));
                }else {
                    config.set("job." + uuid, 0);
                    CitySystem.getPlugin().saveConfig();
                }
                tempPoints.put(uuid, 0d);
                if (!job.equals(Job.NONE))
                    level.put(uuid, getLevelByJob(uuid, job));
            }rs.close();
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void changeJob(Player player, Job job) {
        if (job.equals(Job.NONE)) return;
        try {
            ResultSet rs = CitySystem.getDatabase().getResult("SELECT * FROM tbl_jobs INNER JOIN tbl_players ON tbl_jobs.PLAYER_ID = tbl_players.PLAYER_ID WHERE tbl_players.PLAYER_ID='" + player.getUniqueId() + "'");
            rs.next();
            UUID uuid = player.getUniqueId();
            ArrayList<String> values = new ArrayList<>();
            values = Tools.stringToList(rs.getString(job.toString() + "_EXP"));
            exp.put(uuid, Double.parseDouble(values.get(0)));
            moneyAllTime.put(uuid, Double.parseDouble(values.get(1)));
            level.put(uuid, getLevelByJob(uuid, job));
            rs.close();
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
            }rs.close();
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
            }rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return money;
    }

    public int getLevelByJob(UUID uuid, Job job) {
        double exp = 0;
        try {
            ResultSet rs = CitySystem.getDatabase().getResult("SELECT * FROM tbl_jobs WHERE PLAYER_ID='" + uuid + "'");
            if (rs.next()) {
                ArrayList<String> values = Tools.stringToList(rs.getString(job.toString() + "_EXP"));
                exp = Double.parseDouble(values.getFirst());
            }rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int level = 1;
        for (int i = 1; ((Math.pow(6 * i, 1.5)) + 10) <= exp; i++) {
            level++;
            exp -= ((Math.pow(6 * i, 1.5)) + 10);
        }
        return level;
    }

    public int getLevel(double exp) {
        int level = 1;
        for (int i = 1; ((Math.pow(6 * i, 1.5)) + 10) <= exp; i++) {
            level++;
            exp -= ((Math.pow(6 * i, 1.5)) + 10);
        }
        return level;
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
            for (Material m : job.getBlocks()) {
                if (block.equals(m)) {
                    if (player.getActiveItem().getItemMeta().hasEnchant(Enchantment.SILK_TOUCH))
                        return;
                    tempPoints.put(player.getUniqueId(), tempPoints.get(player.getUniqueId()) + (job.getValue(m) * (1 + level.get(player.getUniqueId()) * 0.1)));
                    return;
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
            for (Material m : job.getBlocks()) {
                if (block.equals(m)) {
                    tempPoints.put(player.getUniqueId(), tempPoints.get(player.getUniqueId()) + (job.getValue(m) * (1 + level.get(player.getUniqueId()) * 0.1)));
                    break;
                }
            }
        }
    }

    @EventHandler
    public void handlePlayerKillEntity(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        if (cPlayer == null) return;

    }

    @EventHandler
    public void handlePlayerCraft(PrepareItemCraftEvent event) {
        Player player = (Player) event.getView().getPlayer();
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        if (cPlayer == null) return;

    }

    @EventHandler
    public void handlePlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        if (cPlayer == null) return;
        if (!cPlayer.hasJob()) return;
        Job job = cPlayer.getJob();
        if (job == Job.FISHER) {
            tempPoints.put(player.getUniqueId(), tempPoints.get(player.getUniqueId()) + (10 * (1 + level.get(player.getUniqueId()) * 0.1)));
        }
    }

    @EventHandler
    public void handlePlayerTrade(PlayerPurchaseEvent event) {
        Player player = event.getPlayer();
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        if (cPlayer == null) return;
        if (!cPlayer.hasJob()) return;
        Job job = cPlayer.getJob();
        if (job == Job.TRADER) {
            MerchantRecipe trade = event.getTrade();
            tempPoints.put(player.getUniqueId(), tempPoints.get(player.getUniqueId()) + (1.4 * (1 + level.get(player.getUniqueId()) * 0.1)));
        }
    }
}
