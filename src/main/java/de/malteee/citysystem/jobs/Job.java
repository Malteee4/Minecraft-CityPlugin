package de.malteee.citysystem.jobs;

import de.malteee.citysystem.CitySystem;
import de.malteee.citysystem.core.City;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Job {

    BUILDER("Builder", true, false),
    HUNTER("Hunter", false, true),
    LUMBERJACK("Lumberjack", true, false), //everything with wood, planks, log
    MINER("Miner", true, false),
    TRADER("Trader", true, false), //Villager
    FISHER("Fisher", false, true),
    NONE("None", false, false);

    public static final ArrayList<Material> allJobBlocks = new ArrayList<>();
    public static final ArrayList<EntityType> allEntityTypes = new ArrayList<>();
    private final FileConfiguration config = CitySystem.getJobConfig();

    static {
        FileConfiguration conf = CitySystem.getJobConfig();
        for (Job job : Job.values()) {
            if (!conf.contains(job.toString() + ".materialList")) {
                conf.set(job.toString() + ".materialList", new ArrayList<String>());
                CitySystem.saveJobConfig();
            }
            if (job == HUNTER || job == FISHER) {
                for (String str : conf.getStringList(job.toString() + ".materialList"))
                    allEntityTypes.add(EntityType.valueOf(str));
            }else {
                for (String str : conf.getStringList(job.toString() + ".materialList"))
                    allJobBlocks.add(Material.valueOf(str));
            }
        }
    }

    private List<Material> blocks = new ArrayList<>();
    private List<EntityType> entities = new ArrayList<>(); //for Hunter and Fisher
    private String display;

    Job(String display, boolean material, boolean entity) {
        this.display = display;
        if (!config.contains(this.toString() + ".materialList")) {
            config.set(this.toString() + ".materialList", new ArrayList<String>());
            CitySystem.saveJobConfig();
        }
        if (material)
            for (String str : config.getStringList(this.toString() + ".materialList"))
                blocks.add(Material.valueOf(str));
        if (entity)
            for (String str : config.getStringList(this.toString() + ".materialList"))
                entities.add(EntityType.valueOf(str));
    }

    public String getDisplayName() {
        return display;
    }

    public List<Material> getBlocks() {
        return blocks;
    }

    public List<Material> getInvertedBlocks() {
        ArrayList<Material> list = (ArrayList<Material>) allJobBlocks.clone();
        list.removeAll(blocks);
        return list;
    }

    public List<EntityType> getEntities() {
        return entities;
    }

    public List<EntityType> getInvertedEntities() {
        ArrayList<EntityType> list = (ArrayList<EntityType>) allEntityTypes.clone();
        list.removeAll(entities);
        return list;
    }

    public double getValue(Material material) {
        if (!config.contains(this.toString() + ".material." + material.toString())) {
            config.set(this.toString() + ".material." + material.toString(), 1.0);
            CitySystem.saveJobConfig();
        }
        return config.getDouble(this.toString() + ".material." + material.toString());
    }

    public double getValue(EntityType material) {
        if (!config.contains(this.toString() + ".material." + material.toString())) {
            config.set(this.toString() + ".material." + material.toString(), 1.0);
            CitySystem.saveJobConfig();
        }
        return config.getDouble(this.toString() + ".material." + material.toString());
    }
}
