package de.malteee.citysystem.chat;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.money_system.Konto;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

import java.util.Random;

public class MessageBroadcaster {

    public MessageBroadcaster() {
        try {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(CitySystem.getPlugin(), () -> {
                for (CityPlayer player : CitySystem.getCityPlayers()) {
                    int i = new Random().nextInt(3);
                    switch (i) {
                        case 0 -> {
                            player.toPlayer().sendMessage("§6You've got " + CitySystem.getMm().getKonto(player).getMot() + "/" + Konto.MOT_MAX + " Shards today for playing!");
                        }
                        case 1 -> {
                            TextComponent message = new TextComponent( "§5§lCheck out our Discord server!" );
                            message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "https://discord.gg/pJSeMNhDgn" ) );
                            message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Click here to join the server!" ).create()));
                            player.toPlayer().spigot().sendMessage(message);
                            /*Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                                    "tellraw " + player.toPlayer().getName() +
                                            " {text:\"" + "Check out our discord server!" + "\",clickEvent:{action:open_url,value:\"" +
                                            "https://discord.gg/pJSeMNhDgn" + "\"}}");*/
                        }
                        case 2 -> {
                            double money = CitySystem.getJm().getMoneyToday(player.toPlayer());
                            if (player.hasJob())
                                player.toPlayer().sendMessage("§eYou earned §l" + CitySystem.df.format(money) + "§r§e Shard" + (money == 1 ? "":"s") + " today through your job!");
                            else
                                player.toPlayer().sendMessage("§eYou currently have no job! Just use §l/jobs §r§eto select one!");
                        }
                        default -> {
                            return;
                        }
                     }
                }
            }, 0, 20 * 60 * 10);
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
