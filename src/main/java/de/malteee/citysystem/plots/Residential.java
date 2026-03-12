package de.malteee.citysystem.plots;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.core.Database;

import java.util.ArrayList;
import java.util.UUID;

public class Residential extends Plot {

    private boolean shop;
    private double rent = 0;

    public Residential(String id, City city, ArrayList<Area> areas, String name, double rent, boolean rentable, boolean shop, boolean create) {
        super(id, city, areas, name, rentable);
        this.shop = shop;
        this.rent = rent;
        if (create) {
            try {
                Database db = CitySystem.getDatabase();
                db.execute("INSERT INTO tbl_residential(RESIDENTIAL_ID, BUILDING_RIGHTS, RENTER, CITY_ID, NAME, RENTABLE, SHOP, RENT) " +
                        "VALUES('" + id + "', 'NONE', 'NONE', '" + city.getName() + "', '" + name + "', FALSE, FALSE, 0)");
                for (Area area : areas) {
                    db.execute("INSERT INTO tbl_residential_areas(RESIDENTIAL_ID, AREA_ID) VALUES('" + id + "', '" + area.getId() + "')");
                    area.setPlot(this);
                    area.setCity(city);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }else {
            for (Area area : areas) {
                area.setPlot(this);
                area.setCity(city);
            }
        }
        /*
        con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_residential(RESIDENTIAL_ID varchar(30), BUILDING_RIGHTS varchar(200), RENTER varchar(30), CITY_ID varchar(30), NAME varchar(30))").execute();
            con.prepareStatement("CREATE TABLE IF NOT EXISTS tbl_residential_areas(RESIDENTIAL_ID varchar(30), AREA_ID varchar(30))").execute();
         */
    }

    public void setRentable(boolean val) {
        super.rentable = val;
        try {
            CitySystem.getDatabase().execute("UPDATE tbl_residential SET RENTABLE=" + (val ? "TRUE":"FALSE") + " WHERE RESIDENTIAL_ID='" + super.id + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startRenting(CityPlayer player) {
        this.owner = player.toPlayer().getUniqueId();
        try {
            CitySystem.getDatabase().execute("UPDATE tbl_residential SET RENTER='" + owner + "' WHERE RESIDENTIAL_ID='" + super.id + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRenter(UUID uuid) {
        this.owner = uuid;
    }

    public UUID getRenter() {
        return this.owner;
    }

    public void stopRenting() {

    }

    public void setRent(double i) {
        this.rent = i;
        try {
            CitySystem.getDatabase().execute("UPDATE tbl_residential SET RENT=" + i + " WHERE RESIDENTIAL_ID='" + super.id + "'");
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        try {
            CitySystem.getDatabase().execute("UPDATE tbl_residential SET NAME='" + name + "' WHERE RESIDENTIAL_ID='" + super.id + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getRent() {
        return this.rent;
    }

    public boolean hasShopLicense() {
        return shop;
    }

    public void setShopLicense(boolean shop) {
        this.shop = shop;
        try {
            CitySystem.getDatabase().execute("UPDATE tbl_residential SET SHOP=" + (shop ? "TRUE":"FALSE") + " WHERE RESIDENTIAL_ID='" + super.id + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
