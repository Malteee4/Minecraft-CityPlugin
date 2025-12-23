package de.malteee.citysystem.core;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.area.AreaChecker;
import de.malteee.citysystem.plots.Residential;
import de.malteee.citysystem.plots.Shop;
import de.malteee.citysystem.utilities.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.sql.ResultSet;
import java.util.ArrayList;
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
    private int daysActive;
    private boolean active = true, publicSpawn = false;
    private Location spawnpoint;

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
}
