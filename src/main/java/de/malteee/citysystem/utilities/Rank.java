package de.malteee.citysystem.utilities;

public enum Rank {

    PLAYER("§3%player%"),
    ADVANCED_PLAYER("§3§l%player%"),
    MODERATOR("§6%player%"),
    SUPPORTER("§e%player%"),
    ADMIN("§6§l%player%"),
    NONE("%player%");

    private String display;

    Rank(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
