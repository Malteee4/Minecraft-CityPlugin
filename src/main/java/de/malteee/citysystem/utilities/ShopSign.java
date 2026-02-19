package de.malteee.citysystem.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.commands_admin.BreakShopCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ShopSign implements Listener{

    FileConfiguration config = CitySystem.getPlugin().getConfig();

    public static HashMap<Player, Boolean> item = new HashMap<Player, Boolean>();
    private static HashMap<Player, Integer> ids = new HashMap<>();
    private static HashMap<Player, Boolean> wait = new HashMap<>();
    public static HashMap<Player, ArmorStand> a = new HashMap<>();
    public static HashMap<Player, Boolean> neu = new HashMap<>();
    ArrayList<String> li = new ArrayList<String>();

    @EventHandler
    public void onSignPlace(SignChangeEvent e) {
        Player p = e.getPlayer();
        Location s = e.getBlock().getLocation();
        if(e.getLine(0).equalsIgnoreCase("shop")) {
            if(!wait.containsKey(p)) {
                wait.put(p, false);
            }
            if(wait.get(p) == false) {
                wait.put(p, true);
                try {
                    int i = Integer.parseInt(e.getLine(1));
                }catch(Exception ee) {
                    wait.put(p, false);p.sendMessage("§cShop konnte nicht erstellt werden \nGrund: ungültiger Preis!"); return;
                }
                ArrayList<Location> locs = new ArrayList<>();
                Location l1 = e.getBlock().getRelative(BlockFace.DOWN).getLocation(); locs.add(l1);
                Location l2 = e.getBlock().getRelative(BlockFace.EAST).getLocation(); locs.add(l2);
                Location l3 = e.getBlock().getRelative(BlockFace.NORTH).getLocation(); locs.add(l3);
                Location l4 = e.getBlock().getRelative(BlockFace.WEST).getLocation(); locs.add(l4);
                Location l5 = e.getBlock().getRelative(BlockFace.SOUTH).getLocation(); locs.add(l5);
                for(Location loc : locs) {
                    if(loc.getBlock().getType().equals(Material.CHEST) && !(config.contains(loc.getWorld().getName() + "" + loc.getBlockX() + "" + loc.getBlockY() + "" + loc.getBlockZ()))) {
                        e.setLine(0, "§l§f[Shop]");
                        e.setLine(3, e.getPlayer().getName());
                        String st = loc.getWorld().getName() + "" + loc.getBlockX() + "" + loc.getBlockY() + "" + loc.getBlockZ();
                        config.set(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ() + ".chest", st);
                        config.set(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ() + ".owner", p.getUniqueId().toString());
                        config.set(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ() + ".ownername", p.getName());
                        config.set(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ() + ".chestloc", loc);
                        List<String> shops = new ArrayList<String>();
                        if(config.contains("players." + p.getUniqueId().toString() + ".shops.shops")) {
                            shops = config.getStringList("players." + p.getUniqueId().toString() + ".shops.shops");
                        }shops.add(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ());
                        config.set("players." + p.getUniqueId().toString() + ".shops.shops", shops); CitySystem.getPlugin().saveConfig();
                        config.set(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ() + ".preis", e.getLine(1));
                        Chest chest = (Chest) loc.getBlock().getState();
                        chest.setCustomName(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ()); chest.update();
                        if(chest.getBlockData().getAsString().contains("right")) {
                            if(chest.getBlockData().getAsString().contains("east")) {
                                Location l = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() - 1);
                                if(l.getBlock().getType().equals(Material.CHEST)) {Chest ch = (Chest) l.getBlock().getState(); ch.setCustomName(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ()); ch.update();
                                    config.set(ch.getLocation().getWorld().getName() + "" + ch.getLocation().getBlockX() + "" + ch.getLocation().getBlockY() + "" + ch.getLocation().getBlockZ(), st); config.set(st + ".typ", "l");}
                            }else if(chest.getBlockData().getAsString().contains("north")) {
                                Location l = new Location(loc.getWorld(), loc.getX() - 1, loc.getY(), loc.getZ());
                                if(l.getBlock().getType().equals(Material.CHEST)) {Chest ch = (Chest) l.getBlock().getState(); ch.setCustomName(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ()); ch.update();
                                    config.set(ch.getLocation().getWorld().getName() + "" + ch.getLocation().getBlockX() + "" + ch.getLocation().getBlockY() + "" + ch.getLocation().getBlockZ(), st); config.set(st + ".typ", "l");}
                            }else if(chest.getBlockData().getAsString().contains("south")) {
                                Location l = new Location(loc.getWorld(), loc.getX() + 1, loc.getY(), loc.getZ());
                                if(l.getBlock().getType().equals(Material.CHEST)) {Chest ch = (Chest) l.getBlock().getState(); ch.setCustomName(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ()); ch.update();
                                    config.set(ch.getLocation().getWorld().getName() + "" + ch.getLocation().getBlockX() + "" + ch.getLocation().getBlockY() + "" + ch.getLocation().getBlockZ(), st); config.set(st + ".typ", "l");}
                            }else if(chest.getBlockData().getAsString().contains("west")) {
                                Location l = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() + 1);
                                if(l.getBlock().getType().equals(Material.CHEST)) {Chest ch = (Chest) l.getBlock().getState(); ch.setCustomName(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ()); ch.update();
                                    config.set(ch.getLocation().getWorld().getName() + "" + ch.getLocation().getBlockX() + "" + ch.getLocation().getBlockY() + "" + ch.getLocation().getBlockZ(), st); config.set(st + ".typ", "l");}
                            }
                        }else if(chest.getBlockData().getAsString().contains("left")) {
                            if(chest.getBlockData().getAsString().contains("east")) {
                                Location l = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() + 1);
                                if(l.getBlock().getType().equals(Material.CHEST)) {Chest ch = (Chest) l.getBlock().getState(); ch.setCustomName(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ()); ch.update();
                                    config.set(ch.getLocation().getWorld().getName() + "" + ch.getLocation().getBlockX() + "" + ch.getLocation().getBlockY() + "" + ch.getLocation().getBlockZ(), st); config.set(st + ".typ", "l");}
                            }else if(chest.getBlockData().getAsString().contains("north")) {
                                Location l = new Location(loc.getWorld(), loc.getX() + 1, loc.getY(), loc.getZ());
                                if(l.getBlock().getType().equals(Material.CHEST)) {Chest ch = (Chest) l.getBlock().getState(); ch.setCustomName(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ()); ch.update();
                                    config.set(ch.getLocation().getWorld().getName() + "" + ch.getLocation().getBlockX() + "" + ch.getLocation().getBlockY() + "" + ch.getLocation().getBlockZ(), st); config.set(st + ".typ", "l");}
                            }else if(chest.getBlockData().getAsString().contains("south")) {
                                Location l = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() - 1);
                                if(l.getBlock().getType().equals(Material.CHEST)) {Chest ch = (Chest) l.getBlock().getState(); ch.setCustomName(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ()); ch.update();
                                    config.set(ch.getLocation().getWorld().getName() + "" + ch.getLocation().getBlockX() + "" + ch.getLocation().getBlockY() + "" + ch.getLocation().getBlockZ(), st); config.set(st + ".typ", "l");}
                            }else if(chest.getBlockData().getAsString().contains("west")) {
                                Location l = new Location(loc.getWorld(), loc.getX() - 1, loc.getY(), loc.getZ());
                                if(l.getBlock().getType().equals(Material.CHEST)) {Chest ch = (Chest) l.getBlock().getState(); ch.setCustomName(s.getWorld().getName() + "" + s.getBlockX() + "" + s.getBlockY() + "" + s.getBlockZ()); ch.update();
                                    config.set(ch.getLocation().getWorld().getName() + "" + ch.getLocation().getBlockX() + "" + ch.getLocation().getBlockY() + "" + ch.getLocation().getBlockZ(), st); config.set(st + ".typ", "l");}
                            }
                        }
                        ArmorStand armor = (ArmorStand) loc.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getX() + 0.5, loc.getY() - 0.5, loc.getZ() + 0.5, p.getLocation().getYaw() - 180, 0), EntityType.ARMOR_STAND);
                        armor.setInvisible(true); armor.setGravity(false); armor.setCustomName("mmaalltteemm2"); armor.setCustomNameVisible(false); a.put(p, armor);
                        e.getPlayer().sendMessage("§lUm das Shopschild fertigzustellen linksklicke nun mit dem zu verkaufenden Item auf das Schild!");
                        CitySystem.getPlugin().saveConfig(); item.put(e.getPlayer(), true); Bukkit.getScheduler().scheduleSyncDelayedTask(CitySystem.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                wait.put(p, false);
                            }
                        },10);
                        ids.put(p, Bukkit.getScheduler().scheduleSyncDelayedTask(CitySystem.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                if(item.containsKey(p)) {
                                    item.remove(p); p.sendMessage("§cDu hast zu lange gebraucht! Der Vorgang wurde abgebrochen!");
                                }
                            }
                        }, 20*16));
                    }
                }
            }else {
                e.getPlayer().sendMessage("§cShopschild konnte nicht erstellt werden \nGrund: Preis(e) fehlen!");
            }

        }else {
        }

    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        Player p = e.getPlayer(); Material b = e.getClickedBlock().getType();
        if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if(item.containsKey(p) && config.contains(e.getClickedBlock().getLocation().getWorld().getName() + "" + e.getClickedBlock().getLocation().getBlockX() + "" + e.getClickedBlock().getLocation().getBlockY() + "" + e.getClickedBlock().getLocation().getBlockZ() + ".owner")) {
                if(item.get(p) && config.getString(e.getClickedBlock().getLocation().getWorld().getName() + "" + e.getClickedBlock().getLocation().getBlockX() + "" + e.getClickedBlock().getLocation().getBlockY() + "" + e.getClickedBlock().getLocation().getBlockZ() + ".owner").equalsIgnoreCase(p.getUniqueId().toString())
                        && (b.equals(Material.BIRCH_WALL_SIGN) || b.equals(Material.OAK_WALL_SIGN) || b.equals(Material.SPRUCE_WALL_SIGN) || b.equals(Material.ACACIA_WALL_SIGN) || b.equals(Material.DARK_OAK_WALL_SIGN)
                        || b.equals(Material.JUNGLE_WALL_SIGN) || b.equals(Material.CRIMSON_WALL_SIGN) || b.equals(Material.WARPED_WALL_SIGN) || b.equals(Material.CRIMSON_SIGN) || b.equals(Material.JUNGLE_SIGN)
                        || b.equals(Material.BIRCH_SIGN) || b.equals(Material.OAK_SIGN) || b.equals(Material.SPRUCE_SIGN) || b.equals(Material.ACACIA_SIGN) || b.equals(Material.DARK_OAK_SIGN) || b.equals(Material.WARPED_SIGN))) {
                    if(!p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                        if(!neu.containsKey(p)) {
                            Sign sign = (Sign) e.getClickedBlock().getState();
                            ItemStack hand = p.getInventory().getItemInMainHand();
                            String chest = config.getString(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ() + ".chest");
                            config.set(chest + ".item", hand);
                            config.set(chest + ".sonstiges.menge", hand.getAmount());
                            config.set(chest + ".sonstiges.type", hand.getType().toString());
                            //config.set(chest + ".item.meta.Damage", 0);
                            sign.setLine(0, sign.getLine(0) + "  §l[§0"+hand.getAmount() + "§f]");
                            sign.setLine(2, "§e" + hand.getType().toString());
                            sign.update();
                            p.sendMessage("§l§aShopschild erfolgreich fertiggestellt!");
                            a.get(p).getEquipment().setHelmet(hand); a.remove(p);
                            item.put(p, false); wait.put(p, false);
                            CitySystem.getPlugin().saveConfig(); CitySystem.getPlugin().reloadConfig();
                        }else {
                            Sign sign = (Sign) e.getClickedBlock().getState();
                            Location loc = config.getLocation(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ() + ".chestloc");
                            ItemStack hand = p.getInventory().getItemInMainHand();
                            String chest = config.getString(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ() + ".chest");
                            config.set(chest + ".item", hand);
                            config.set(chest + ".sonstiges.menge", hand.getAmount());
                            config.set(chest + ".sonstiges.type", hand.getType().toString());
                            sign.setLine(0, "§l§f[Shop]" + "  §l[§0"+hand.getAmount() + "§f]");
                            sign.setLine(2, "§e" + hand.getType().toString());
                            sign.update();
                            p.sendMessage("§l§aShopschild erfolgreich fertiggestellt!");
                            for(Entity entity : loc.getWorld().getEntities()) {
                                Location lE = entity.getLocation();
                                if(entity.getType().equals(EntityType.ARMOR_STAND) && lE.getX() == (loc.getX() + 0.5) && lE.getY() == (loc.getY() - 0.5) && lE.getZ() == (loc.getZ() + 0.5)) {
                                    ArmorStand armor = (ArmorStand) entity; armor.getEquipment().setHelmet(hand);
                                }
                            }
                            CitySystem.getPlugin().saveConfig(); CitySystem.getPlugin().reloadConfig();
                            neu.remove(p); item.put(p, false);
                        }
                    }else {
                        p.sendMessage("§cDu kannst nicht nichts verkaufen!");
                    }
                    Bukkit.getScheduler().cancelTask(ids.get(p));
                }
            }
        }else if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if((b.equals(Material.BIRCH_WALL_SIGN) || b.equals(Material.OAK_WALL_SIGN) || b.equals(Material.SPRUCE_WALL_SIGN) || b.equals(Material.ACACIA_WALL_SIGN) || b.equals(Material.DARK_OAK_WALL_SIGN)
                    || b.equals(Material.JUNGLE_WALL_SIGN) || b.equals(Material.CRIMSON_WALL_SIGN) || b.equals(Material.WARPED_WALL_SIGN) || b.equals(Material.CRIMSON_SIGN) || b.equals(Material.JUNGLE_SIGN)
                    || b.equals(Material.BIRCH_SIGN) || b.equals(Material.OAK_SIGN) || b.equals(Material.SPRUCE_SIGN) || b.equals(Material.ACACIA_SIGN) || b.equals(Material.DARK_OAK_SIGN) || b.equals(Material.WARPED_SIGN))) {
                Sign sign = (Sign) e.getClickedBlock().getState();
                if(config.contains(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ())) {
                    String chest = config.getString(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ() + ".chest");
                    ItemStack item = new ItemStack(Material.valueOf(config.getString(chest + ".sonstiges.type")), config.getInt(chest + ".sonstiges.menge"));
                    double preis = Double.parseDouble(sign.getLine(1));
                    boolean pInvFrei = false;
                    if(p.getInventory().firstEmpty() == -1) {
                        for(ItemStack i : p.getInventory().getContents()) {
                            if(i.getType() == item.getType() && i.getAmount() + item.getAmount() <= 64) {
                                pInvFrei = true;
                            }
                        }
                    }else {
                        pInvFrei = true;
                    }if(pInvFrei) {
                        Player verkaeufer = null; String ver = ""; UUID name = null; UUID offline = null;
                        for(Player pl : Bukkit.getOnlinePlayers()) {
                            if(pl.getName().equals(sign.getLine(3))) {
                                verkaeufer = Bukkit.getPlayer(sign.getLine(3)); ver = verkaeufer.getUniqueId().toString(); name = verkaeufer.getUniqueId();
                            }
                        }
                        Location c = config.getLocation(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ() + ".chestloc");
                        Chest ch = (Chest) c.getBlock().getState();
                        if(verkaeufer != null) {
                            if(verkaeufer.equals(p)) {
                                p.sendMessage("§cDu kannst nichts von deinen eigenen Shops kaufen!");return;
                            }
                        }else {
                            for(OfflinePlayer off : Bukkit.getOfflinePlayers()) {
                                if(off.getName().equals(sign.getLine(3))) {
                                    ver = off.getUniqueId().toString(); name = off.getUniqueId(); offline = off.getUniqueId();
                                }
                            }
                        }
                        if(config.contains(chest + ".typ")) {
                            boolean leer = true; Chest ches = null; ItemStack get = null;
                            ArrayList<Location> ls = new ArrayList<>();
                            Location l = new Location(c.getWorld(), c.getX(), c.getY(), c.getZ() - 1); Location l1 = new Location(c.getWorld(), c.getX() + 1, c.getY(), c.getZ());
                            Location l2 = new Location(c.getWorld(), c.getX(), c.getY(), c.getZ() + 1); Location l3 = new Location(c.getWorld(), c.getX() - 1, c.getY(), c.getZ()); ls.add(l3); ls.add(l2); ls.add(l1); ls.add(l);
                            for(Location loc : ls) {
                                if(config.contains(loc.getWorld().getName() + "" + loc.getBlockX() + "" + loc.getBlockY() + "" + loc.getBlockZ())) {
                                    if(config.getString(loc.getWorld().getName() + "" + loc.getBlockX() + "" + loc.getBlockY() + "" + loc.getBlockZ()).equals(c.getWorld().getName() + "" + c.getBlockX() + "" + c.getBlockY() + "" + c.getBlockZ())) {
                                        ches = (Chest) loc.getBlock().getState();
                                    }
                                }
                            }int vorat = 0; ls.removeAll(ls);
                            for(ItemStack it : ch.getBlockInventory().getContents()) {
                                if(it != null) {
                                    if(it.getType().equals(item.getType())) {
                                        get = it; vorat += it.getAmount();
                                    }
                                }
                            }if(ches != null) {for(ItemStack it : ches.getBlockInventory().getContents()) {
                                if(it != null) {
                                    if(it.getType().equals(item.getType())) {
                                        vorat += it.getAmount();
                                    }
                                }
                            }}
                            if(vorat >= item.getAmount()) {
                                leer = false;
                            }
                            if(!leer) {
                                double buyermoney = 0;//CitySystem.getCityPlayer(p).getKonto().getMoney();
                                get.setAmount(1); ItemStack rem = new ItemStack(item.getType(), 1);
                                if(buyermoney >= preis) {
                                    boolean bo = false;
                                    for(ItemStack s : ches.getInventory().getContents()) {
                                        if(s != null) {
                                            if(s.isSimilar(get)) {
                                                ches.getInventory().removeItem(s);
                                            }
                                        }
                                    }
                                    vorat = vorat - item.getAmount();
                                    if(vorat <= 1728) {
                                        for(int i = 0; i < vorat; i++) {
                                            ches.getInventory().addItem(get);
                                        }
                                    }else {
                                        for(int i = 0; i < 1728; i++) {
                                            ches.getInventory().addItem(get);
                                        }
                                        for(int i = 0; i < vorat - 1728;i++) {
                                            ches.getInventory().addItem(get);
                                        }
                                    }
                                    get.setAmount(item.getAmount());
                                    p.getInventory().addItem(get);
                                    //TODO
                                    //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "money take " + p.getName() + " " + preis);
                                    //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "money give " + name + " " + preis);
                                    if(!config.contains("players." + ver + ".shops.einnahmen")) {
                                        config.set("players." + ver + ".shops.einnahmen", 0);
                                    }
                                    config.set("players." + ver + ".shops.einnahmen", config.getDouble("players." + ver + ".shops.einnahmen") + preis);
                                    if(verkaeufer != null) {
                                        if(config.contains("players." + verkaeufer.getUniqueId().toString() + ".shops.benachrichtigungen.kaufbenachrichtigung")) {
                                            if(config.getBoolean("players." + verkaeufer.getUniqueId().toString() + ".shops.benachrichtigungen.kaufbenachrichtigung")) {
                                                verkaeufer.sendMessage("§2Der Spieler §6" + p.getName() + " §2 hat " + item.getAmount() + " mal §e" + item.getType().toString().toLowerCase() + " §2für §e" + preis + "$ §2gekauft!");
                                            }
                                        }else {
                                            config.set("players." + verkaeufer.getUniqueId().toString() + ".shops.benachrichtigungen.kaufbenachrichtigung", false);
                                        }
                                        if(vorat - item.getAmount() < item.getAmount()) {
                                            verkaeufer.sendMessage("§cDein Shop welcher " + item.getAmount() + " mal §6" + item.getType().toString().toLowerCase() + " §cverkauft ist nun leer!");
                                        }
                                    }
                                    CitySystem.getPlugin().saveConfig();
                                }else {
                                    p.sendMessage("§cDu hast nicht genügend Geld!");
                                }
                            }else {
                                p.sendMessage("§cDer Shop ist leer!");
                            }
                        }else {
                            boolean leer = true; ItemStack get = null;
                            int vorat = 0;
                            for(ItemStack it : ch.getBlockInventory().getContents()) {
                                if(it != null) {
                                    if(it.getType().equals(item.getType())) {
                                        get = it; vorat += it.getAmount();
                                    }
                                }
                            }
                            if(vorat >= item.getAmount()) {
                                leer = false;
                            }get.setAmount(1);
                            if(!leer) {
                                double buyermoney = 0;//CitySystem.getCityPlayer(p).getKonto().getMoney();
                                if(buyermoney >= preis) {
                                    for(ItemStack s : ch.getInventory().getContents()) {
                                        if(s != null) {
                                            if(s.isSimilar(get)) {
                                                ch.getInventory().removeItem(s);
                                            }
                                        }
                                    }
                                    vorat = vorat - item.getAmount();
                                    if(vorat <= 1728) {
                                        for(int i = 0; i < vorat; i++) {
                                            ch.getInventory().addItem(get);
                                        }
                                    }
                                    get.setAmount(item.getAmount());
                                    p.getInventory().addItem(get);
                                    if(verkaeufer != null) {
                                        if(config.contains("players." + verkaeufer.getUniqueId().toString() + ".shops.benachrichtigungen.kaufbenachrichtigung")) {
                                            if(config.getBoolean("players." + verkaeufer.getUniqueId().toString() + ".shops.benachrichtigungen.kaufbenachrichtigung")) {
                                                verkaeufer.sendMessage("§2Der Spieler §6" + p.getName() + " §2 hat " + item.getAmount() + " mal §e" + item.getType().toString().toLowerCase() + " §2für §e" + preis + "$ §2gekauft!");
                                            }
                                        }else {
                                            config.set("players." + verkaeufer.getUniqueId().toString() + ".shops.benachrichtigungen.kaufbenachrichtigung", false);
                                        }
                                        if(vorat - item.getAmount() < item.getAmount()) {
                                            verkaeufer.sendMessage("§cDein Shop welcher " + item.getAmount() + " mal §6" + item.getType().toString().toLowerCase() + " §cverkauft ist nun leer!");
                                        }
                                    }
                                    //TODO
                                    //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "money take " + p.getName() + " " + preis);
                                    //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "money give " + name + " " + preis);
                                    if(!config.contains("players." + ver + ".shops.einnahmen")) {
                                        config.set("players." + ver + ".shops.einnahmen", preis);
                                    }
                                    config.set("players." + ver + ".shops.einnahmen", config.getDouble("players." + ver + ".shops.einnahmen") + preis);
                                    CitySystem.getPlugin().saveConfig();
                                }else {
                                    p.sendMessage("§cDu hast nicht genügend Geld!");
                                }
                            }else {
                                p.sendMessage("§cDer Shop ist leer!");
                            }
                        }
                    }else {
                        p.sendMessage("§cDein Inventar ist voll!");
                    }
                }

            }
        }
    }
    @EventHandler
    public void handlePlayerHitArmorstand(PlayerArmorStandManipulateEvent e) {
        if(e.getRightClicked().getCustomName().equals("mmaalltteemm2")) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void handlePlayerDamageArmorstand(EntityDamageEvent e) {
        if(e.getEntity() instanceof ArmorStand) {
            if(e.getEntity().getCustomName().equals("mmaalltteemm2")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChestOpening(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            if(block.getType().equals(Material.CHEST)) {
                Chest chest = (Chest) block.getState();
                if (chest.getCustomName() == null) return;
                if(config.contains(chest.getCustomName())) {
                    //p.sendMessage(config.getString(chest.getCustomName() + ".ownername"));
                    if(config.contains(chest.getCustomName() + ".owner")) {
                        if(p.hasPermission("Shopplugin.lookinchops")) {
                            return;
                        }
                        if(!(config.getString(chest.getCustomName() + ".owner").equalsIgnoreCase(p.getUniqueId().toString()))) {
                            p.sendMessage("§cDas ist nicht dein Shop!"); e.setCancelled(true);
                        }
                    }
                }

            }
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Material b = e.getBlock().getType();
        if((b.equals(Material.BIRCH_WALL_SIGN) || b.equals(Material.OAK_WALL_SIGN) || b.equals(Material.SPRUCE_WALL_SIGN) || b.equals(Material.ACACIA_WALL_SIGN) || b.equals(Material.DARK_OAK_WALL_SIGN)
                || b.equals(Material.JUNGLE_WALL_SIGN) || b.equals(Material.CRIMSON_WALL_SIGN) || b.equals(Material.WARPED_WALL_SIGN) || b.equals(Material.CRIMSON_SIGN) || b.equals(Material.JUNGLE_SIGN)
                || b.equals(Material.BIRCH_SIGN) || b.equals(Material.OAK_SIGN) || b.equals(Material.SPRUCE_SIGN) || b.equals(Material.ACACIA_SIGN) || b.equals(Material.DARK_OAK_SIGN) || b.equals(Material.WARPED_SIGN))) {
            Sign sign = (Sign) e.getBlock().getState();
            if((!(sign.getLine(3).equals(e.getPlayer().getName())) && !BreakShopCommand.canBreakShop(e.getPlayer())) && config.contains(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ())) {
                e.setCancelled(true);
            }else if(config.contains(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ())) {
                String name = sign.getLine(3); Player p = null; OfflinePlayer o = null;
                for(Player pl : Bukkit.getOnlinePlayers()) {
                    if(pl.getName().equals(name)) {
                        p = pl;
                    }
                }if(p == null) {
                    for(OfflinePlayer pl : Bukkit.getOfflinePlayers()) {
                        if(pl.getName().equals(name)) {
                            o = pl;
                        }
                    }
                }
                Location loc = config.getLocation(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ() + ".chestloc");
                ArrayList<Location> ls = new ArrayList<>();
                Location l = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() - 1); Location l1 = new Location(loc.getWorld(), loc.getX() + 1, loc.getY(), loc.getZ());
                Location l2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() + 1); Location l3 = new Location(loc.getWorld(), loc.getX() - 1, loc.getY(), loc.getZ()); ls.add(l3); ls.add(l2); ls.add(l1); ls.add(l);
                for(Location chloc : ls) {
                    if(config.contains(chloc.getWorld().getName() + "" + chloc.getBlockX() + "" + chloc.getBlockY() + "" + chloc.getBlockZ())) {
                        if(config.getString(chloc.getWorld().getName() + "" + chloc.getBlockX() + "" + chloc.getBlockY() + "" + chloc.getBlockZ()).equals(loc.getWorld().getName() + "" + loc.getBlockX() + "" + loc.getBlockY() + "" + loc.getBlockZ())) {
                            config.set(chloc.getWorld().getName() + "" + chloc.getBlockX() + "" + chloc.getBlockY() + "" + chloc.getBlockZ(), null); CitySystem.getPlugin().saveConfig();
                        }
                    }
                }
                if(p != null) {
                    config.set(config.getString(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ() + ".chest"), null);
                    config.set(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ(), null);
                    if(config.contains("players." + p.getUniqueId().toString() + ".shops.shops")) {
                        List<String> sh = config.getStringList("players." + p.getUniqueId().toString() + ".shops.shops");
                        sh.remove(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ());
                        config.set("players." + p.getUniqueId().toString() + ".shops.shops", sh);
                    }
                }else if(o != null) {
                    config.set(config.getString(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ() + ".chest"), null);
                    config.set(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ(), null);
                    if(config.contains("players." + o.getUniqueId().toString() + ".shops.shops")) {
                        List<String> sh = config.getStringList("players." + o.getUniqueId().toString() + ".shops.shops");
                        sh.remove(sign.getLocation().getWorld().getName() + "" + sign.getLocation().getBlockX() + "" + sign.getLocation().getBlockY() + "" + sign.getLocation().getBlockZ());
                        config.set("players." + o.getUniqueId().toString() + ".shops.shops", sh);
                    }
                }else {
                    e.setCancelled(true);
                }
                CitySystem.getPlugin().saveConfig();
                for(Entity entity : loc.getWorld().getEntities()) {
                    Location lE = entity.getLocation();
                    if(entity.getType().equals(EntityType.ARMOR_STAND) && lE.getX() == (loc.getX() + 0.5) && lE.getY() == (loc.getY() - 0.5) && lE.getZ() == (loc.getZ() + 0.5)) {
                        entity.remove();
                    }
                }
                if(BreakShopCommand.canBreakShop(e.getPlayer())) {
                    BreakShopCommand.stopId(e.getPlayer());
                }
                e.getPlayer().sendMessage("§eShopschild entfernt!");
            }
        }else if(e.getBlock().getType() == Material.CHEST){
            Chest chest = (Chest) e.getBlock().getState();
            chest.setCustomName("");
            if(config.contains(chest.getCustomName())) {
                e.getPlayer().sendMessage("§cBitte entferne zuerst das Shopschild bevor du die Kiste zerstörst!");
                e.setCancelled(true);
            }
        }
    }
}
