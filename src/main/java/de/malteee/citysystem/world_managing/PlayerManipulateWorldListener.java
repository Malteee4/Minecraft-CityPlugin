package de.malteee.citysystem.world_managing;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.area.Area;
import de.malteee.citysystem.area.AreaChecker;
import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.utilities.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;

import java.nio.Buffer;
import java.util.*;

public class PlayerManipulateWorldListener implements Listener {

    public static HashMap<UUID, Block> creatingCity = new HashMap<>();
    private static ArrayList<UUID> confirmation = new ArrayList<>();
    private final List<Material> mainNotDropable = Arrays.asList(Material.COAL_ORE, Material.COPPER_ORE, Material.COPPER_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.GOLD_ORE,
            Material.DEEPSLATE_COAL_ORE, Material.LAPIS_ORE, Material.DEEPSLATE_DIAMOND_ORE, Material.IRON_ORE, Material.DEEPSLATE_COPPER_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_REDSTONE_ORE, Material.REDSTONE_ORE);

    public boolean isAllowedToManipulate(Player player, Location location) {
        if (player.hasPermission("citysystem.unrestricted_building")) return true;
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        Area locArea = AreaChecker.getAreaByLocation(location);
        if (locArea == null) return true;
        if (player.getWorld().equals(CitySystem.spawnWorld) || locArea.getType().equals(Area.AreaType.SPAWN)) {
            return false;
        }if (locArea.getType().equals(Area.AreaType.CITY)) {
            return locArea.getCity().getBuilding_right().contains(player.getUniqueId());
        }else if (locArea.getType().equals(Area.AreaType.PLOT)) {
            return locArea.getPlot().getBuildingRights().contains(player.getUniqueId());
        }
        return true;
    }

    public boolean jobCheck(CityPlayer player, Material target) {
        if (player.getJob().getInvertedBlocks().contains(target))
            return true;
        return false;
    }

    //TODO: check fishing and mob killing

