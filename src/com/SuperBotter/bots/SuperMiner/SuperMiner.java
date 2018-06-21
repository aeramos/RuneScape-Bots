package com.SuperBotter.bots.SuperMiner;

import com.SuperBotter.api.Bank;
import com.SuperBotter.api.CollectableItems;
import com.SuperBotter.api.Location;
import com.SuperBotter.api.ProtectedItems;
import com.SuperBotter.bots.SuperBot;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

public class SuperMiner extends SuperBot {
    public SuperMiner() {
        super(Skill.MINING, new Location[]{
                new Location("Al Kharid Mine", new Area.Rectangular(new Coordinate(3292, 3285, 0), new Coordinate(3309, 3315, 0)), new Bank(Bank.BankName.AL_KHARID), genInteractableItems("Adamantite ore", "Coal", "Copper ore", "Gold ore", "Iron ore", "Mithril ore", "Silver ore", "Tin ore")),
                new Location("Draynor", new Area.Rectangular(new Coordinate(3138, 3315, 0), new Coordinate(3143, 3320, 0)), new Bank(Bank.BankName.CABBAGE_FACEPUNCH_BONANZA), genInteractableItems("Clay")),
                new Location("Dwarven Mine", new Area.Rectangular(new Coordinate(3033, 9759, 0), new Coordinate(3059, 9786, 0)), new Bank(Bank.BankName.FALADOR_EAST), genInteractableItems("Adamantite ore", "Coal", "Copper ore", "Gold ore", "Iron ore", "Mithril ore", "Tin ore")),
                new Location("Falador south-west", new Area.Rectangular(new Coordinate(2930, 3340, 0), new Coordinate(2922, 3334, 0)), new Bank(Bank.BankName.CLAN_CAMP), genInteractableItems("Coal", "Copper ore", "Iron ore", "Tin ore")),
                new Location("Lumbridge Swamp east", new Area.Rectangular(new Coordinate(3233, 3151, 0), new Coordinate(3223, 3145, 0)), new Bank(Bank.BankName.AL_KHARID), genInteractableItems("Copper ore", "Tin ore")),
                new Location("Lumbridge Swamp west", new Area.Rectangular(new Coordinate(3149, 3152, 0), new Coordinate(3144, 3144, 0)), new Bank(Bank.BankName.DRAYNOR), genInteractableItems("Adamantite ore", "Coal", "Mithril ore")),
                new Location("Rimmington", new Area.Rectangular(new Coordinate(2981, 3242, 0), new Coordinate(2964, 3229, 0)), new Bank(Bank.BankName.CLAN_CAMP), genInteractableItems("Clay", "Copper ore", "Gold ore", "Iron ore", "Tin ore")),
                new Location("Varrock south-east", new Area.Rectangular(new Coordinate(3280, 3361, 0), new Coordinate(3291, 3371, 0)), new Bank(Bank.BankName.VARROCK_EAST), genInteractableItems("Copper ore", "Iron ore", "Tin ore")),
                new Location("Varrock south-west", new Area.Rectangular(new Coordinate(3171, 3364, 0), new Coordinate(3188, 3380, 0)), new Bank(Bank.BankName.VARROCK_WEST), genInteractableItems("Clay", "Iron ore", "Silver ore", "Tin ore"))
        }, new String[]{"Cracked mining", "Fragile mining", "Mining", "Strong mining", "Decorated mining"}, "Ore", "Powermine", "Powermining");
    }

    private static CollectableItems genInteractableItems(String... items) {
        String[] interactionNames = new String[items.length];
        String[] actionNames = new String[items.length];
        String[] actionIngs = new String[items.length];
        String[] pastTenses = new String[items.length];
        ProtectedItems[] protectedItems = new ProtectedItems[items.length];

        for (int i = 0; i < items.length; i++) {
            interactionNames[i] = items[i] + " rocks";
            actionNames[i] = "Mine";
            actionIngs[i] = "Mining";
            pastTenses[i] = "mined";
            protectedItems[i] = new ProtectedItems();
        }
        return new CollectableItems(items, interactionNames, actionNames, actionIngs, pastTenses, protectedItems);
    }
}