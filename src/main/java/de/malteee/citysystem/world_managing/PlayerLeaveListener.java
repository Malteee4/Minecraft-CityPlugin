package de.malteee.citysystem.world_managing;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.commands_city.PlotCommand;
import de.malteee.citysystem.core.CityPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {


    @EventHandler
    public void handlePlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage("");
        CityPlayer cPlayer = CitySystem.getCityPlayer(event.getPlayer());
        PlotCommand.stop(event.getPlayer());
        CitySystem.removePlayer(cPlayer);
    }

}
