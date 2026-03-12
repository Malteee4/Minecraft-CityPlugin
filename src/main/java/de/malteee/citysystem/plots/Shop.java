package de.malteee.citysystem.plots;

import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.CityPlayer;

import java.util.ArrayList;

@Deprecated
public class Shop extends Plot {

    public Shop(String id, City city) {
        super(id, city, null, null, false);
    }

    public Shop(String id, City city, ArrayList<Area> areas) {
        super(id, city, areas, null, false);
    }

    public void startRenting(CityPlayer player) {

    }

    public void stopRenting() {

    }

    public void setRent(int rent) {

    }

    public int getRent() {
        return 0;
    }

    public void setRentable(boolean val) {
        super.rentable = val;
    }
}
