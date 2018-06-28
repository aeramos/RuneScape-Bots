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
                new Location(genName("Al Kharid"), new Area.Rectangular(new Coordinate(3292, 3285, 0), new Coordinate(3309, 3315, 0)), new Bank(Bank.BankName.AL_KHARID), genCollectableItems("Adamantite ore", "Coal", "Copper ore", "Gold ore", "Iron ore", "Mithril ore", "Silver ore", "Tin ore")),
                new Location(genName("Bandit Camp"), new Area.Rectangular(new Coordinate(3014, 3793, 0), new Coordinate(3032, 3809, 0)), new Bank(Bank.BankName.EDGEVILLE), genCollectableItems("Adamantite ore", "Coal", "Iron ore", "Mithril ore")), // need to add traveling bankers as bank
                new Location(genName("Barbarian Village"), new Area.Rectangular(new Coordinate(3078, 3418, 0), new Coordinate(3084, 3423, 0)), new Bank(Bank.BankName.EDGEVILLE), genCollectableItems("Coal", "Tin ore")),
                //new Location(genName("Burthorpe"), new Area.Rectangular(new Coordinate(2274, 4498, 0), new Coordinate(2291, 4518, 0)), new Bank(Bank.BankName.BURTHORPE), genCollectableItems("Clay", "Copper ore", "Tin ore")),
                //new Location(genName("Crafting Guild"), new Area.Rectangular(new Coordinate(2943, 3291, 0), new Coordinate(2937, 3276, 0)), new Bank(Bank.BankName.CLAN_CAMP), genCollectableItems("Clay", "Gold ore", "Silver ore")),
                new Location(genName("Crandor north-east"), new Area.Rectangular(new Coordinate(2860, 3286, 0), new Coordinate(2863, 3289, 0)), new Bank(Bank.BankName.DRAYNOR), genCollectableItems("Gold ore")),
                new Location(genName("Crandor north-west"), new Area.Rectangular(new Coordinate(2831, 3290, 0), new Coordinate(2837, 3297, 0)), new Bank(Bank.BankName.DRAYNOR), genCollectableItems("Coal", "Mithril ore")),
                new Location(genName("Crandor south-east"), new Area.Rectangular(new Coordinate(2834, 3242, 0), new Coordinate(2839, 3247, 0)), new Bank(Bank.BankName.DRAYNOR), genCollectableItems("Adamantite ore", "Coal")),
                new Location(genName("Crandor south-west"), new Area.Rectangular(new Coordinate(2819, 3240, 0), new Coordinate(2823, 3248, 0)), new Bank(Bank.BankName.DRAYNOR), genCollectableItems("Mithril ore")),
                new Location(genName("Draynor"), new Area.Rectangular(new Coordinate(3138, 3315, 0), new Coordinate(3143, 3320, 0)), new Bank(Bank.BankName.CABBAGE_FACEPUNCH_BONANZA), genCollectableItems("Clay")),
                new Location(genName("Dwarven"), new Area.Rectangular(new Coordinate(3033, 9759, 0), new Coordinate(3059, 9786, 0)), new Bank(Bank.BankName.FALADOR_EAST), genCollectableItems("Adamantite ore", "Coal", "Copper ore", "Gold ore", "Iron ore", "Mithril ore", "Tin ore")),
                new Location(genName("Edgeville"), new Area.Rectangular(new Coordinate(3134, 9868, 0), new Coordinate(3143, 9880, 0)), new Bank(Bank.BankName.EDGEVILLE), genCollectableItems("Adamantite ore", "Coal", "Copper ore", "Iron ore", "Mithril ore", "Silver ore", "Tin ore")),
                new Location(genName("Falador south-west"), new Area.Rectangular(new Coordinate(2930, 3340, 0), new Coordinate(2922, 3334, 0)), new Bank(Bank.BankName.CLAN_CAMP), genCollectableItems("Coal", "Copper ore", "Iron ore", "Tin ore")),
                new Location(genName("Karamja Volcano"), new Area.Rectangular(new Coordinate(2859, 9576, 0), new Coordinate(2863, 9580, 0)), new Bank(Bank.BankName.DRAYNOR), genCollectableItems("Gold ore")),
                new Location(genName("Lava Maze runite"), new Area.Rectangular(new Coordinate(3058, 3883, 0), new Coordinate(3061, 3886, 0)), new Bank(Bank.BankName.EDGEVILLE), genCollectableItems("Runite ore")), // need to add traveling bankers as bank
                new Location(genName("Lumbridge Swamp east"), new Area.Rectangular(new Coordinate(3233, 3151, 0), new Coordinate(3223, 3145, 0)), new Bank(Bank.BankName.AL_KHARID), genCollectableItems("Copper ore", "Tin ore")),
                new Location(genName("Lumbridge Swamp west"), new Area.Rectangular(new Coordinate(3149, 3152, 0), new Coordinate(3144, 3144, 0)), new Bank(Bank.BankName.DRAYNOR), genCollectableItems("Adamantite ore", "Coal", "Mithril ore")),
                new Location(genName("Mining Guild"), new Area.Rectangular(new Coordinate(3041, 9732, 0), new Coordinate(3055, 9743, 0)), new Bank(Bank.BankName.FALADOR_EAST), genCollectableItems("Coal", "Mithril ore")), // main guild door does not work, only ladder
                new Location(genName("Rimmington"), new Area.Rectangular(new Coordinate(2981, 3242, 0), new Coordinate(2964, 3229, 0)), new Bank(Bank.BankName.CLAN_CAMP), genCollectableItems("Clay", "Copper ore", "Gold ore", "Iron ore", "Tin ore")),
                new Location(genName("Varrock south-east"), new Area.Rectangular(new Coordinate(3280, 3361, 0), new Coordinate(3291, 3371, 0)), new Bank(Bank.BankName.VARROCK_EAST), genCollectableItems("Copper ore", "Iron ore", "Tin ore")),
                new Location(genName("Varrock south-west"), new Area.Rectangular(new Coordinate(3171, 3364, 0), new Coordinate(3188, 3380, 0)), new Bank(Bank.BankName.VARROCK_WEST), genCollectableItems("Clay", "Iron ore", "Silver ore", "Tin ore")),
                new Location(genName("Wilderness south"), new Area.Rectangular(new Coordinate(3102, 3564, 0), new Coordinate(3106, 3571, 0)), new Bank(Bank.BankName.EDGEVILLE), genCollectableItems("Coal", "Iron ore")),
                new Location(genName("Wilderness south-west"), new Area.Rectangular(new Coordinate(3019, 3588, 0), new Coordinate(3025, 3592, 0)), new Bank(Bank.BankName.EDGEVILLE), genCollectableItems("Coal"))
        }, genCollectableItems(), new String[]{"Cracked mining", "Fragile mining", "Mining", "Strong mining", "Decorated mining"}, "Ore", "Powermine", "Powermining");
    }

    private static String genName(String name) {
        if (name.equals("Dwarven")) {
            return name + " Mine";
        } else if (name.equals("Mining Guild")) {
            return name;
        } else {
            return name + " mine";
        }
    }

    private static CollectableItems genCollectableItems(String... items) {
        if (items.length == 0) {
            return genCollectableItems("Adamantite ore", "Clay", "Coal", "Copper ore", "Gold ore", "Iron ore", "Mithril ore", "Runite ore", "Silver ore", "Tin ore");
        }

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