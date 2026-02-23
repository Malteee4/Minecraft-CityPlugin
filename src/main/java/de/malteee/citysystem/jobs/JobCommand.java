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
import org.bukkit.inventory.ItemStack;

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
        ItemStack active = new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE, 1).setName(" ").build();
        new InventoryBuilder(3*9, "Jobs", cPlayer.toPlayer())
                .setSlots(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE, 1).setName(" ").build(), 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26)
                .setSlot(10, new ItemBuilder(Material.GOLDEN_AXE,1 ).setName("§3Lumberjack")
                        .setLore((currentJob == Job.LUMBERJACK) ? "§7§oYou're currently a Lumberjack!" : "§7§oEarn Shards for cutting down trees\n and crafting with wood!")
                        .setEnchantmentGlintOverride(true, currentJob == Job.LUMBERJACK)
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build(), event -> {
                    event.setCancelled(true);
                    if (cooldown == 0) {
                        cPlayer.setJob(Job.LUMBERJACK);
                        openJobGui(cPlayer);
                    }else {
                        cPlayer.toPlayer().sendMessage("§cYou can change your job again in §l" + cooldown + " §r§cday" + ((cooldown > 1) ? "s":"") + "!");
                    }
                })
                .setSlot(1, active, currentJob == Job.LUMBERJACK)
                .setSlot(19, active, currentJob == Job.LUMBERJACK)
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
                .setSlot(2, active, currentJob == Job.MINER)
                .setSlot(20, active, currentJob == Job.MINER)
                .setSlot(12, new ItemBuilder(Material.GOLDEN_SWORD, 1).setName("§3Hunter")
                        .setLore((currentJob == Job.HUNTER) ? "§7§oYou're currently a Hunter!" : "§7§oEarn Shards for killing mobs!")
                        .setEnchantmentGlintOverride(true, currentJob == Job.HUNTER)
                        .addFlag(ItemFlag.HIDE_ENCHANTS)
                        .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .build(), event -> {
                    event.setCancelled(true);
                    if (cooldown == 0) {
                        cPlayer.setJob(Job.HUNTER);
                        openJobGui(cPlayer);
                    }else {
                        cPlayer.toPlayer().sendMessage("§cYou can change your job again in §l" + cooldown + " §r§cday" + ((cooldown > 1) ? "s":"") + "!");
                    }
                })
                .setSlot(3, active, currentJob == Job.HUNTER)
                .setSlot(21, active, currentJob == Job.HUNTER)
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
                .setSlot(4, active, currentJob == Job.FISHER)
                .setSlot(22, active, currentJob == Job.FISHER)
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
                .setSlot(5, active, currentJob == Job.TRADER)
                .setSlot(23, active, currentJob == Job.TRADER)
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
                .setSlot(6, active, currentJob == Job.BUILDER)
                .setSlot(24, active, currentJob == Job.BUILDER)
                .setSlot(16, new ItemBuilder(Material.RED_STAINED_GLASS, 1).setName("§3None")
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
                })
                .setSlot(7, active, currentJob == Job.NONE)
                .setSlot(25, active, currentJob == Job.NONE)
                .open();
    }
}
