package com.SuperBotter.api;

import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.Players;

public class Methods {
    public static void goToArea(Coordinate destination) {
        if (!Players.getLocal().isMoving()) {
            Path p;
            p = RegionPath.buildTo(destination);
            if (p == null) {
                WebPath wp = Traversal.getDefaultWeb().getPathBuilder().buildTo(destination);
                if (wp != null) {
                    wp.step();
                }
            }
            // if Web path was done then p is still null and this will not run
            if (p != null) {
                p.step();
            }
        }
    }
}
