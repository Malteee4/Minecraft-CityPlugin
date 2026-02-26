package de.malteee.citysystem.jobs;

import org.bukkit.Material;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Job {

    BUILDER("Builder", Arrays.asList("CONCRETE", "BRICK", "STONE")),
    HUNTER("Hunter", Arrays.asList()),
    LUMBERJACK("Lumberjack", Arrays.asList("WOOD", "PLANKS", "LOG", "OAK", "SPRUCE", "BIRCH", "ACACIA", "JUNGLE", "MANGROVE", "CHERRY", "PALE", "BAMBOO")), //everything with wood, planks, log
    MINER("Miner", Arrays.asList("ORE", "IRON", "COPPER", "COAL", "DIAMOND", "GOLD", "REDSTONE", "LAPIS", "EMERALD")),
    TRADER("Trader", Arrays.asList()), //Villager
    FISHER("Fisher", Arrays.asList("CORAL", "PRISMARINE")),
    NONE("None", Arrays.asList());

    public static final ArrayList<String> allJobBlocks = new ArrayList<>();

    static {
        for (Job job : Job.values())
            allJobBlocks.addAll(job.getBlocks());
    }

    private List<String> blocks;
    private String display;

    Job(String display, List<String> blocks) {
        this.blocks = blocks;
        this.display = display;
    }

    public String getDisplayName() {
        return display;
    }

    public List<String> getBlocks() {
        return blocks;
    }

    public List<String> getInvertedBlocks() {
        ArrayList<String> list = (ArrayList<String>) allJobBlocks.clone();
        list.removeAll(this.blocks);
        return list;
    }

    public int getMultiplier(String block) {

        return 1;
    }
    //TODO: Record? to save Blocks in connection with value gained
}
