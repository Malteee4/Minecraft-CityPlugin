package de.malteee.citysystem.plots;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.core.Database;

import java.util.ArrayList;

public class Residential extends Plot implements Rentable {

    public Residential(String id) {
        super(id, null, null, null);

    }

    public Residential(String id, City city, ArrayList<Area> areas, String name) {
        super(id, city, areas, name);
        try {
            Database db = CitySystem.getDatabase();
            db.execute("INSERT INTO tbl_residential(RESIDENTIAL_ID, BUILDING_RIGHTS, RENTER, CITY_ID, NAME) VALUES('" + id + "', 'NONE', 'NONE', '" + city.getName() + "', '" + name + "')");
            for (Area area : areas) {
                db.execute("INSERT INTO tbl_residential_areas(RESIDENTIAL_ID, AREA_ID) VALUES('" + id + "', '" + area.getId() + "')");
                area.setPlot(this);
                area.setCity(city);
            }
        }catch (Exception exception) {
            exception.printStackTrace();
        }

        /*
        con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_residential(RESIDENTIAL_ID varchar(30), BUILDING_RIGHTS varchar(200), RENTER varchar(30), CITY_ID varchar(30), NAME varchar(30))").execute();
            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_residential_areas(RESIDENTIAL_ID varchar(30), AREA_ID varchar(30))").execute();
         */
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
