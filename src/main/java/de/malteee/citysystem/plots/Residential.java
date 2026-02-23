package de.malteee.citysystem.plots;

import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.CityPlayer;

public class Residential extends Plot implements Rentable {



    protected Residential(String id, City city) {
        super(id, city);

    }

    @Override
    public void startRenting(CityPlayer player) {

    }

    @Override
    public void stopRenting() {

    }

    @Override
    public void setRent(int i) {

    }

    @Override
    public int getRent() {
        return 0;
    }
}
