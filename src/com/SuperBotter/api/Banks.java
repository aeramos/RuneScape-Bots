package com.SuperBotter.api;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

public class Banks {
    public enum BankName {
        AL_KHARID, CABBAGE_FACEPUNCH_BONANZA, CLAN_CAMP, COMBAT_ACADEMY, DRAYNOR, VARROCK_EAST, VARROCK_WEST
    }

    public Area area;
    public String name;
    public String type;

    public Banks(BankName bankName) {
        switch (bankName) {
            case AL_KHARID:
                area = new Area.Rectangular(new Coordinate(3272, 3168, 0), new Coordinate(3268, 3161, 0));
                name = "Al Kharid bank";
                type = "Bank booth";
                break;
            case CABBAGE_FACEPUNCH_BONANZA:
                area = new Area.Circular(new Coordinate(3170, 3280, 0), 5);
                name = "Cabbage Facepunch Bonanza bank chest";
                type = "Bank chest";
                break;
            case CLAN_CAMP:
                area = new Area.Circular(new Coordinate(2955, 3297, 0), 5);
                name = "Clan Camp bank chest";
                type = "Bank chest";
                break;
            case COMBAT_ACADEMY:
                area = new Area.Circular(new Coordinate(3215, 3257, 0), 5);
                name = "Combat Academy bank chest";
                type = "Bank chest";
                break;
            case DRAYNOR:
                area = new Area.Rectangular(new Coordinate(3097, 3246, 0), new Coordinate(3092, 3240, 0));
                name = "Draynor bank";
                type = "Counter";
                break;
            case VARROCK_EAST:
                area = new Area.Rectangular(new Coordinate(3250, 3420, 0), new Coordinate(3257, 3423, 0));
                name = "Varrock east bank";
                type = "Bank booth";
                break;
            case VARROCK_WEST:
                area = new Area.Rectangular(new Coordinate(3182, 3433, 0), new Coordinate(3189, 3446, 0));
                name = "Varrock west bank";
                type = "Bank booth";
                break;
        }
    }
}
