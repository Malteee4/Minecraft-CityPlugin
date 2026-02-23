package de.malteee.citysystem.plots;

import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.CityPlayer;

public class Shop extends Plot implements Rentable {

    protected Shop(String id, City city) {
        super(id, city);
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
