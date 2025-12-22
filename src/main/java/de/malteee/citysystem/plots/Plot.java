package de.malteee.citysystem.plots;

import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.core.City;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Plot {

    private final String id;
    private final City city;
    private List<Area> areas;
    private ArrayList<UUID> buildingRights = new ArrayList<>();

    protected Plot(String id, City city) {
        this.id = id;
        this.city = city;
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
}
