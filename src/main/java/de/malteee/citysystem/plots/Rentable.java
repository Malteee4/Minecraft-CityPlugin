package de.malteee.citysystem.plots;

import de.malteee.citysystem.core.CityPlayer;

public interface Rentable {

    void startRenting(CityPlayer player);
    void stopRenting();
    void setRent(int i);
    int getRent();

}
