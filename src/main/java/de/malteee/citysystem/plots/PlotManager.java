package de.malteee.citysystem.plots;

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
