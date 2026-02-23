package de.malteee.citysystem.commands_city;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.area.AreaChecker;
import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.core.Expansion;
import de.malteee.citysystem.money_system.Konto;
import de.malteee.citysystem.utilities.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class CityCommand implements CommandExecutor {

    private HashMap<UUID, Location[]> confirmation = new HashMap<>();
    private HashMap<UUID, City> confirmationCity = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (args.length == 0) return false;
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        if (cPlayer == null) return false;
        switch (args[0].toLowerCase()) {
            case "buy" -> {
                FileConfiguration config = CitySystem.getPlugin().getConfig();
                if (!config.contains("foundationStone." + player.getUniqueId().toString())) {
                    config.set("foundationStone." + player.getUniqueId().toString(), 0);
                    CitySystem.getPlugin().saveConfig();
                }
                int cities = config.getInt("foundationStone." + player.getUniqueId().toString());
                for (City c : CitySystem.getCm().getCities()) {
                    if (c.getOwner().equals(player.getUniqueId())) cities++;
                }
                Konto konto = CitySystem.getMm().getKonto(cPlayer);
                int costs = (int) ((cities > 0 ? 0:1000) + (cities > 0 ?(Math.pow(100, (cities + 1))):(0)));
                if (konto.getMoney() < costs) {
                    cPlayer.toPlayer().sendMessage("§cYou don't have enough money to buy a city! A city costs " + costs + " Shards!");
                    return false;
                }
                konto.removeMoney(costs);
                player.getInventory().addItem(new ItemBuilder(Material.BEDROCK, 1).setName("§6§lFoundation Stone").setLore("§7§oPlace in the Mainworld to found a city!").build());
                player.sendMessage("§aYou've successfully bought a city foundation stone! Place it on the ground to found a city!");
                config.set("foundationStone." + player.getUniqueId().toString(), (config.getInt("foundationStone." + player.getUniqueId().toString()) + 1));
                CitySystem.getPlugin().saveConfig();
            }
            case "info" -> {
                //TODO: gui with general information, button to change name, button to set spawn, button to plot overview
                if (cPlayer.getCurrentArea() == null) {
                    player.sendMessage("§cYou're currently not in a city!");
                    return false;
                }
                if (cPlayer.getCurrentArea().getType().equals(Area.AreaType.CITY)) {
                    City city = cPlayer.getCurrentArea().getCity();
                    player.sendMessage("§aYou're currently in §l" + city.getName() + "§r§a:\n" +
                            "  §7owner: §o" + Bukkit.getOfflinePlayer(city.getOwner()).getName() + "\n" +
                            "  §r§7stage: §o" + city.getStage().getDisplay() + "\n" +
                            "  §r§7residential plots: §o0/" + city.getStage().getResidential() + "\n" +
                            "  §r§7shop plots: §o---");
                }else {
                    player.sendMessage("§cYou're currently not in a city!");
                }
            }
            case "settings" -> {
                //TODO: gui for settings
            }
            case "expand" -> {
                if (args.length >= 2) {
                    if (args[1].equalsIgnoreCase("confirm")) {
                        if (confirmation.containsKey(player.getUniqueId())) {
                            Location loc1 = confirmation.get(player.getUniqueId())[0], loc2 = confirmation.get(player.getUniqueId())[1];
                            int costs = (Math.max(loc1.getBlockX(), loc2.getBlockX()) - Math.min(loc1.getBlockX(), loc2.getBlockX()) + 1)
                                    * (Math.max(loc1.getBlockZ(), loc2.getBlockZ()) - Math.min(loc1.getBlockZ(), loc2.getBlockZ()) + 1) * 20;
                            Konto konto = CitySystem.getMm().getKonto(cPlayer);
                            if (konto.getMoney() < costs) {
                                player.sendMessage("§cYou don't have enough money!");
                                return false;
                            }
                            konto.removeMoney(costs);
                            Area area = new Area(loc1, loc2, Area.AreaType.CITY, AreaChecker.getSuperiorByLocation(loc1), true);
                            City city = confirmationCity.get(player.getUniqueId());
                            city.addArea(area);
                            area.setCity(city);
                            player.sendMessage("§aYou've successfully expanded your city!");
                            return false;
                        }
                    }
                }
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
                if (AreaChecker.partOfArea(loc1, loc2, Area.AreaType.CITY)) {
                    player.sendMessage("§cYou marked an area, that is already claimed");
                    return false;
                }
                boolean adjacent = false;
                City cityToExpand = null;
                for (City c : city) {
                    for (Area area : c.getAreas()) {
                        if (area.getType().equals(Area.AreaType.PLOT)) continue;
                        if (AreaChecker.partOfXZ(area, loc1, loc2)) {
                            player.sendMessage("§cYou marked an area that you already own!");
                            return false;
                        }
                        if (AreaChecker.isAdjacent(area, loc1, loc2))
                            adjacent = true;
                    }if (adjacent) {
                        cityToExpand = c;
                        break;
                    }
                }
                if (!adjacent) {
                    player.sendMessage("§cYou're marked area is not adjacent to your city!");
                    return false;
                }
                int expansion = (Math.max(loc1.getBlockX(), loc2.getBlockX()) - Math.min(loc1.getBlockX(), loc2.getBlockX()) + 1)
                        * (Math.max(loc1.getBlockZ(), loc2.getBlockZ()) - Math.min(loc1.getBlockZ(), loc2.getBlockZ()) + 1);
                int space = 0;
                for (Area a : cityToExpand.getAreas())
                    space += a.getSurface();
                confirmation.put(player.getUniqueId(), new Location[] {loc1, loc2});
                confirmationCity.put(player.getUniqueId(), cityToExpand);
                player.sendMessage("§aUse §l/city expand confirm §r§ato expand your city for §l" + (20 * expansion) + " Shards!");
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
                if (args.length < 2)
                    return false;
                if (args[1].equalsIgnoreCase("set")) {
                    if (cPlayer.getCurrentArea() == null) {
                        player.sendMessage("§cYou're currently not in a city!");
                        return false;
                    }
                    if (cPlayer.getCurrentArea().getType().equals(Area.AreaType.CITY)) {
                        City city = cPlayer.getCurrentArea().getCity();
                        if (city.getOwner().equals(player.getUniqueId())) {
                            if (city.hasExpansion(Expansion.SPAWNPOINT)) {

                            }else {
                                player.sendMessage("§cYou need the spawnpoint expansion to set the city's spawn");
                            }
                        }else {
                            player.sendMessage("§cYou don't have the right to do that here!");
                        }
                    }else {
                        player.sendMessage("§cYou're currently not in a city!");
                    }
                }else if (args[1].equalsIgnoreCase("toggleAccess")) {

                }
            }
            case "tp" -> {
                if (args.length >= 2) {
                    String cityName = args[1];

                }
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
