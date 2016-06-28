package com.SuperBotter.bots.SuperMiner;

import com.SuperBotter.bots.SuperMiner.tasks.Mine;
import com.SuperBotter.bots.SuperMiner.tasks.Store;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.script.framework.task.TaskScript;

public class SuperMiner extends TaskScript {
    public static boolean isMining = false;
    public static boolean hasMined = false;
    public static GameObject oreToMine;
    public static int oreToMineCoordHash;
    public static int isBeingMinedCoordHash;
    public static StopWatch stopWatch = new StopWatch();
    public static long startMineTime = -7500; // so that mining can start instantly
    @Override
    public void onStart(String... args) {
        stopWatch.start();
        setLoopDelay(100, 300); // in ms (1000ms = 1s)
        add(new Mine(), new Store());
        // get input from player on what to mine
        // used to decide if you add bank or drop
    }
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
    }

    // run by Mine and Store to go to their different areas
    public static void goToArea(Area destination) {
        Path p;
        p = RegionPath.buildTo(destination.getRandomCoordinate());
        if (p == null) {
            WebPath wp = Traversal.getDefaultWeb().getPathBuilder().buildTo(destination.getRandomCoordinate());
            if (wp != null) {
                wp.step();
            }
        }
        // if Web path was done then p is still null and this will not run
        if(p != null) {
            p.step();
        }
    }
}