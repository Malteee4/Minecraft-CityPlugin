package de.malteee.citysystem.core;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.money_system.Konto;
import de.malteee.citysystem.money_system.MoneyManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Timer implements CommandExecutor {

    public static final int MOT_MAX = 300;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (!(player.hasPermission("CitySystem.nextDay"))) return false;
        boolean reset = true;
        FileConfiguration config = CitySystem.getPlugin().getConfig();
        String day = LocalDate.now().getDayOfMonth() + "." + LocalDate.now().getMonth().getValue();
        if (!config.contains("last_day_saved")) config.set("last_day_saved", day);
        List<String> login = config.getStringList("login_today");
        for (String str : config.getStringList("active.list")) {
            if (!login.contains(str))
                config.set("active." + str, config.getInt("active." + str) > 1 ? ((int) (Math.sqrt(4 * config.getInt("active." + str)) - 1)) : (0));
        }
        config.set("login_today", new ArrayList<>());
        config.set("last_day_saved", day);
        for (String uuid : config.getStringList("job_cooldown.list"))
            config.set("job_cooldown." + uuid, Math.max(config.getInt("job_cooldown." + uuid) - 1, 0));
        CitySystem.getPlugin().saveConfig();

        MoneyManager mm = CitySystem.getMm();
        for (OfflinePlayer off : Bukkit.getOfflinePlayers()) {
            Konto konto = mm.getKonto(off.getUniqueId());
            if (konto == null) continue;
            if (konto.getMot() < MOT_MAX) {
                konto.addMot(konto.motPerMinute);
            }
            if (reset) {
                konto.clearMot();
                config.set("job." + off.getUniqueId(), 0);
                CitySystem.getPlugin().saveConfig();
            }
        }
        for (CityPlayer cPlayer : CitySystem.getCityPlayers()) {
            if (reset) {
                cPlayer.setBlocksWilderness(0);
                cPlayer.setJobCooldown(config);
                CitySystem.getJm().updatePlayers();
            }
        }
        return false;
    }

    public Timer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CitySystem.getPlugin(), () -> {
            boolean reset = false;
            FileConfiguration config = CitySystem.getPlugin().getConfig();
            String day = LocalDate.now().getDayOfMonth() + "." + LocalDate.now().getMonth().getValue();
            if (!config.contains("last_day_saved")) config.set("last_day_saved", day);
            if (!config.getString("last_day_saved").equalsIgnoreCase(day)) {
                List<String> login = config.getStringList("login_today");
                for (String str : config.getStringList("active.list")) {
                    if (!login.contains(str))
                        config.set("active." + str, config.getInt("active." + str) > 1 ? ((int) (Math.sqrt(5 * config.getInt("active." + str)) - 1)) : (0));
                }
                config.set("login_today", new ArrayList<>());
                config.set("last_day_saved", day);
                for (String uuid : config.getStringList("job_cooldown.list"))
                    config.set("job_cooldown." + uuid, Math.max(config.getInt("job_cooldown." + uuid) - 1, 0));
                CitySystem.getPlugin().saveConfig();
                reset = true;
            }
            MoneyManager mm = CitySystem.getMm();
            for (OfflinePlayer off : Bukkit.getOfflinePlayers()) {
                Konto konto = mm.getKonto(off.getUniqueId());
                if (konto == null) continue;
                if (konto.getMot() < MOT_MAX) {
                    konto.addMot(konto.motPerMinute);
                }
                if (reset) {
                    konto.clearMot();
                    config.set("job." + off.getUniqueId(), 0);
                    CitySystem.getPlugin().saveConfig();
                }
            }
            for (CityPlayer cPlayer : CitySystem.getCityPlayers()) {
                if (reset) {
                    cPlayer.setBlocksWilderness(0);
                    cPlayer.setJobCooldown(config);
                    CitySystem.getJm().updatePlayers();
                }
            }
        }, 0, 20 * 60);
    }
}
