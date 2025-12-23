package de.malteee.citysystem.area;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.core.City;
import de.malteee.citysystem.core.CityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.UUID;

public class AreaCreator implements Listener {

    ArrayList<UUID> cooldown = new ArrayList<>();

    @EventHandler
    public void handlePlayerUseGoldHoe(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_HOE)) return;
        if (cooldown.contains(player.getUniqueId())) return;
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);
            CityPlayer cPlayer = CitySystem.getCityPlayer(player);
            cPlayer.setMarked(event.getClickedBlock().getLocation(), event.getAction().equals(Action.LEFT_CLICK_BLOCK) ? 0:1);
            player.sendMessage("§ePosition " + (event.getAction().equals(Action.LEFT_CLICK_BLOCK) ? "1":"2") + " has been marked!");
            cooldown.add(player.getUniqueId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(CitySystem.getPlugin(), () -> {
                cooldown.remove(player.getUniqueId());
            }, 10);
         }
    }

}
