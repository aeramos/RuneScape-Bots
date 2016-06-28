package com.SuperBotter.bots.SuperMiner;

import com.SuperBotter.bots.SuperMiner.tasks.Mine;
import com.SuperBotter.bots.SuperMiner.tasks.Store;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.script.framework.task.TaskScript;

//import com.runemate.game.api.hybrid.util.StopWatch;

public class SuperMiner extends TaskScript {
    //private StopWatch stopWatch = new StopWatch();
    @Override
    public void onStart(String... args) {
        //stopWatch.start();
        setLoopDelay(300, 750); // in ms (1000ms = 1s)
        add(new Mine(), new Store());
        // get input from player on what to mine
        // used to decide if you add bank or drop
    }/*
    @Override
    public void onPause() {
        stopWatch.stop();
    }
    @Override
    public void onResume() {
        stopWatch.start();
    }
    @Override
    public void onStop() {
        stopWatch.stop();
    }*/

    // run by Mine and Store to go to their different areas
    public static void goToArea(Area destination) {
        Path p;
        p = RegionPath.buildTo(destination.getRandomCoordinate());
        if (p == null) {
            p = BresenhamPath.buildTo(destination.getRandomCoordinate());
        }
        if(p != null) {
            p.step();
        }
    }
}