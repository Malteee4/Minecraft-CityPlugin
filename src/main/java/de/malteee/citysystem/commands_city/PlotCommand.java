package de.malteee.citysystem.commands_city;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.area.AreaChecker;
import de.malteee.citysystem.core.CityPlayer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlotCommand implements CommandExecutor {

    private HashMap<UUID, ArrayList<Area>> creatingPlot = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (args.length == 0) return false;
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
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

                        }
                        case "confirm" -> {

                        }
                        case "start" -> {
                            start(player);
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
            case "toggleshop" -> {

            }
        }
        return false;
    }

    public void start(Player player) {
        if (creatingPlot.containsKey(player.getUniqueId())) return;
        creatingPlot.put(player.getUniqueId(), new ArrayList<>());
    }

}
