package de.malteee.citysystem.jobs;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.core.CityPlayer;
import de.malteee.citysystem.utilities.InventoryBuilder;
import de.malteee.citysystem.utilities.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public class JobCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        CityPlayer cPlayer = CitySystem.getCityPlayer(player);
        if (label.equalsIgnoreCase("jobs"))
            openJobGui(cPlayer);
        else {
            if (!cPlayer.hasJob()) {
                player.sendMessage("§cYou currently have no job!");
                return false;
            }
            Job job = cPlayer.getJob();
            JobManager jm = CitySystem.getJm();
            player.sendMessage("§aYou are currently a §l" + job.getDisplayName() + "§r§a:");


        }
        return false;
    }

    public void openJobGui(CityPlayer cPlayer) {
        int cooldown = cPlayer.getJobCooldown();
        Job currentJob = cPlayer.getJob();
        new InventoryBuilder(3*9, "Jobs", cPlayer.toPlayer())
                .setSlot(10, new ItemBuilder(Material.GOLDEN_AXE,1 ).setName("§3Lumberjack")
                        .setLore((currentJob == Job.LUMBERJACK) ? "§7§oYou're currently a Lumberjack!" : "§7§oEarn Shards for cutting down trees\n and crafting with wood!")
                        .setEnchantmentGlintOverride(true, currentJob == Job.LUMBERJACK)
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build(), event -> {
                    event.setCancelled(true);
                    if (cooldown == 0) {
                        cPlayer.setJob(Job.LUMBERJACK);
                        Bukkit.broadcastMessage("job changed");
                        openJobGui(cPlayer);
                    }else {
                        cPlayer.toPlayer().sendMessage("§cYou can change your job again in §l" + cooldown + " §r§cday" + ((cooldown > 1) ? "s":"") + "!");
                    }
                })
                .setSlot(11, new ItemBuilder(Material.GOLDEN_PICKAXE, 1).setName("§3Miner")
                        .setLore((currentJob == Job.MINER) ? "§7§oYou're currently a Miner!" : "§7§oEarn Shards for mining ore!")
                        .setEnchantmentGlintOverride(true, currentJob == Job.MINER)
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build(), event -> {
                    event.setCancelled(true);

                    if (cooldown == 0) {
                        cPlayer.setJob(Job.MINER);
                        openJobGui(cPlayer);
                    }else {
                        cPlayer.toPlayer().sendMessage("§cYou can change your job again in §l" + cooldown + " §r§cday" + ((cooldown > 1) ? "s":"") + "!");
                    }
                })
                .setSlot(12, new ItemBuilder(Material.GOLDEN_SWORD, 1).setName("§3Hunter")
                        .setLore((currentJob == Job.HUNTER) ? "§7§oYou're currently a Hunter!" : "§7§oEarn Shards for killing mobs!")
                        .setEnchantmentGlintOverride(true, currentJob == Job.HUNTER)
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build(), event -> {
                    event.setCancelled(true);
                    if (cooldown == 0) {
                        cPlayer.setJob(Job.HUNTER);
                    }else {
                        cPlayer.toPlayer().sendMessage("§cYou can change your job again in §l" + cooldown + " §r§cday" + ((cooldown > 1) ? "s":"") + "!");
                    }
                })
                .setSlot(13, new ItemBuilder(Material.FISHING_ROD, 1).setName("§3Fisher")
                        .setLore((currentJob == Job.FISHER) ? "§7§oYou're currently a Fisher!" : "§7§oEarn shards for fishing!")
                        .setEnchantmentGlintOverride(true, currentJob == Job.FISHER)
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build(), event -> {
                    event.setCancelled(true);
                    if (cooldown == 0) {
                        cPlayer.setJob(Job.FISHER);
                        openJobGui(cPlayer);
                    }else {
                        cPlayer.toPlayer().sendMessage("§cYou can change your job again in §l" + cooldown + " §r§cday" + ((cooldown > 1) ? "s":"") + "!");
                    }
                })
                .setSlot(14, new ItemBuilder(Material.EMERALD, 1).setName("§3Trader")
                        .setLore((currentJob == Job.TRADER) ? "§7§oYou're currently a Trader!" : "§7§oEarn shards for trading with villagers!")
                        .setEnchantmentGlintOverride(true, currentJob == Job.TRADER)
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build(), event -> {
                    event.setCancelled(true);
                    if (cooldown == 0) {
                        cPlayer.setJob(Job.TRADER);
                        openJobGui(cPlayer);
                    }else {
                        cPlayer.toPlayer().sendMessage("§cYou can change your job again in §l" + cooldown + " §r§cday" + ((cooldown > 1) ? "s":"") + "!");
                    }
                })
                .setSlot(15, new ItemBuilder(Material.WHITE_CONCRETE, 1).setName("§3Builder")
                        .setLore((currentJob == Job.BUILDER) ? "§7§oYou're currently a Builder!" : "§7§oEarn shards for building in the Mainworld!")
                        .setEnchantmentGlintOverride(true, currentJob == Job.BUILDER)
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build(), event -> {
                    event.setCancelled(true);
                    if (cooldown == 0) {
                        cPlayer.setJob(Job.BUILDER);
                        openJobGui(cPlayer);
                    }else {
                        cPlayer.toPlayer().sendMessage("§cYou can change your job again in §l" + cooldown + " §r§cday" + ((cooldown > 1) ? "s":"") + "!");
                    }
                })
                .setSlot(16, new ItemBuilder(Material.BARRIER, 1).setName("§3None")
                        .setLore((currentJob == Job.NONE) ? "§7§oYou currently have no job!" : "§7§oremoves your job")
                        .setEnchantmentGlintOverride(true, currentJob == Job.NONE)
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build(), event -> {
                    event.setCancelled(true);
                    if (cooldown == 0) {
                        cPlayer.setJob(Job.NONE);
                        openJobGui(cPlayer);
                    }else {
                        cPlayer.toPlayer().sendMessage("§cYou can change your job again in §l" + cooldown + " §r§cday" + ((cooldown > 1) ? "s":"") + "!");
                    }
                }).open();
    }
}
