package de.malteee.citysystem.world_managing;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.commands_general.WorldSpawnCommand;
import de.malteee.citysystem.core.CityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerJoinListener implements Listener {

    private FileConfiguration config = CitySystem.getPlugin().getConfig();

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("");
        player.setPlayerListName("  §6§l" + player.getName() + "  ");
        if (player.getName().equals("JanikObenaus"))
            player.teleport(new Location(Bukkit.getWorld("janiksWorld"), -438, 70, 206));
        if (!CitySystem.isRegistered(player)) {
            CitySystem.registerPlayer(player);
            player.teleport(WorldSpawnCommand.worldSpawn.get(CitySystem.spawnWorld));
            if (player.getName().equals("JanikObenaus"))
                player.teleport(new Location(Bukkit.getWorld("janiksWorld"), -438, 70, 206));
            player.sendMessage("§aWelcome to Futuria!\nIf you want a tutorial, just use /tutorial.");
        }else
            CitySystem.loadPlayer(player);
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        if (!(config.contains("login_today"))) {
            ArrayList<String> list = new ArrayList<>(); list.add(player.getUniqueId().toString());
            config.set("login_today", list);
            config.set("active." + player.getUniqueId().toString(), 1);
            config.set("active.list", list);
        }else {
            List<String> list = config.getStringList("login_today");
            List<String> active_list = config.getStringList("active.list");
            if (!list.contains(player.getUniqueId().toString())) {
                list.add(player.getUniqueId().toString());
                config.set("login_today", list);
                config.set("active." + player.getUniqueId().toString(), (config.getInt("active." + player.getUniqueId().toString()) + 1));
                CitySystem.getMm().getKonto(cPlayer).addMoney((int) (10 * Math.sqrt(config.getInt("active." + player.getUniqueId().toString())) + 10));
            }if (!active_list.contains(player.getUniqueId().toString())) {
                active_list.add(player.getUniqueId().toString());
                config.set("active.list", active_list);
            }
        }CitySystem.getPlugin().saveConfig();
    }
}