    @EventHandler
    public void handlePlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        if (cPlayer == null) return;
        if (player.hasPermission("citysystem.unrestricted_building")) return;
        event.setCancelled(!isAllowedToManipulate(player, event.getBlock().getLocation()));
        if (player.getWorld().equals(CitySystem.mainWorld)) {
            if (AreaChecker.getAreaByLocation(event.getBlock().getLocation()) == null) {
                if (cPlayer.getBlocksInWilderness() >= CityPlayer.BLOCKS_MAX) {
                    event.setCancelled(true);
                    player.sendMessage("§cYou can only break or place " + CityPlayer.BLOCKS_MAX + " blocks in the wilderness per day!");
                    return;
                }else {
                    cPlayer.setBlocksWilderness(cPlayer.getBlocksInWilderness() + 1);
                }
            }
            if (mainNotDropable.contains(event.getBlock().getType()))
                event.setDropItems(false);
        }
        event.setCancelled(jobCheck(cPlayer, event.getBlock().getType()));
    }

    @EventHandler
    public void handlePlayerPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        if (event.getBlock().getType().equals(Material.BEDROCK)) {
            event.setCancelled(true);
            if (!event.getBlock().getWorld().equals(CitySystem.mainWorld)) {
                player.sendMessage("§cYou can only found cities in the Mainworld!");
                return;
            }
            Location middle = event.getBlock().getLocation();
            Location corner1 = new Location(middle.getWorld(), middle.getBlockX() + 7, middle.getBlockY(), middle.getBlockZ() + 7);
            Location corner2 = new Location(middle.getWorld(), middle.getBlockX() - 7, middle.getBlockY(), middle.getBlockZ() - 7);
            for (int x = corner1.getBlockX(); x >= corner2.getBlockX(); x--) {
                for (int z = corner1.getBlockZ(); z >= corner2.getBlockZ(); z--) {
                    Location check = new Location(middle.getWorld(), x, 100, z);
                    if (AreaChecker.getAreaByLocation(check) != null) {
                        player.sendMessage("§cYour city is too close to an already claimed area!");
                        return;
                    }
                }
            }
            creatingCity.put(player.getUniqueId(), event.getBlock());
            player.sendMessage("§aConfirm your action by typing the name of your city into the chat! Type cancel to cancel!");
            event.setCancelled(false);
            return;
        }
        if (player.hasPermission("citysystem.unrestricted_building")) return;
        event.setCancelled(!isAllowedToManipulate(player, event.getBlock().getLocation()));
        if(cPlayer.toPlayer().getWorld().equals(CitySystem.mainWorld)) {
            if (AreaChecker.getAreaByLocation(event.getBlock().getLocation()) != null) return;
            if (cPlayer.getBlocksInWilderness() >= CityPlayer.BLOCKS_MAX) {
                event.setCancelled(true);
                player.sendMessage("§cYou can only break or place " + CityPlayer.BLOCKS_MAX + " blocks in the wilderness per day!");
                return;
            }else {
                cPlayer.setBlocksWilderness(cPlayer.getBlocksInWilderness() + 1);
            }
        }
    }

    @EventHandler
    public void handlePlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        CityPlayer cityPlayer = CitySystem.getCityPlayer(player);
        if (creatingCity.containsKey(player.getUniqueId()))
            event.setCancelled(true);
        Bukkit.getScheduler().runTask(CitySystem.getPlugin(), () -> {
            if (creatingCity.containsKey(player.getUniqueId())) {
                String name = event.getMessage();
                if (name.equalsIgnoreCase("cancel")) {
                    creatingCity.get(player.getUniqueId()).setType(Material.AIR);
                    creatingCity.remove(player.getUniqueId());
                    player.getInventory().addItem(new ItemBuilder(Material.BEDROCK, 1).setName("§6§lFoundation Stone").setLore("§7§oPlace in the Mainworld to found a city!").build());
                    player.sendMessage("§eYour action has been canceled!");
                    //TODO: full inventory
                    return;
                }
                if (name.length() > 20) {
                    player.sendMessage("§cYour city's name can't be longer than 20 letters!");
                    return;
                }if (CitySystem.getCm().getCityNames().contains(name.toLowerCase()) && !name.equalsIgnoreCase("confirm")) {
                    player.sendMessage("§cThis name has already been taken!");
                    return;
                }
                //TODO: check for unallowed names!
                if(event.getMessage().equalsIgnoreCase("confirm") && confirmation.contains(player.getUniqueId())) {
                    confirmation.remove(player.getUniqueId());
                    Location middle = creatingCity.get(player.getUniqueId()).getLocation();
                    Location corner1 = new Location(middle.getWorld(), middle.getBlockX() + 7, middle.getBlockY(), middle.getBlockZ() + 7);
                    Location corner2 = new Location(middle.getWorld(), middle.getBlockX() - 7, middle.getBlockY(), middle.getBlockZ() - 7);
                    Area area = new Area(corner1, corner2, Area.AreaType.CITY, cityPlayer.getSuperiorArea(), true);
                    CitySystem.getCm().addCity(new City(name, player, area, middle));
                    creatingCity.get(player.getUniqueId()).setType(Material.AIR);
                    creatingCity.remove(player.getUniqueId());
                    player.sendMessage("§aYour city has been founded!");
                }else {
                    player.sendMessage("§aType \"confirm\" into the chat to create your city §l" + name + "§r§a§o, cancel to cancel or a new name for your city!");
                    if (!confirmation.contains(player.getUniqueId())) {
                        confirmation.add(player.getUniqueId());
                    }
                }
            }
        });
    }

    @EventHandler
    public void handlePlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (creatingCity.containsKey(player.getUniqueId())) {
            creatingCity.get(player.getUniqueId()).setType(Material.AIR);
            creatingCity.remove(player.getUniqueId());
            player.getInventory().addItem(new ItemBuilder(Material.BEDROCK, 1).setName("§6§lFoundation Stone").setLore("§7§oPlace in the Mainworld to found a city!").build());
            player.sendMessage("§eYour action has been canceled!");
            //TODO: full inventory
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("citysystem.unrestricted_building")) return;
        event.setCancelled(!isAllowedToManipulate(player, event.getRightClicked().getLocation()));
    }

    @EventHandler
    public void onPlayerDestroyFarmland(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("citysystem.unrestricted_building")) return;
        /*if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.FARMLAND)
            event.setCancelled(true);*/
        if (event.getClickedBlock() == null) return;
        event.setCancelled(!isAllowedToManipulate(player, event.getClickedBlock().getLocation()));
    }

    @EventHandler
    public void onPlayerEnterPortal(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCreatePortal(PortalCreateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handlePlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World targetWorld = player.getWorld();
        World oldWorld = event.getFrom();
        if (oldWorld.equals(CitySystem.mainWorld)) {
            if (creatingCity.containsKey(player.getUniqueId())) {
                creatingCity.get(player.getUniqueId()).setType(Material.AIR);
                creatingCity.remove(player.getUniqueId());
                player.getInventory().addItem(new ItemBuilder(Material.BEDROCK, 1).setName("§6§lFoundation Stone").setLore("§7§oPlace in the Mainworld to found a city!").build());
                player.sendMessage("§eYour action has been canceled!");
                //TODO: full inventory
            }
        }
    }
}
