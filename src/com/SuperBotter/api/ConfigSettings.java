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
    public int radius = -1;
    public Area botArea;
    public String botAreaName = null;
    public Bank bank = null;
    public InteractableItems interactableItems = new InteractableItems();
}