package de.malteee.citysystem.core;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.money_system.Konto;
import de.malteee.citysystem.money_system.MoneyManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Timer {

    public static final int MOT_MAX = 300;

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
                        config.set("active." + str, (int) (Math.sqrt(4 * config.getInt("active." + str)) - 1));
                }
                config.set("login_today", new ArrayList<>());
                config.set("last_day_saved", day);
                for (String uuid : config.getStringList("job_cooldown.list"))
                    config.set("job_cooldown." + uuid, Math.max(config.getInt("job_cooldown." + uuid) - 1, 0));
                CitySystem.getPlugin().saveConfig();
                reset = true;
            }
            MoneyManager mm = CitySystem.getMm();
            for (CityPlayer cPlayer : CitySystem.getCityPlayers()) {
                Konto konto = mm.getKonto(cPlayer);
                if (reset) {
                    cPlayer.setJobCooldown(config);
                    konto.clearMot();
                    cPlayer.setBlocksWilderness(0);
                }
                if (konto.getMot() < MOT_MAX) {
                    konto.addMot(konto.motPerMinute);
                }
            }
        }, 0, 20 * 60);
    }
}
