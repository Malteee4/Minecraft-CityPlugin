package de.malteee.citysystem.commands_general;

import com.destroystokyo.paper.profile.PlayerProfile;
import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.money_system.Konto;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import java.util.ArrayList;
import java.util.Arrays;

public class MoneyCommand {

    public static void register() {
        Argument<?> noSelectorSuggestions = new PlayerProfileArgument("target")
                .replaceSafeSuggestions(SafeSuggestions.suggest(info ->
                        Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getPlayerProfile).toArray(PlayerProfile[]::new)
                ));
        new CommandAPICommand("money")
                .withSubcommand(new CommandAPICommand("send")
                        .withArguments(noSelectorSuggestions, new DoubleArgument("money"))
                        .executesPlayer((player, args) -> {
                            CityPlayer cPlayer = CitySystem.getCityPlayer(player);
                            if (cPlayer == null) return;
                            String target = ((ArrayList<PlayerProfile>) args.get("target")).get(0).getName();
                            if (CitySystem.getMm().getKonto(target) == null) {
                                player.sendMessage("§cThis player doesn't exist on the server!");
                                return;
                            }
                            if (player.getName().equalsIgnoreCase(target)) {
                                player.sendMessage("§cYou can not send money to yourself!");
                                return;
                            }
                            double money = 0;
                            double senderMoney = CitySystem.getMm().getKonto(cPlayer).getMoney();
                            try {
                                money = (double) args.get("money");
                            }catch (NumberFormatException e) {
                                player.sendMessage("§cInvalid value!");
                                return;
                            }if (money > senderMoney) {
                                player.sendMessage("§cYou don't have enough Shards!");
                                return;
                            }
                            player.sendMessage("§aUse §l/money send " + target + " " + money + " confirm§r§a to confirm your transaction!");
                        }).withSubcommand(new CommandAPICommand("confirm")
                                .executesPlayer(((player, args) -> {
                                    CityPlayer cPlayer = CitySystem.getCityPlayer(player);
                                    if (cPlayer == null) return;
                                    String target = ((ArrayList<PlayerProfile>) args.get("target")).get(0).getName();
                                    double money = 0;
                                    double senderMoney = CitySystem.getMm().getKonto(cPlayer).getMoney();
                                    if (CitySystem.getMm().getKonto(target) == null) {
                                        player.sendMessage("§cThis player doesn't exist on the server!");
                                        return;
                                    }
                                    if (player.getName().equalsIgnoreCase(target)) {
                                        player.sendMessage("§cYou can not send money to yourself!");
                                        return;
                                    }
                                    try {
                                        money = (double) args.get("money");
                                    }catch (NumberFormatException e) {
                                        player.sendMessage("§cInvalid value!");
                                        return;
                                    }if (money > senderMoney) {
                                        player.sendMessage("§cYou don't have enough Shards!");
                                        return;
                                    }
                                    Konto receiver = CitySystem.getMm().getKonto(target);
                                    Konto senderKonto = CitySystem.getMm().getKonto(cPlayer);
                                    receiver.addMoney(money);
                                    senderKonto.removeMoney(money);
                                    player.sendMessage("§aShards sent!");
                                    //TODO receiver message
                                }))))
                .executesPlayer((player, args) -> {
                    CityPlayer cPlayer = CitySystem.getCityPlayer(player);
                    if (cPlayer == null) return;
                    player.sendMessage("§aYour current balance is: §l" + CitySystem.df.format(CitySystem.getMm().getKonto(cPlayer).getMoney()) + " Shards");
                }).register();
    }
    /*
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
    }*/
}
