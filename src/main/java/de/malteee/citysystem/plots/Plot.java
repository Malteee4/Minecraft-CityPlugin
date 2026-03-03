package de.malteee.citysystem.plots;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.Database;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Plot {

    protected String id, name;
    protected City city;
    protected List<Area> areas;
    protected ArrayList<UUID> buildingRights = new ArrayList<>(); //max: 5
    protected UUID owner;
    protected boolean rentable;

    public Plot(String id, City city, ArrayList<Area> areas, String name) {
        this.id = id;
        this.city = city;
        this.areas = areas;
        this.name = name;
        this.rentable = false;
    }

    public void addArea() {

    }

    public void removeArea() {

    }

    public boolean isInside(Player player) {
        return false;
    }

    public City getCity() {
        return city;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public ArrayList<UUID> getBuildingRights() {
        return buildingRights;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addBuildingRights() {
        //TODO

    }

    public void removeBuildingRights() {
        //TODO

    }

    public boolean isRentable() {
        return rentable;
    }

    public abstract void setRentable(boolean val);
}
