package de.malteee.citysystem.commands_admin;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.utilities.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (!player.hasPermission("CitySystem.setRank")) return false;
        if (args.length < 2) return false;
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage("§cThere is no player with that name!");
            return false;
        }
        try {
            Rank rank = Rank.valueOf(args[1].toUpperCase());
            CityPlayer cityPlayer = CitySystem.getCityPlayer(target);
            if (cityPlayer == null)
                return false;
            cityPlayer.setRank(rank);
            player.sendMessage("§aRank has been set!");
        } catch (Exception e) {
            player.sendMessage("§cThis rank doesn't exist!");
        }

        return false;
    }
}
