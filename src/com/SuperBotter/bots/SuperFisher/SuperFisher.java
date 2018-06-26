package com.SuperBotter.bots.SuperFisher;

import com.SuperBotter.api.Bank;
import com.SuperBotter.api.CollectableItems;
import com.SuperBotter.api.Location;
import com.SuperBotter.api.ProtectedItems;
import com.SuperBotter.bots.SuperBot;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

public class SuperFisher extends SuperBot {
    public SuperFisher() {
        super(Skill.FISHING, new Location[]{
                new Location("Al Kharid west", new Area.Rectangular(new Coordinate(3255, 3159, 0), new Coordinate(3258, 3164, 0)), new Bank(Bank.BankName.AL_KHARID), genCollectableItems("Raw anchovies", "Raw herring", "Raw sardine", "Raw shrimps")),
                new Location("Lum Bridge", new Area.Rectangular(new Coordinate(3239, 3241, 0), new Coordinate(3242, 3257, 0)), new Bank(Bank.BankName.COMBAT_ACADEMY), genCollectableItems("Raw pike", "Raw salmon", "Raw trout")),
                new Location("Lumbridge Church", new Area.Rectangular(new Coordinate(3256, 3203, 0), new Coordinate(3258, 3207, 0)), new Bank(Bank.BankName.COMBAT_ACADEMY), genCollectableItems("Raw crayfish")),
                new Location("Lumbridge Swamp east", new Area.Rectangular(new Coordinate(3239, 3146, 0), new Coordinate(3246, 3157, 0)), new Bank(Bank.BankName.COMBAT_ACADEMY), genCollectableItems("Raw anchovies", "Raw herring", "Raw sardine", "Raw shrimps"))
        }, genCollectableItems(), new String[]{"Cracked fishing", "Fragile fishing", "Fishing", "Strong fishing", "Decorated fishing"}, "Fish", "Powerfish", "Powerfishing");
    }

    private static CollectableItems genCollectableItems(String... items) {
        if (items.length == 0) {
            return genCollectableItems("Raw anchovies", "Raw crayfish", "Raw herring", "Raw lobster", "Raw pike", "Raw salmon", "Raw sardine", "Raw shark", "Raw shrimps", "Raw swordfish", "Raw trout", "Raw tuna");
        }

        String[] interactionNames = new String[items.length];
        String[] actionNames = new String[items.length];
        String[] actionIngs = new String[items.length];
        String[] pastTenses = new String[items.length];
        ProtectedItems[] protectedItems = new ProtectedItems[items.length];

        for (int i = 0; i < items.length; i++) {
            interactionNames[i] = "Fishing spot";
            protectedItems[i] = new ProtectedItems();
            pastTenses[i] = "caught";
            switch (items[i]) {
                case "Raw anchovies":
                case "Raw shrimps":
                    actionNames[i] = "Net";
                    actionIngs[i] = "Netting";
                    break;
                case "Raw crayfish":
                case "Raw lobster":
                    actionNames[i] = "Cage";
                    actionIngs[i] = "Caging";
                    break;
                case "Raw herring":
                case "Raw pike":
                case "Raw sardine":
                    actionNames[i] = "Bait";
                    actionIngs[i] = "Baiting";
                    protectedItems[i].add("Fishing bait", 0, ProtectedItems.Status.REQUIRED);
                    break;
                case "Raw salmon":
                case "Raw trout":
                    actionNames[i] = "Lure";
                    actionIngs[i] = "Luring";
                    protectedItems[i].add("Feather", 0, ProtectedItems.Status.REQUIRED);
                    break;
                case "Raw tuna":
                case "Raw swordfish":
                case "Raw shark":
                    actionNames[i] = "Harpoon";
                    actionIngs[i] = "Harpooning";
                    break;
            }
        }
        return new CollectableItems(items, interactionNames, actionNames, actionIngs, pastTenses, protectedItems);
    }
}