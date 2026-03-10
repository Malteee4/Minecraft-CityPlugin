package de.malteee.citysystem.core;

public enum Stage {

        SETTLEMENT(0, 0, "Settlement", 2500),
        VILLAGE(1, 0, "Village", 9000),
        SMALL_TOWN(4, 1, "Small Town", 20000),
        CITY(8, 2, "City", 35000),
        BIG_CITY(10, 2, "Big City", 60000),
        METROPOLIS(14, 4, "Metropolis", 100000);

        private int shops, farms, maxSize;
        private String display;

        Stage(int shops, int farms, String display, int maxSize) {
            this.shops = shops;
            this.farms = farms;
            this.display = display;
            this.maxSize = maxSize;
        }

        public String getDisplay() {
            return display;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public int getShops() {
            return shops;
        }

        public int getFarms() {
            return farms;
        }
}
