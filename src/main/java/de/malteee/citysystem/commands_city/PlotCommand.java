package de.malteee.citysystem.commands_city;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.area.AreaChecker;
import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.plots.Plot;
import de.malteee.citysystem.plots.Residential;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlotCommand {

    private static HashMap<UUID, ArrayList<Location[]>> creatingPlot = new HashMap<>();
    private static HashMap<UUID, Integer[]> creatingPlotHeight = new HashMap<>();

    private ArrayList<Player> renting = new ArrayList<>();

    public static void register() {
        new CommandAPICommand("plot")
                .withSubcommand(new CommandAPICommand("info")
                        .executesPlayer(((player, args) -> {
                            CityPlayer cPlayer = CitySystem.getCityPlayer(player);
                            if (cPlayer == null) return;
                            if (cPlayer.getCurrentArea() == null) {
                                player.sendMessage("§cYou're not standing on a plot!");
                                return;
                            }
                            if (!cPlayer.getCurrentArea().getType().equals(Area.AreaType.PLOT)) {
                                player.sendMessage("§cYou're not standing on a plot!");
                                return;
                            }
                            Plot currentPlot = cPlayer.getCurrentArea().getPlot();
                            if (currentPlot instanceof Residential residentialPlot) {
                                City c = residentialPlot.getCity();
                                if (c.getOwner().equals(player.getUniqueId())) {
                                    player.sendMessage("§2" + residentialPlot.getName() + ":");
                                    player.sendMessage(" §7base area: §o" + residentialPlot.getSize() + " block" + (residentialPlot.getSize() == 1 ? "":"s"));
                                    player.sendMessage(" §7rentable: §o" + (residentialPlot.isRentable() ? "yes":"no"));
                                    player.sendMessage(" §7rent: §o" + (residentialPlot.isRentable() ? (residentialPlot.getRent() + " Shard" + (residentialPlot.getRent() == 1 ? "":"s")):"not rentable"));
                                    player.sendMessage(" §7renter: §o" + ((residentialPlot.getRenter() == null) ? "none":Bukkit.getOfflinePlayer(residentialPlot.getRenter()).getName()));
                                    player.sendMessage(" §7");
                                    player.sendMessage(" §7");
                                }else {
                                    player.sendMessage("§2Residential Plot - \n" + residentialPlot.getName() + ":");
                                    player.sendMessage(" §7base area: §o" + residentialPlot.getSize() + " block" + (residentialPlot.getSize() == 1 ? "":"s"));
                                    player.sendMessage(" §7rent: §o" + (residentialPlot.isRentable() ? (residentialPlot.getRent() + " Shard" + (residentialPlot.getRent() == 1 ? "":"s")):"not rentable"));
                                    player.sendMessage(" §7renter: §o" + ((residentialPlot.getRenter() == null) ? "none":Bukkit.getOfflinePlayer(residentialPlot.getRenter()).getName()));
                                }
                            }else {
                                player.sendMessage("§cNot a residential plot!");
                            }
                        })))
                .withSubcommand(new CommandAPICommand("create")
                        .withSubcommand(new CommandAPICommand("addArea")
                                .executesPlayer((player, args) -> {
                                    CityPlayer cPlayer = CitySystem.getCityPlayer(player);
                                    UUID uuid = player.getUniqueId();
                                    if (cPlayer == null) return;
                                    if (!cPlayer.isMarked(0)) {
                                        player.sendMessage("§cPlease select the §lfirst §r§cLocation using a golden hoe or /pos1!");
                                        return;
                                    }else if (!cPlayer.isMarked(1)) {
                                        player.sendMessage("§cPlease select the §lsecond §r§cLocation using a golden hoe or /pos2!");
                                        return;
                                    }
                                    Location loc1 = cPlayer.getMarkedLocations()[0];
                                    Location loc2 = cPlayer.getMarkedLocations()[1];
                                    if (AreaChecker.partOfArea(loc1, loc2, Area.AreaType.PLOT)) {
                                        player.sendMessage("§cYou marked an area, that already contains a plot!");
                                        return;
                                    }
                                    Area start = AreaChecker.getAreaByLocation(loc1);
                                    if (start == null) {
                                        player.sendMessage("§cYour marked area doesn't seem to be fully in a city!");
                                        return;
                                    }if (start.getType() != Area.AreaType.CITY) {
                                        player.sendMessage("§cYour marked area doesn't seem to be fully in a city!");
                                        return;
                                    }if (!start.getCity().getOwner().equals(uuid)) {
                                        player.sendMessage("§cYour marked area doesn't seem to be in a city you own!");
                                        return;
                                    }
                                    City city = start.getCity();
                                    /*if (city.getPlots().size() >= city.getStage().getResidential()) {
                                        player.sendMessage("§cYou can't create any more residential plots in this city!\nGet on a higher stage to unlock more plots!");
                                        return false;
                                    }*/
                                    if (!AreaChecker.partOfCity(loc1, loc2, city)) {
                                        player.sendMessage("§cYour marked area doesn't seem to be fully in your city!");
                                        return;
                                    }
                                    if (!creatingPlot.containsKey(uuid))
                                        start(player);
                                    ArrayList<Location[]> list = creatingPlot.get(uuid);
                                    for (Location[] locations : list) {
                                        if (AreaChecker.locationSpanPartOfAnother(loc1, loc2, locations[0], locations[1])) {
                                            player.sendMessage("§cYou marked an area that has already been added!");
                                            return;
                                        }
                                    }
                                    boolean adjacent = list.isEmpty();
                                    for (Location[] locations : list) {
                                        if (AreaChecker.isAdjacentToLocationArea(loc1, loc2, locations[0], locations[1]))
                                            adjacent = true;
                                    }
                                    if (!adjacent) {
                                        player.sendMessage("§cThe selected area is not adjacent to the already added areas!");
                                        return;
                                    }
                                    int height = Math.max(loc1.getBlockY(), loc2.getBlockY()) - Math.min(loc1.getBlockY(), loc2.getBlockY()) + 1;
                                    if (height < 3) {
                                        player.sendMessage("§cYour Plot has to be at least 3 blocks high!");
                                        return;
                                    }
                                    list.add((new Location[]{loc1, loc2}));
                                    creatingPlot.put(uuid, list);
                                    player.sendMessage("§aArea has been added!");
                                })
                        )
                        .withSubcommand(new CommandAPICommand("cancel")
                                .executesPlayer((player, args) -> {
                                    CityPlayer cPlayer = CitySystem.getCityPlayer(player);
                                    UUID uuid = player.getUniqueId();
                                    if (cPlayer == null) return;
                                    if (creatingPlot.containsKey(uuid)) {
                                        player.sendMessage("§aYour action has been canceled!");
                                        stop(player);
                                    }
                                })
                        )
                        .withSubcommand(new CommandAPICommand("start")
                                .executesPlayer((player, args) -> {
                                    CityPlayer cPlayer = CitySystem.getCityPlayer(player);
                                    UUID uuid = player.getUniqueId();
                                    if (cPlayer == null) return;
                                    if (creatingPlot.containsKey(uuid)) return;
                                    start(player);
                                    player.sendMessage("§aYou started the plot creation! Use §l/plot create addArea §r§ato add areas and §l/plot create setMaxHeight <height>" +
                                            " §r§aas well as §l/plot create setMinHeight <height> §r§ato set the plot height!\nUse §lconfirm §r§a to create the plot or §lcancel §r§ato stop!");
                                }))
                        .withSubcommand(new CommandAPICommand("confirm")
                                .executesPlayer((player, args) -> {
                                    CityPlayer cPlayer = CitySystem.getCityPlayer(player);
                                    UUID uuid = player.getUniqueId();
                                    if (cPlayer == null) return;
                                    if (!creatingPlot.containsKey(uuid)) return;
                                    if (creatingPlot.get(uuid).isEmpty()) {
                                        player.sendMessage("§cYou have to select at least one Area and add it using §l/plot create addarea§r§c!");
                                        return;
                                    }
                                    ArrayList<Location[]> list = creatingPlot.get(uuid);
                                    City city = AreaChecker.getAreaByLocation(list.getFirst()[0]).getCity();
                                    if (city == null) return;
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
                                    city.addResidentialPlot(new Residential("PLOT-" + areas.getFirst().getLocOne().toString(), city, areas,
                                            city.getName().toUpperCase() + "-PLOT" + (city.getPlots().size() + 1), 0, false, false, true));
                                    player.sendMessage("§aPlot has been created!");
                                    stop(player);
                                }))
                        .withSubcommand(new CommandAPICommand("setMinHeight")
                                .withArguments(new IntegerArgument("height", 0, 300))
                                .executesPlayer((player, args) -> {
                                    CityPlayer cPlayer = CitySystem.getCityPlayer(player);
                                    UUID uuid = player.getUniqueId();
                                    if (cPlayer == null) return;
                                    if (!creatingPlot.containsKey(uuid)) return;
                                    try {
                                        int min = (int) args.get("height");
                                        if (min < 0 || min > 300) {
                                            player.sendMessage("§cPlease enter a valid number between 0 and 300!");
                                            return;
                                        }
                                        if (!creatingPlotHeight.containsKey(uuid))
                                            creatingPlotHeight.put(uuid, new Integer[] {min, 300});
                                        else {
                                            int maxHeight = creatingPlotHeight.get(uuid)[1];
                                            if (min > maxHeight) {
                                                player.sendMessage("§cYour minimal height can't be bigger then your maximal building height(" + maxHeight + ")!");
                                                return;
                                            }if ((maxHeight - min) < 5) {
                                                player.sendMessage("§cYour plot has to be as least 5 blocks high(" + maxHeight + "-" + min + "=" + (maxHeight - min) + ")!");
                                                return;
                                            }
                                            creatingPlotHeight.put(uuid, new Integer[] {min, maxHeight});
                                        }
                                        player.sendMessage("§aThe minimal building height has been set to " + min + "!");
                                    }catch (Exception exception) {
                                        player.sendMessage("§cPlease enter a valid number between 0 and 300!");
                                    }
                                }))
                        .withSubcommand(new CommandAPICommand("setMaxHeight")
                                .withArguments(new IntegerArgument("height", 0, 300))
                                .executesPlayer((player, args) -> {
                                    CityPlayer cPlayer = CitySystem.getCityPlayer(player);
                                    UUID uuid = player.getUniqueId();
                                    if (cPlayer == null) return;
                                    if (!creatingPlot.containsKey(uuid)) return;
                                    try {
                                        int max = (int) args.get("height");
                                        if (max < 0 || max > 300) {
                                            player.sendMessage("§cPlease enter a valid number between 0 and 300!");
                                            return;
                                        }
                                        if (!creatingPlotHeight.containsKey(uuid))
                                            creatingPlotHeight.put(uuid, new Integer[] {10, max});
                                        else {
                                            int min = creatingPlotHeight.get(uuid)[0];
                                            if (max < min) {
                                                player.sendMessage("§cYour maximal building height can't be lower then your minimal height(" + min + ")!");
                                                return;
                                            }if ((max - min) < 5) {
                                                player.sendMessage("§cYour plot has to be as least 5 blocks high(" + max + "-" + min + "=" + (max - min) + ")!");
                                                return;
                                            }
                                            creatingPlotHeight.put(uuid, new Integer[]{min, max});
                                        }
                                        player.sendMessage("§aThe maximum building height has been set to " + max + "!");
                                    }catch (Exception exception) {
                                        player.sendMessage("§cPlease enter a valid number between 0 and 300!");
                                    }
                                }))
                )
                .withSubcommand(new CommandAPICommand("rent")
                        .executesPlayer((player, args) -> {
                            CityPlayer cPlayer = CitySystem.getCityPlayer(player);
                            if (cPlayer == null) return;
                            if (cPlayer.hasPlot()) {
                                player.sendMessage("§cYou already have a plot!");
                                return;
                            }
                        }))
                .withSubcommand(new CommandAPICommand("stopRent")
                        .executesPlayer(((player, args) -> {

                        })))
                .withSubcommand(new CommandAPICommand("rentable")
                        .withArguments(new BooleanArgument("rentable"))
                        .executesPlayer((player, args) -> {
                            CityPlayer cPlayer = CitySystem.getCityPlayer(player);
                            if (cPlayer == null) return;
                            if (args.get("rentable") == null) return;
                            boolean rentable = (boolean) args.get("rentable");
                            if (cPlayer.getCurrentArea() == null) {
                                player.sendMessage("§cYou're not standing on a plot!");
                                return;
                            }
                            if (!cPlayer.getCurrentArea().getType().equals(Area.AreaType.PLOT)) {
                                player.sendMessage("§cYou're not standing on a plot!");
                                return;
                            }
                            Plot currentPlot = cPlayer.getCurrentArea().getPlot();
                            if (currentPlot instanceof Residential residentialPlot) {
                                if (residentialPlot.getCity().getOwner().equals(cPlayer.toPlayer().getUniqueId())) {
                                    residentialPlot.setRentable(rentable);
                                    player.sendMessage("§aThe plot is now " + (rentable ? "":"not ") + "rentable!");
                                }else {
                                    player.sendMessage("§cYou're not the owner of this plot!");
                                }
                            }else {
                                player.sendMessage("§cNot a residential plot!");
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("edit"))
                .withSubcommand(new CommandAPICommand("setRent")
                        .withArguments(new DoubleArgument("rent"))
                        .executesPlayer((player, args) -> {
                            double rent = (double) args.get("rent");
                            if (rent < 0.5) {
                                player.sendMessage("§cThe rent can't be lower then 0.5 Shards!");
                                return;
                            }
                            CityPlayer cPlayer = CitySystem.getCityPlayer(player);
                            if (cPlayer == null) return;
                            if (cPlayer.getCurrentArea() == null) {
                                player.sendMessage("§cYou're not standing on a plot!");
                                return;
                            }
                            if (!cPlayer.getCurrentArea().getType().equals(Area.AreaType.PLOT)) {
                                player.sendMessage("§cYou're not standing on a plot!");
                                return;
                            }
                            Plot currentPlot = cPlayer.getCurrentArea().getPlot();
                            if (currentPlot instanceof Residential residentialPlot) {
                                if (residentialPlot.getCity().getOwner().equals(cPlayer.toPlayer().getUniqueId())) {
                                    residentialPlot.setRent(rent);
                                    player.sendMessage("§aThe rent of this plot has been set to §l" + rent + " Shard" + (rent == 1 ? "":"s")  + "§r§a!");
                                }else {
                                    player.sendMessage("§cYou're not the owner of this plot!");
                                }
                            }else {
                                player.sendMessage("§cNot a residential plot!");
                            }
                        }))
                .withSubcommand(new CommandAPICommand("setName")
                        .withArguments(new StringArgument("name"))
                        .executesPlayer((player, args) -> {

                        }))
                .register();
    }

/*
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
                            /*if (city.getPlots().size() >= city.getStage().getResidential()) {
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
                            city.addResidentialPlot(new Residential("PLOT-" + areas.getFirst().getLocOne().toString(), city, areas,
                                    city.getName().toUpperCase() + "-PLOT" + (city.getPlots().size() + 1), false, false, true));
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
                if (args.length < 2) {
                    player.sendMessage("§cPlease use /plot rentable <true/false>");
                    return false;
                }
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


                }else {
                    player.sendMessage("§cNot a residential plot!");
                }
            }
            case "edit" -> {
                //TODO: select plot
                // --> set height, add areas, (remove areas)
            }
        }
        return false;
    }*/

    public static void start(Player player) {
        if (creatingPlot.containsKey(player.getUniqueId())) return;
        creatingPlot.put(player.getUniqueId(), new ArrayList<>());
    }

    public static void stop(Player player) {
        creatingPlot.remove(player.getUniqueId());
        creatingPlotHeight.remove(player.getUniqueId());
    }

}
