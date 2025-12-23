package de.malteee.citysystem.commands_general;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        switch (args.length) {
            case 0 -> {
                switch (label.toLowerCase()) {
                    case "city" -> {
                        list.addAll(Arrays.asList("info", "buy", "settings", "tp"));
                    }
                    case "home" -> {
                        list.add("set");
                    }
                    case "money" -> {
                        list.add("send");
                    }
                }
            }
            case 1 -> {
                switch (label.toLowerCase()) {
                    case "city" -> {

                    }
                }
            }
            case 2 -> {
                switch (label.toLowerCase()) {
                    case "city" -> {

                    }
                }
            }
        }
        return list;
    }
}
