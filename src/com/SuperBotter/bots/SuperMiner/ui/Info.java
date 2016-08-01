package com.SuperBotter.bots.SuperMiner.ui;

/**
 *  --- Info Class ---
 *  This class is used to transfer information between
 *  the two threads we have in this bot. The GUI and bot threads.
 *
 *  Inside SuperMiner.java you will see that in updateInfo()
 *  we create a new Info class (this class) and assign its constructor
 *  the appropriate values. You do this so you can pass that newly gathered
 *  information to the GUI thread, in a thread-safe manner by Platform.runLater().
 */
public class Info {

    long orePerHour, oreCount, xpPerHour, xpGained;
    String runTime, currentAction;

    public Info(){
        this.orePerHour = 0;
        this.oreCount = 0;
        this.xpPerHour = 0;
        this.xpGained = 0;
        this.runTime = "";
        this.currentAction = "";
    }

    public Info(long orePerHour, long oreCount, long xpPerHour, long xpGained, String runTime, String currentAction) {
        this.orePerHour = orePerHour;
        this.oreCount = oreCount;
        this.xpPerHour = xpPerHour;
        this.xpGained = xpGained;
        this.runTime = runTime;
        this.currentAction = currentAction;
    }

}
