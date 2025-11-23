package de.malteee.citysystem.commands_general;

import de.malteee.citysystem.CitySystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LoginBonusCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        FileConfiguration config = CitySystem.getPlugin().getConfig();
        String uuid = player.getUniqueId().toString();
        if (!config.contains("active." + uuid))
            player.sendMessage("§6You seem to have §lno §r§6 login streak!");
        else
            player.sendMessage("§6You currently have a login streak of §l" + config.getInt("active." + uuid) + " day" + ((config.getInt("active." + uuid) > 1) ? "s":"") + "§r§6!");
        return false;
    }
}
