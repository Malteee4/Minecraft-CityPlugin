package de.malteee.citysystem.commands_city;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.area.AreaChecker;
import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.money_system.Konto;
import de.malteee.citysystem.utilities.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CityCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (args.length == 0) return false;
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        if (cPlayer == null) return false;
        switch (args[0].toLowerCase()) {
            case "buy" -> {
                int cities = 0;
                for (City c : CitySystem.getCm().getCities()) {
                    if (c.getOwner().equals(player.getUniqueId())) cities++;
                }
                Konto konto = CitySystem.getMm().getKonto(cPlayer);
                int costs = (int) (1000 + (cities > 0 ?(Math.pow(100, (cities + 1))):(0)));
                if (konto.getMoney() < costs) {
                    cPlayer.toPlayer().sendMessage("§cYou don't have enough money to buy a city! A city costs " + costs + " Shards!");
                    return false;
                }
                konto.removeMoney(costs);
                player.getInventory().addItem(new ItemBuilder(Material.BEDROCK, 1).setName("§6§lFoundation Stone").setLore("§7§oPlace in the Mainworld to found a city!").build());
                player.sendMessage("§aYou've successfully bought a city foundation stone! Place it on the ground to found a city!");
            }
            case "info" -> {
                //TODO: gui with general information, button to change name, button to set spawn, button to plot overview
                if (cPlayer.getCurrentArea() == null) return false;
                if (cPlayer.getCurrentArea().getType().equals(Area.AreaType.CITY)) {
                    City city = cPlayer.getCurrentArea().getCity();
                    player.sendMessage("§aYou're currently in §l" + city.getName() + "§r§a:\n" +
                            "  §7owner: §o" + Bukkit.getOfflinePlayer(city.getOwner()).getName() + "\n" +
                            "  §r§7stage: §o" + city.getStage().getDisplay() + "\n" +
                            "  §r§7residential plots: §o0/2" + "\n" +
                            "  §r§7shop plots: §o---");
                }else {
                    player.sendMessage("§cYou're currently not in a city!");
                }
            }
            case "settings" -> {
                //TODO: gui for settings
            }
            case "expand" -> {
                ArrayList<City> city = new ArrayList<>();
                for (City c : CitySystem.getCm().getCities()) {
                    if (c.getOwner().equals(player.getUniqueId())) city.add(c);
                }if (city.isEmpty()) {
                    player.sendMessage("§cIt seems like you don't own a city!");
                    return false;
                }
                if (!cPlayer.isMarked(0)) {
                    player.sendMessage("§cYou didn't mark the first location!");
                    return false;
                }if (!cPlayer.isMarked(1)) {
                    player.sendMessage("§cYou didn't mark the second location!");
                    return false;
                }
                Location loc1 = cPlayer.getMarkedLocations()[0];
                Location loc2 = cPlayer.getMarkedLocations()[1];
                if (AreaChecker.partOfArea(loc1, loc2)) {
                    player.sendMessage("§cYou marked an area, that is already claimed");
                    return false;
                }
                boolean adjacent = false;
                for (City c : city) {
                    for (Area area : c.getAreas()) {
                        if (area.getType().equals(Area.AreaType.PLOT)) continue;
                        if (AreaChecker.partOfXZ(area, loc1, loc2)) {
                            player.sendMessage("§cYou marked an area that you already own!");
                            return false;
                        }
                        if (AreaChecker.isAdjacent(area, loc1, loc2))
                            adjacent = true;
                    }
                }
                if (!adjacent) {
                    player.sendMessage("§cYou're marked area is not adjacent to your city!");
                    return false;
                }

            }
            case "extensions" -> {

            }
            case "extension" -> {

            }
            case "list" -> {
                //TODO add pages
                for (City c : CitySystem.getCm().getCities())
                    player.sendMessage("§a- " + c.getName());
            }
            case "spawn" -> {

            }
            case "tp" -> {

            }
            case "markarea" -> {

            }
            case "delete" -> {
                if (!player.hasPermission("CitySystem.city.delete")) {
                    player.sendMessage("§cYou don't have the permission to use this command");
                    return false;
                }
                if (cPlayer.getCurrentArea().getType().equals(Area.AreaType.CITY)) {
                    City city = cPlayer.getCurrentArea().getCity();
                    if (city.delete())
                        player.sendMessage("§aCity has been deleted!");
                    else
                        player.sendMessage("§cSomething went wrong deleting the city!");
                    AreaChecker.initializeAreas();
                }else {
                    player.sendMessage("§c You're currently not in a city!");
                }

            }
        }
        return false;
    }
}
