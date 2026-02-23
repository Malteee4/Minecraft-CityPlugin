package de.malteee.citysystem.commands_city;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.area.AreaChecker;
import de.malteee.citysystem.core.CityPlayer;
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

            }
            case "create" -> {
                if (args.length > 1) {
                    switch (args[1].toLowerCase()) {
                        case "addArea" -> {
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

                        }
                        case "cancel" -> {
                            player.sendMessage("§aYour action has been canceled!");
                            stop(player);
                        }
                        case "confirm" -> {
                            if (!creatingPlot.containsKey(uuid)) break;
                            if (creatingPlot.get(uuid).isEmpty()) {
                                player.sendMessage("§cYou have to select at least one Area and add it using §l/plot create addarea§r§c!");
                                break;
                            }
                            if (!creatingPlotHeight.containsKey(uuid)) {
                                creatingPlotHeight.put(uuid, new Integer[] {0, 300});
                            }

                            stop(player);
                        }
                        case "start" -> {
                            start(player);
                        }
                        case "setminheight" -> {
                            if (args.length < 3) return false;
                            try {
                                int i = Integer.parseInt(args[2]);
                                if (i < 0 || i > 200) {
                                    player.sendMessage("§cPlease enter a valid number between 10 and 200!");
                                    return false;
                                }
                                if (!creatingPlotHeight.containsKey(uuid)) {
                                    creatingPlotHeight.put(uuid, new Integer[] {i, 300});
                                }
                            }catch (Exception exception) {
                                player.sendMessage("§cPlease enter a valid number between 10 and 200!");
                            }
                        }
                        case "setmaxheight" -> {
                            if (args.length < 3) return false;

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
