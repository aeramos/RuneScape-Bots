package com.SuperBotter.api;

import com.runemate.game.api.hybrid.location.Area;

/**
 * Class of constants similar to Globals but these are not used in every bot.
 * I may change the way that global variables are used to prevent two global variable holders from being made.
 */

public class ConfigSettings {
    public boolean guiWait = true;
    public boolean startButtonPressed = false;
    public boolean dontDrop;
    public String[] requiredItems = new String[]{};
    public int[] requiredItemsAmount = new int[]{};
    public int radius = -1;
    public Area botArea;
    public String botAreaName = null;
    public Banks bank = null;
    public String itemName = null;
    public String interactWithName = null;
    public String actionName = null;
    public String actionIng = null;
}