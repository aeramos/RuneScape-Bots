package com.SuperBotter.api;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.Web;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPathBuilder;
import com.runemate.game.api.hybrid.region.Region;
import com.runemate.game.api.script.Execution;

public class Methods {
    private WebPathBuilder webPathBuilder;
    public Methods() {
        webPathBuilder = Traversal.getDefaultWeb().getPathBuilder();
    }
    public Path getPathTo(Coordinate destination, Player player) {
        Path p = null;
        // if the player isn't moving
        if (player != null && !player.isMoving()) {
            // if the destination is in the region
            if (Region.getArea().contains(destination)) {
                p = RegionPath.buildTo(destination);
            } else {
                p = webPathBuilder.buildTo(destination);
            }
            // last resort
            if (p == null) {
                p = BresenhamPath.buildTo(destination);
            }
        }
        return p;
    }
    public Path getPathTo(Coordinate destination, Player player, Web customWeb) {
        WebPath p = null;
        if (player != null && !player.isMoving()) {
            if (customWeb != null) {
                p = customWeb.getPathBuilder().buildTo(destination);
            }
        } else {
            return null;
        }
        if (p != null) {
            return p;
        } else {
            return getPathTo(destination, player);
        }
    }
    public static boolean playerIsInWeb(Player player, Web web) {
        Coordinate playerLocation = player.getPosition(); // same coordinate to get a guaranteed reachable coordinate
        // if the path is null, meaning that the coordinate is not in the web, then the method returns false
        return web.getPathBuilder().build(playerLocation, playerLocation) != null;
    }
    public static void shutdownBot(Globals globals, String reason) {
        globals.currentAction = reason;
        while (RuneScape.isLoggedIn()) {
            RuneScape.logout();
            Execution.delayUntil(() -> !RuneScape.isLoggedIn(), 7500);
        }
        Environment.getBot().stop();
    }
}