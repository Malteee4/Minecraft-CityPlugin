package de.malteee.citysystem.plots;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.area.AreaChecker;
import de.malteee.citysystem.core.City;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;

public class PlotManager {

    private HashSet<FarmingPlot> farmingPlots = new HashSet<>();
    private HashSet<Residential> residentialPlots = new HashSet<>();
    private HashSet<Shop> shopPlots = new HashSet<>();

    public PlotManager() {
        initializeFarmPlots();
        initializeResidentialPlots();
        initializeShopPlots();
    }

    public void initializeResidentialPlots() {
        try {
            ResultSet rs = CitySystem.getDatabase().getResult("SELECT * FROM tbl_residential");
            while (rs.next()) {
                String id = rs.getString("RESIDENTIAL_ID");
                ResultSet rs1 = CitySystem.getDatabase().getResult("SELECT * FROM tbl_residential_areas WHERE RESIDENTIAL_ID='" + id + "'");
                ArrayList<Area> areas = new ArrayList<>();
                while (rs1.next())
                    areas.add(AreaChecker.getAreaByID(rs1.getString("AREA_ID")));
                rs1.close();
                if (areas.isEmpty()) continue;
                City city = areas.getFirst().getCity();
                residentialPlots.add(new Residential(id, city, areas, rs.getString("NAME"), rs.getBoolean("RENTABLE"), false));
            }rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initializeShopPlots() {



    }

    public void initializeFarmPlots() {


    }

    public HashSet<FarmingPlot> getFarmingPlots() {
        return farmingPlots;
    }

    public HashSet<Residential> getResidentialPlots() {
        return residentialPlots;
    }

    public HashSet<Shop> getShopPlots() {
        return shopPlots;
    }
}
