package de.malteee.citysystem.commands_general;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.money_system.Konto;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.ParseException;

public class MoneyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (CitySystem.isRegistered(player)) {
            CityPlayer cPlayer = CitySystem.getCityPlayer(player);
            if (args.length > 2) {
                if (args[0].equalsIgnoreCase("send")) {
                    String target = args[1];
                    if (CitySystem.getMm().getKonto(target) == null) {
                        player.sendMessage("§cThis player doesn't exist on the server!");
                        return false;
                    }
                    if (player.getName().equalsIgnoreCase(target)) {
                        player.sendMessage("§cYou can not send money to yourself!");
                        return false;
                    }
                    double money = 0;
                    double senderMoney = CitySystem.getMm().getKonto(cPlayer).getMoney();
                    try {
                        money = Double.parseDouble(args[2]);
                    }catch (NumberFormatException e) {
                        player.sendMessage("§cInvalid value!");
                        return false;
                    }if (money > senderMoney) {
                        player.sendMessage("§cYou don't have enough Shards!");
                        return false;
                    }
                    if (args.length > 3) {
                        if (args[3].equalsIgnoreCase("confirm")) {
                            Konto receiver = CitySystem.getMm().getKonto(target);
                            Konto senderKonto = CitySystem.getMm().getKonto(cPlayer);
                            receiver.addMoney(money);
                            senderKonto.removeMoney(money);
                            player.sendMessage("§aShards sent!");
                            //TODO receiver message
                            return false;
                        }
                    }else {
                        player.sendMessage("§aUse §l/money send " + target + " " + money + " confirm§r§a to confirm your transaction!");
                        return false;
                    }
                }else if(args[0].equalsIgnoreCase("give")) {
                    if(!player.hasPermission("CitySystem.giveMoney")) return false;
                    String target = args[1];
                    double money = 0;
                    try {
                        money = Double.parseDouble(args[2]);
                    }catch (NumberFormatException e) {
                        player.sendMessage("§cInvalid value!");
                        return false;
                    }
                    Konto receiver = CitySystem.getMm().getKonto(target);
                    receiver.addMoney(money);
                    player.sendMessage("§aShards sent!");
                }
            }
            player.sendMessage("§aYour current balance is: §l" + CitySystem.df.format(CitySystem.getMm().getKonto(cPlayer).getMoney()) + " Shards");    //Shards
        }else {
            //TODO: error message
        }
        return false;
    }
}
