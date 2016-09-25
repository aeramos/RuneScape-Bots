package com.SuperBotter.bots.SuperCookingGuildDoor.ui;

/**
 *  --- InfoController Class ---
 *  This class is used to transfer information between
 *  the two threads we have in this bot. The GUI and bot threads.
 *
 *  Inside SuperMiner.java you will see that in updateInfo()
 *  we create a new InfoController class (this class) and assign its constructor
 *  the appropriate values. You do this so you can pass that newly gathered
 *  information to the GUI thread, in a thread-safe manner by Platform.runLater().
 */
public class InfoController {

    long itemPerHour, itemCount;
    String runtime, currentAction;

    public InfoController(){
        this.itemPerHour = 0;
        this.itemCount = 0;
        this.runtime = "";
        this.currentAction = "";
    }

    public InfoController(long itemPerHour, long itemCount, String runtime, String currentAction) {
        this.itemPerHour = itemPerHour;
        this.itemCount = itemCount;
        this.runtime = runtime;
        this.currentAction = currentAction;
    }

}
