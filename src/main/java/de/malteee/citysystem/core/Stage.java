package de.malteee.citysystem.core;

public enum Stage {

        SETTLEMENT(2, 0, 0, "Settlement"),
        VILLAGE(4, 1, 0, "Village"),
        SMALL_TOWN(8, 4, 1, "Small Town"),
        CITY(15, 8, 2, "City"),
        BIG_CITY(20, 10, 2, "Big City"),
        METROPOLIS(28, 14, 4, "Metropolis");

        private int residential, shops, farms;
        private String display;

        Stage(int residential, int shops, int farms, String display) {
            this.residential = residential;
            this.shops = shops;
            this.farms = farms;
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
}
