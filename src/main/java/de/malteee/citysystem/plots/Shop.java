package de.malteee.citysystem.plots;

import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.CityPlayer;

import java.util.ArrayList;

public class Shop extends Plot implements Rentable {

    public Shop(String id, City city) {
        super(id, city, null, null);
    }

    public Shop(String id, City city, ArrayList<Area> areas) {
        super(id, city, areas, null);
    }

    @Override
    public void startRenting(CityPlayer player) {

    }

    @Override
    public void stopRenting() {

    }

    @Override
    public void setRent(int rent) {

    }

    @Override
    public int getRent() {
        return 0;
    }
}
