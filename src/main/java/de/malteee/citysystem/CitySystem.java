package de.malteee.citysystem;

import de.malteee.citysystem.area.AreaChecker;
import de.malteee.citysystem.area.AreaCreator;
import de.malteee.citysystem.area.Portal;
import de.malteee.citysystem.area.PosCommand;
import de.malteee.citysystem.chat.MessageBroadcaster;
import de.malteee.citysystem.commands_admin.*;
import de.malteee.citysystem.commands_city.CityCommand;
import de.malteee.citysystem.commands_city.PlotCommand;
import de.malteee.citysystem.commands_city.ShopCommand;
import de.malteee.citysystem.commands_farming.EndCommand;
import de.malteee.citysystem.commands_farming.FarmworldCommand;
import de.malteee.citysystem.commands_farming.NetherCommand;
import de.malteee.citysystem.commands_general.WorldSpawnCommand;
import de.malteee.citysystem.commands_general.*;
import de.malteee.citysystem.core.*;
import de.malteee.citysystem.core.Database;
import de.malteee.citysystem.jobs.JobCommand;
import de.malteee.citysystem.jobs.JobManager;
import de.malteee.citysystem.money_system.MoneyManager;
import de.malteee.citysystem.money_system.ShopSign;
import de.malteee.citysystem.plots.PlotManager;
import de.malteee.citysystem.utilities.*;
import de.malteee.citysystem.chat.PlayerChatListener;
import de.malteee.citysystem.world_managing.*;
import org.bukkit.*;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CitySystem extends JavaPlugin {

    private static CitySystem plugin;
    private static Database db;

    private static MoneyManager mm;
    private static PlotManager pm;
    private static CityManager cm;
    private static JobManager jm;

    private static HashSet<CityPlayer> players = new HashSet<>();

    public static World spawnWorld = Bukkit.getWorld("world");
    public static World mainWorld = Bukkit.getWorld("mainWorld");
    public static World farmWorld = Bukkit.getWorld("farmWorld");
    public static World netherWorld = Bukkit.getWorld("netherWorld");
    public static World endWorld = Bukkit.getWorld("endWorld");

    private List<String> maps = getConfig().getStringList("worlds");

    public static DecimalFormat df = new DecimalFormat("#0.00");

    public void onEnable() {
        plugin = this;
        db = new Database().connect("database");

        PluginManager pluginManager = getPlugin().getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new ShopSign(), this);
        pluginManager.registerEvents(new PlayerChatListener(), this);
        pluginManager.registerEvents(new PlayerLeaveListener(), this);
        pluginManager.registerEvents(new PlayerManipulateWorldListener(), this);
        pluginManager.registerEvents(new StatsSaver(), this);
        pluginManager.registerEvents(new HologramCommand(), this);
        pluginManager.registerEvents(new PlayerDeathListener(), this);
        pluginManager.registerEvents(new AreaCreator(), this);
        pluginManager.registerEvents(new PortalCommand(), this);

        TabCompleter tabCompleter = new TabComplete();
        getCommand("spawn").setExecutor(new WorldSpawnCommand());
        getCommand("setSpawn").setExecutor(new SetWorldSpawnCommand());
        getCommand("breakShop").setExecutor(new BreakShopCommand());
        getCommand("world").setExecutor(new WorldCreation());
        getCommand("money").setExecutor(new MoneyCommand());
        getCommand("money").setTabCompleter(tabCompleter);
        getCommand("home").setExecutor(new HomeCommand());
        getCommand("home").setTabCompleter(tabCompleter);
        getCommand("farmWorld").setExecutor(new FarmworldCommand());
        getCommand("mainWorld").setExecutor(new MainWorldCommand());
        getCommand("createSuperiorArea").setExecutor(new CreateSuperiorArea());
        getCommand("hologram").setExecutor(new HologramCommand());
        getCommand("pos1").setExecutor(new PosCommand());
        getCommand("pos2").setExecutor(new PosCommand());
        getCommand("msg").setExecutor(new MsgCommand());
        getCommand("city").setExecutor(new CityCommand());
        getCommand("city").setTabCompleter(tabCompleter);
        getCommand("setMainSpawn").setExecutor(new SetMainWorldSpawnCommand());
        getCommand("tutorial").setExecutor(new TutorialCommand());
        getCommand("database").setExecutor(new DatabaseCommand());
        getCommand("mot").setExecutor(new MotCommand());
        getCommand("loginStreak").setExecutor(new LoginBonusCommand());
        getCommand("jobs").setExecutor(new JobCommand());
        getCommand("job").setExecutor(new JobCommand());
        getCommand("stats").setExecutor(new StatsCommand());
        getCommand("nether").setExecutor(new NetherCommand());
        getCommand("end").setExecutor(new EndCommand());
        getCommand("createPortal").setExecutor(new PortalCommand());
        getCommand("plot").setExecutor(new PlotCommand());
        getCommand("shop").setExecutor(new ShopCommand());
        getCommand("rank").setExecutor(new RankCommand());

        for(int i = 0; i < maps.size(); i++) {
            if (maps.get(i).equalsIgnoreCase("mainWorld")) {
                WorldCreator w = (WorldCreator) new WorldCreator(maps.get(i)).type(WorldType.NORMAL).generatorSettings("cold_ocean");
                Bukkit.createWorld(w);
                Bukkit.getWorlds().add(Bukkit.getWorld(maps.get(i)));
            }
            String name = maps.get(i);
            World.Environment environment = World.Environment.NORMAL;
            if (name.toLowerCase().contains("nether")) {
                environment = World.Environment.NETHER;
            }else if(name.toLowerCase().contains("end")) {
                environment = World.Environment.THE_END;
            }
            WorldCreator w = (WorldCreator) new WorldCreator(maps.get(i)).environment(environment).type(WorldType.NORMAL);
            Bukkit.createWorld(w);
            Bukkit.getWorlds().add(Bukkit.getWorld(maps.get(i)));
        }

        try {
            ResultSet rs = db.getResult("SELECT VALUE FROM tbl_properties WHERE CODE='worldspawn'");
            while (rs.next()) {
                String loc = rs.getString("VALUE");
                WorldSpawnCommand.worldSpawn.put(spawnWorld, Tools.getLocFromString(loc, this));
            }rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        AreaChecker.initializeAreas();
        Portal.initialize();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            mainWorld = Bukkit.getWorld("mainWorld");
            farmWorld = Bukkit.getWorld("farmWorld");
            netherWorld = Bukkit.getWorld("netherWorld");
            endWorld = Bukkit.getWorld("endWorld");
            try {
                ResultSet rs = db.getResult("SELECT VALUE FROM tbl_properties WHERE CODE='mainspawn'");
                while (rs.next()) {
                    String loc = rs.getString("VALUE");
                    WorldSpawnCommand.worldSpawn.put(mainWorld, Tools.getLocFromString(loc, this));
                }
                rs.close();
            }catch (Exception exception) {
                exception.printStackTrace();
            }
        }, 160);

        //new Border(new Location(mainWorld, -520, 100, -1000), 18100);
        //mainWorld.getWorldBorder().reset();
        new Border(new Location(farmWorld, 0, 100, 0), 2400);
        new Border(new Location(netherWorld, 0, 100, 0), 1200);
        //new Border(new Location(spawnWorld, 600, 100, 1700), 2500);
        //spawnWorld.getWorldBorder().reset();
        new Timer();
        new MessageBroadcaster();
        mm = new MoneyManager();
        cm = new CityManager();
        pm = new PlotManager();
        jm = new JobManager();

        /*try {
            ResultSet rs = db.getResult("SELECT * FROM tbl_players");
            while (rs.next()) {
                db.getCon().prepareStatement("INSERT INTO tbl_jobs(PLAYER_ID, LUMBERJACK_EXP, FISHER_EXP, HUNTER_EXP, BUILDER_EXP, MINER_EXP, TRADER_EXP) VALUES" +
                        "('" + rs.getString("PLAYER_ID") + "', '0.0, 0.0', '0.0, 0.0', '0.0, 0.0', '0.0, 0.0', '0.0, 0.0', '0.0, 0.0')").execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void onDisable() {
        for (CityPlayer player : players)
            removePlayer(player);
        mm.safeMoney();
        db.disconnect();
    }

    public static boolean isRegistered(Player player) {
        try {
            ResultSet rs = db.getCon().prepareStatement("SELECT * FROM tbl_players WHERE PLAYER_ID = '" + player.getUniqueId().toString() + "'").executeQuery();
            if (rs.next()) {
                rs.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void registerPlayer(Player player) {
        try {
            db.getCon().prepareStatement("INSERT INTO tbl_players(PLAYER_ID, MONEY, JOB, RANK, HOME) VALUES('" +
                    player.getUniqueId().toString() + "', 0, 'NONE', 'NONE', 'NONE')").execute();
            db.getCon().prepareStatement("INSERT INTO tbl_jobs(PLAYER_ID, LUMBERJACK_EXP, FISHER_EXP, HUNTER_EXP, BUILDER_EXP, MINER_EXP, TRADER_EXP) VALUES" +
                    "('" + player.getUniqueId().toString() + "', '0.0, 0.0', '0.0, 0.0', '0.0, 0.0', '0.0, 0.0', '0.0, 0.0', '0.0, 0.0')").execute();
            CityPlayer cityPlayer = new CityPlayer(player);
            players.add(cityPlayer);
            mm.createKonto(cityPlayer);
            jm.updatePlayers();
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void loadPlayer(Player player) {
        CityPlayer cityPlayer = new CityPlayer(player);
        players.add(cityPlayer);
    }

    public static void addPlayer(CityPlayer player) {
        players.add(player);
    }

    public static void removePlayer(CityPlayer player) {
        players.remove(player);
        StatsSaver.safeStats(player.toPlayer());
    }

    public static CityPlayer getCityPlayer(Player player) {
        for (CityPlayer pl : players) {
            if (pl.toPlayer().equals(player))
                return pl;
        }
        return null;
    }

    public static CityPlayer getCityPlayer(UUID uuid) {
        for (CityPlayer pl : players) {
            if (pl.toPlayer().getUniqueId().equals(uuid))
                return pl;
        }
        return null;
    }

    public static MoneyManager getMm() {
        return mm;
    }

    public static CityManager getCm() {return cm;}

    public static PlotManager getPm() {return pm;}

    public static JobManager getJm() {return jm;}

    public static Set<CityPlayer> getCityPlayers() {
        return players;
    }

    public static Database getDatabase() {
        return db;
    }

    public static CitySystem getPlugin() {
        return plugin;
    }
}
