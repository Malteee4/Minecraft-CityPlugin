package de.malteee.citysystem.plots;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.area.AreaChecker;
import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.CityPlayer;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class PlotManager {

    private HashSet<FarmingPlot> farmingPlots = new HashSet<>();
    private HashSet<Residential> residentialPlots = new HashSet<>();
    @Deprecated private HashSet<Shop> shopPlots = new HashSet<>();

    public PlotManager() {
        initializeFarmPlots();
        initializeResidentialPlots();
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
                City city = CitySystem.getCm().getCity(rs.getString("CITY_ID"));
                Residential r = new Residential(id, city, areas, rs.getString("NAME"), rs.getDouble("RENT"), rs.getBoolean("RENTABLE"), rs.getBoolean("SHOP"), false);
                residentialPlots.add(r);
                if (!rs.getString("RENTER").equals("NONE")) {
                    try {
                        CityPlayer cPlayer = CitySystem.getCityPlayer(UUID.fromString(rs.getString("RENTER")));
                        if (cPlayer != null)
                            cPlayer.setPlot(r);
                    } catch (Exception e) {
                        rs.close();
                    }
                }
            }rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initializeFarmPlots() {


    }

    public HashSet<FarmingPlot> getFarmingPlots() {
        return farmingPlots;
    }

    public HashSet<Residential> getResidentialPlots() {
        return residentialPlots;
    }

    @Deprecated public HashSet<Shop> getShopPlots() {
        return shopPlots;
    }
}
