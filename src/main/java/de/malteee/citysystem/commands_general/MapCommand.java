package de.malteee.citysystem.commands_general;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MapCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        TextComponent message = new TextComponent( "§a§lClick here to open the map!" );
        message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "http://futuria-mc.com:8123/" ) );
        message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Click here to open the map!" ).create()));
        player.spigot().sendMessage(message);
        return false;
    }
}
