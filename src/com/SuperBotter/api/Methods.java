package com.SuperBotter.api;

import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPathBuilder;
import com.runemate.game.api.hybrid.region.Players;

public class Methods {
    private WebPathBuilder webPathBuilder = Traversal.getDefaultWeb().getPathBuilder();

    public void goToArea(Coordinate destination) {
        if (!Players.getLocal().isMoving()) {
            Path p;
            p = RegionPath.buildTo(destination);
            if (p == null) {
                WebPath wp = webPathBuilder.buildTo(destination);
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