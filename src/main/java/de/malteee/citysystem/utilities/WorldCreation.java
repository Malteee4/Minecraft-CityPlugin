package de.malteee.citysystem.utilities;

import de.malteee.citysystem.CitySystem;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class WorldCreation implements CommandExecutor {

    FileConfiguration config = CitySystem.getPlugin().getConfig();
    List<String> Maps = config.getStringList("worlds");


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("bedwars.createworld")) {
                if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("create")) {
                        String name = args[1];
                        World.Environment environment = World.Environment.NORMAL;
                        if (name.toLowerCase().contains("nether")) {
                            environment = World.Environment.NETHER;
                        }else if(name.toLowerCase().contains("end")) {
                            environment = World.Environment.THE_END;
                        }
                        WorldCreator w = (WorldCreator) new WorldCreator(name).type(WorldType.NORMAL).environment(environment);
                        p.sendMessage("§3Welt wird erstellt!...");
                        Bukkit.createWorld(w);
                        Bukkit.getWorlds().add(Bukkit.getWorld(name));
                        Maps.add(name);
                        config.set("worlds", Maps);
                        CitySystem.getPlugin().saveConfig();
                        p.sendMessage("§3Die Welt " + name + " wurde erstellt!");
                        p.teleport(Bukkit.getWorld(name).getSpawnLocation());

                    }else if(args[0].equalsIgnoreCase("tp")) {
                        Location loc = Bukkit.getWorld(args[1]).getSpawnLocation();
                        p.teleport(loc);
                    }else {
                        p.sendMessage("§cBitte benutze §9/world <tp/create> <name>§c!");
                    }
                }
            }
        }


        return false;
    }
}
