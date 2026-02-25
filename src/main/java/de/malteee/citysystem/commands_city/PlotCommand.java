package de.malteee.citysystem.commands_city;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.area.AreaChecker;
import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.plots.Plot;
import de.malteee.citysystem.plots.Residential;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlotCommand implements CommandExecutor {

    private static HashMap<UUID, ArrayList<Location[]>> creatingPlot = new HashMap<>();
    private static HashMap<UUID, Integer[]> creatingPlotHeight = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (args.length == 0) return false;
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        UUID uuid = player.getUniqueId();
        if (cPlayer == null) return false;
        switch (args[0].toLowerCase()) {
            case "info" -> {
                if (cPlayer.getCurrentArea() == null) {
                    player.sendMessage("§cYou're not standing on a plot!");
                    return false;
                }
                if (!cPlayer.getCurrentArea().getType().equals(Area.AreaType.PLOT)) {
                    player.sendMessage("§cYou're not standing on a plot!");
                    return false;
                }
                Plot currentPlot = cPlayer.getCurrentArea().getPlot();
                if (currentPlot instanceof Residential residentialPlot) {
                    player.sendMessage("§oResidential Plot:\n" + residentialPlot.getName());
                }else {
                    player.sendMessage("§cNot a residential plot!");
                }
            }
            case "create" -> {
                if (args.length > 1) {
                    switch (args[1].toLowerCase()) {
                        case "addarea" -> {
                            if (!cPlayer.isMarked(0)) {
                                player.sendMessage("§cPlease select the §lfirst §r§cLocation using a golden hoe or /pos1!");
                                return false;
                            }else if (!cPlayer.isMarked(1)) {
                                player.sendMessage("§cPlease select the §lsecond §r§cLocation using a golden hoe or /pos2!");
                                return false;
                            }
                            Location loc1 = cPlayer.getMarkedLocations()[0];
                            Location loc2 = cPlayer.getMarkedLocations()[1];
                            if (AreaChecker.partOfArea(loc1, loc2, Area.AreaType.PLOT)) {
                                player.sendMessage("§cYou marked an area, that already contains a plot!");
                                return false;
                            }
                            Area start = AreaChecker.getAreaByLocation(loc1);
                            if (start == null) {
                                player.sendMessage("§cYour marked area doesn't seem to be fully in a city!");
                                return false;
                            }if (start.getType() != Area.AreaType.CITY) {
                                player.sendMessage("§cYour marked area doesn't seem to be fully in a city!");
                                return false;
                            }if (!start.getCity().getOwner().equals(uuid)) {
                                player.sendMessage("§cYour marked area doesn't seem to be in a city you own!");
                                return false;
                            }
                            City city = start.getCity();
                            if (city.getPlots().size() >= city.getStage().getResidential()) {
                                player.sendMessage("§cYou can't create any more residential plots in this city!\nGet on a higher stage to unlock more plots!");
                                return false;
                            }
                            if (!AreaChecker.partOfCity(loc1, loc2, city)) {
                                player.sendMessage("§cYour marked area doesn't seem to be fully in your city!");
                                return false;
                            }
                            if (!creatingPlot.containsKey(uuid))
                                start(player);
                            ArrayList<Location[]> list = creatingPlot.get(uuid);
                            for (Location[] locations : list) {
                                if (AreaChecker.locationSpanPartOfAnother(loc1, loc2, locations[0], locations[1])) {
                                    player.sendMessage("§cYou marked an area that has already been added!");
                                    return false;
                                }
                            }
                            boolean adjacent = list.isEmpty();
                            for (Location[] locations : list) {
                                if (AreaChecker.isAdjacentToLocationArea(loc1, loc2, locations[0], locations[1]))
                                    adjacent = true;
                            }
                            if (!adjacent) {
                                player.sendMessage("§cThe selected area is not adjacent to the already added areas!");
                                return false;
                            }
                            list.add((new Location[]{loc1, loc2}));
                            creatingPlot.put(uuid, list);
                            player.sendMessage("§aArea has been added!");
                        }
                        case "cancel" -> {
                            if (creatingPlot.containsKey(uuid)) {
                                player.sendMessage("§aYour action has been canceled!");
                                stop(player);
                            }
                        }
                        case "confirm" -> {
                            if (!creatingPlot.containsKey(uuid)) break;
                            if (creatingPlot.get(uuid).isEmpty()) {
                                player.sendMessage("§cYou have to select at least one Area and add it using §l/plot create addarea§r§c!");
                                break;
                            }
                            ArrayList<Location[]> list = creatingPlot.get(uuid);
                            City city = AreaChecker.getAreaByLocation(list.getFirst()[0]).getCity();
                            if (city == null) return false;
                            if (creatingPlotHeight.containsKey(uuid)) {
                                int min = creatingPlotHeight.get(uuid)[0];
                                int max = creatingPlotHeight.get(uuid)[1];
                                for (Location[] locations : list) {
                                    if (locations[0].getY() >= locations[1].getY()) {
                                        locations[0].setY(max);
                                        locations[1].setY(min);
                                    }else {
                                        locations[1].setY(max);
                                        locations[0].setY(min);
                                    }
                                }
                            }
                            ArrayList<Area> areas = new ArrayList<>();
                            for (Location[] locations : list)
                                areas.add(new Area(locations[0], locations[1], Area.AreaType.PLOT, AreaChecker.getSuperiorByLocation(locations[0]), true));
                            city.addResidentialPlot(new Residential("PLOT-" + areas.getFirst().getLocOne().toString(), city, areas, city.getName().toUpperCase() + "-PLOT" + (city.getPlots().size() + 1)));
                            player.sendMessage("§aPlot has been created!");
                            stop(player);
                        }
                        case "start" -> {
                            if (creatingPlot.containsKey(uuid)) break;
                            start(player);
                            player.sendMessage("§aYou started the plot creation! Use §l/plot create addarea §r§ato add areas and §l/plot create setmaxheight <height>" +
                                    " §r§aas well as §l/plot create setminheight <height> §r§ato set the plot height!\nUse §lconfirm §r§a to create the plot or §lcancel §r§ato stop!");
                        }
                        case "setminheight" -> {
                            if (!creatingPlot.containsKey(uuid)) break;
                            if (args.length < 3) return false;
                            try {
                                int min = Integer.parseInt(args[2]);
                                if (min < 0 || min > 300) {
                                    player.sendMessage("§cPlease enter a valid number between 0 and 300!");
                                    return false;
                                }
                                if (!creatingPlotHeight.containsKey(uuid))
                                    creatingPlotHeight.put(uuid, new Integer[] {min, 300});
                                else {
                                    int maxHeight = creatingPlotHeight.get(uuid)[1];
                                    if (min > maxHeight) {
                                        player.sendMessage("§cYour minimal height can't be bigger then your maximal building height(" + maxHeight + ")!");
                                        return false;
                                    }if ((maxHeight - min) < 5) {
                                        player.sendMessage("§cYour plot has to be as least 5 blocks high(" + maxHeight + "-" + min + "=" + (maxHeight - min) + ")!");
                                        return false;
                                    }
                                    creatingPlotHeight.put(uuid, new Integer[] {min, maxHeight});
                                }
                                player.sendMessage("§aThe minimal building height has been set to " + min + "!");
                            }catch (Exception exception) {
                                player.sendMessage("§cPlease enter a valid number between 0 and 300!");
                            }
                        }
                        case "setmaxheight" -> {
                            if (!creatingPlot.containsKey(uuid)) break;
                            if (args.length < 3) return false;
                            try {
                                int max = Integer.parseInt(args[2]);
                                if (max < 0 || max > 300) {
                                    player.sendMessage("§cPlease enter a valid number between 0 and 300!");
                                    return false;
                                }
                                if (!creatingPlotHeight.containsKey(uuid))
                                    creatingPlotHeight.put(uuid, new Integer[] {10, max});
                                else {
                                    int min = creatingPlotHeight.get(uuid)[0];
                                    if (max < min) {
                                        player.sendMessage("§cYour maximal building height can't be lower then your minimal height(" + min + ")!");
                                        return false;
                                    }if ((max - min) < 5) {
                                        player.sendMessage("§cYour plot has to be as least 5 blocks high(" + max + "-" + min + "=" + (max - min) + ")!");
                                        return false;
                                    }
                                    creatingPlotHeight.put(uuid, new Integer[]{min, max});
                                }
                                player.sendMessage("§aThe maximum building height has been set to " + max + "!");
                            }catch (Exception exception) {
                                player.sendMessage("§cPlease enter a valid number between 0 and 300!");
                            }
                        }
                    }
                }
            }
            case "rent" -> {

            }
            case "rentable" -> {

            }
            case "edit" -> {

            }
        }
        return false;
    }

    public void start(Player player) {
        if (creatingPlot.containsKey(player.getUniqueId())) return;
        creatingPlot.put(player.getUniqueId(), new ArrayList<>());
    }

    public static void stop(Player player) {
        creatingPlot.remove(player.getUniqueId());
        creatingPlotHeight.remove(player.getUniqueId());
    }

}
