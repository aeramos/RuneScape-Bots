package com.SuperBotter.api;

import com.runemate.game.api.hybrid.location.Area;

public class Location {
    private final String name;
    private final Area area;
    private final Bank bank;
    private final CollectableItems collectableItems;

    public Location(String name, Area area, Bank bank, CollectableItems collectableItems) {
        this.name = name;
        this.area = area;
        this.bank = bank;
        this.collectableItems = collectableItems;
    }

    public String getName() {
        return name;
    }

    public Area getArea() {
        return area;
    }

    public Bank getBank() {
        return bank;
    }

    public CollectableItems getCollectableItems() {
        return collectableItems;
    }
}
