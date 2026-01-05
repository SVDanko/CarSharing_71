package org.example.carsharing_71.explain;

public class House {
    private int rooms;
    private int floors;
    private boolean hasSwimmingPool;
    private boolean hasGarage;
    private boolean hasGarden;
    private String roofType;
    private String wallMaterial;
    // more 10-20 fields


    public House() {
    }

    public House(int rooms, int floors) {
        this.rooms = rooms;
        this.floors = floors;
    }
    public House(int rooms, int floors, boolean hasSwimmingPool) {
        this.rooms = rooms;
        this.floors = floors;
        this.hasSwimmingPool = hasSwimmingPool;
    }
    public House(int rooms, int floors, boolean hasSwimmingPool, boolean hasGarage, boolean hasGarden, String roofType, String wallMaterial) {
        this.rooms = rooms;
        this.floors = floors;
        this.hasSwimmingPool = hasSwimmingPool;
        this.hasGarage = hasGarage;
        this.hasGarden = hasGarden;
        this.roofType = roofType;
        this.wallMaterial = wallMaterial;
    }
}
