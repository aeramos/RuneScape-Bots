package com.SuperBotter.api;

import com.runemate.game.api.client.ClientUI;
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
import com.runemate.game.api.script.framework.AbstractBot;
import javafx.scene.paint.Color;

import java.lang.reflect.Array;

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

    public static void shutdownBot(AbstractBot bot, String reason, boolean logout) {
        ClientUI.showAlert(bot.getMetaData().getName() + ": " + reason, Color.RED);

        // I know its ugly, but I don't want the bots to just be sitting there.
        if (logout) {
            for (int i = 0; i < 3; i++) {
                if (RuneScape.isLoggedIn()) {
                    if (RuneScape.logout()) {
                        Execution.delayUntil(() -> !RuneScape.isLoggedIn(), 5000);
                    }
                } else {
                    break;
                }
            }
        }
        bot.stop();
    }

    public static void shutdownBot(AbstractBot bot, Globals globals, String reason,  boolean logout) {
        globals.currentAction = "Stopping bot: " + reason;
        shutdownBot(bot, reason, logout);
    }

    public static <T> T[] concatenate (T[] a, T[] b) {
        int aLength = a.length;
        int bLength = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLength+bLength);
        System.arraycopy(a, 0, c, 0, aLength);
        System.arraycopy(b, 0, c, aLength, bLength);

        return c;
    }
}