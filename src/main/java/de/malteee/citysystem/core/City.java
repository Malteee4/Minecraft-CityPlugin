package de.malteee.citysystem.core;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.area.AreaChecker;
import de.malteee.citysystem.plots.Residential;
import de.malteee.citysystem.plots.Shop;
import de.malteee.citysystem.utilities.Tools;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class City implements Listener {

    private String name, welcome, goodbye;
    private UUID owner;
    private ArrayList<UUID> buildingRight = new ArrayList<>();
    private List<Expansion> expansions = new ArrayList<>();

    private List<Area> areas = new ArrayList<>();
    private List<Residential> plots = new ArrayList<>();
    private List<Shop> shops = new ArrayList<>();

    private double totalIncome, experience;
    private int daysActive, s;
    private boolean active = true, publicSpawn = false, showBorder = false;

    private Location spawnpoint;
    private ArrayList<Location> corners = new ArrayList<>();

    private Stage stage;

    public City(String name, Player owner, Area area, Location position) {
        try {
            CitySystem.getDatabase().execute("INSERT INTO tbl_city(CITY_ID, WELCOME_MSG, GOODBYE_MSG, SPAWN, PLAYER_ID, DAYS_ACTIVE, PUBLIC_SPAWN, BUILD_RIGHT, EXPANSION, STAGE) VALUES(" +
                    "'" + name + "', 'You''ve entered a city!', '', '" + Tools.locationToString(position) + "', '" + owner.getUniqueId().toString() + "', 1, 'FALSE', '', '', '" + Stage.SETTLEMENT + "')");
            CitySystem.getDatabase().execute("INSERT INTO tbl_city_areas(CITY_ID, AREA_ID) VALUES('" + name + "', '" + area.getId() + "')");
            daysActive = 1;
            areas.add(area);
            area.setCity(this);
            this.owner = owner.getUniqueId();
            this.name = name;
            this.welcome = "You've entered a city!";
            this.spawnpoint = position;
            this.stage = Stage.SETTLEMENT;
            this.buildingRight.add(owner.getUniqueId());
            showBorder();
            Bukkit.getScheduler().scheduleSyncDelayedTask(CitySystem.getPlugin(), this::stopShowingBorder, 20 * 40);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public City(String id) {
        try {
            ResultSet rs = CitySystem.getDatabase().getCon().prepareStatement("SELECT * FROM tbl_city WHERE CITY_ID = '" + id + "'").executeQuery();
            while (rs.next()) {
                this.owner = UUID.fromString(rs.getString("PLAYER_ID"));
                this.welcome = rs.getString("WELCOME_MSG");
                this.goodbye = rs.getString("GOODBYE_MSG");
                this.spawnpoint = Tools.getLocFromString(rs.getString("SPAWN"), CitySystem.getPlugin());
                this.daysActive = rs.getInt("DAYS_ACTIVE");
                this.publicSpawn = rs.getBoolean("PUBLIC_SPAWN");
                this.buildingRight.add(this.owner);
                try {
                    this.stage = Stage.valueOf(rs.getString("STAGE"));
                } catch (Exception e) {
                    this.stage = Stage.SETTLEMENT;
                }
            }rs.close();
            rs = CitySystem.getDatabase().getCon().prepareStatement("SELECT * FROM tbl_city_areas WHERE CITY_ID = '" + id + "'").executeQuery();
            while (rs.next()) {
                Area area = AreaChecker.getAreaByID(rs.getString("AREA_ID"));
                if (area != null) {
                    area.setCity(this);
                    this.areas.add(area);
                }
            }
            rs.close();
            this.name = id;
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void setSpawnAccess(boolean b) {
        this.publicSpawn = b;
        try {
            CitySystem.getDatabase().execute("UPDATE tbl_city SET PUBLIC_SPAWN = '" + b + "' WHERE CITY_ID = '" + this.name + "'");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSpawnPublic() {
        return publicSpawn;
    }

    public String getName() {
        return name;
    }

    public String getWelcomeMessage() {
        return welcome;
    }

    public String getGoodbyeMessage() {
        return goodbye;
    }

    public Location getSpawn() {
        return spawnpoint;
    }

    public UUID getOwner() {
        return owner;
    }

    public ArrayList<UUID> getBuilding_right() {
        return buildingRight;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public Stage getStage() {
        return stage;
    }

    public boolean hasExpansion(Expansion expansion) {
        return expansions.contains(expansion);
    }

    public void addArea(Area area) {
        CitySystem.getDatabase().execute("INSERT INTO tbl_city_areas(CITY_ID, AREA_ID) VALUES('" + name + "', '" + area.getId() + "')");
        this.areas.add(area);
    }

    public boolean delete() {
        try {
            for (Area area : areas)
                area.delete();
            Database db = CitySystem.getDatabase();
            db.execute("DELETE FROM tbl_city WHERE CITY_ID='" + name + "'");
            db.execute("DELETE FROM tbl_city_areas WHERE CITY_ID='" + name + "'");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Residential> getPlots() {
        return plots;
    }

    public void addResidentialPlot(Residential residential) {
        this.plots.add(residential);
    }

    public List<Shop> getShops() {
        return shops;
    }

    public int getSize() {
        int size = 0;
        for (Area area : areas)
            size += area.getSurface();
        return size;
    }

    public void showBorder() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
        showBorder = true;
        corners = getCorners();
        s = Bukkit.getScheduler().scheduleSyncRepeatingTask(CitySystem.getPlugin(), () -> {
            if (!showBorder)
                Bukkit.getScheduler().cancelTask(s);
            if (player.isOnline()) {
                Player pl = player.getPlayer();
                if (pl != null) {
                    for (Location corner : corners) {
                        corner.setZ(corner.getBlockZ() + 0.5);
                        corner.setX(corner.getBlockX() + 0.5);
                        for (int i = 10; i < 400; i++) {
                            corner.setY(i * 0.5);
                            Particle.DustOptions orangeDust = new Particle.DustTransition(Color.fromRGB(255, 0, 0), Color.fromRGB(255, 160, 0), 1.0F);
                            pl.spawnParticle(Particle.DUST_COLOR_TRANSITION, corner, 1, orangeDust);
                        }
                    }
                }
            }
        }, 0, 5);
    }

    public void stopShowingBorder() {
        showBorder = false;
    }

    public boolean borderShown() {
        return showBorder;
    }

    public ArrayList<Location> getCorners() {
        ArrayList<Location> corners = new ArrayList<>();
        for (Area area : areas) {
            for (Location loc : area.getCorners()) {
                boolean corner = false;
                Location neighbor1 = new Location(loc.getWorld(), loc.getBlockX() + 1, loc.getY(), loc.getBlockZ());
                Location neighbor2 = new Location(loc.getWorld(), loc.getBlockX() - 1, loc.getY(), loc.getBlockZ());
                Location neighbor3 = new Location(loc.getWorld(), loc.getBlockX(), loc.getY(), loc.getBlockZ() + 1);
                Location neighbor4 = new Location(loc.getWorld(), loc.getBlockX(), loc.getY(), loc.getBlockZ() - 1);
                Location neighbor5 = new Location(loc.getWorld(), loc.getBlockX() + 1, loc.getY(), loc.getBlockZ() - 1);
                Location neighbor6 = new Location(loc.getWorld(), loc.getBlockX() - 1, loc.getY(), loc.getBlockZ() - 1);
                Location neighbor7 = new Location(loc.getWorld(), loc.getBlockX() + 1, loc.getY(), loc.getBlockZ() + 1);
                Location neighbor8 = new Location(loc.getWorld(), loc.getBlockX() - 1, loc.getY(), loc.getBlockZ() + 1);
                for (Location neighbor : Arrays.asList(neighbor1, neighbor2, neighbor3, neighbor4, neighbor5, neighbor6, neighbor7, neighbor8)) {
                    Area neighborArea = AreaChecker.getAreaByLocation(neighbor);
                    if (neighborArea == null) {
                        corner = true;
                        continue;
                    }else if (neighborArea.getType().equals(Area.AreaType.CITY) || neighborArea.getType().equals(Area.AreaType.PLOT)) {
                        City other = neighborArea.getCity();
                        if (!other.equals(this))
                            corner = true;
                    }else {
                        corner = true;
                    }
                }
                if (corner)
                    corners.add(loc);
            }
        }
        return corners;
    }
}
