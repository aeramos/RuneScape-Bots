package com.SuperBotter.bots.SuperCookingGuildDoor.tasks;

import com.SuperBotter.bots.SuperCookingGuildDoor.SuperCookingGuildDoor;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.ViewportPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;

public class EnterDoor extends Task {
    private Area.Circular doorArea;
    private SuperCookingGuildDoor bot;
    public EnterDoor(SuperCookingGuildDoor bot, Area.Circular doorArea) {
        this.bot = bot;
        this.doorArea = doorArea;
    }
    @Override
    public boolean validate() {
        if (bot.player != null && !bot.player.isMoving()) {
            // return true if the player is not moving
            return !bot.player.isMoving();
        } else {
            return false;
        }
    }

    @Override
    public void execute() {
        LocatableEntityQueryResults<GameObject> results = GameObjects.newQuery().within(doorArea).names("Door").results();
        if (results != null) {
            GameObject door = results.first();
            if (door != null) {
                if (door.isVisible()) {
                    bot.globals.currentAction = "Opening door";
                    if (door.interact("Open", "Door")) {
                        Execution.delayUntil(() -> bot.player.isMoving(), 3000);
                        if (bot.player.isMoving()) {
                            bot.globals.currentAction = "Passing through door";
                            Execution.delayUntil(() -> !bot.player.isMoving());
                            bot.clickCount += 1;
                        }
                    }
                } else {
                    bot.globals.currentAction = "Turning to face door";
                    Camera.turnTo(door);
                    if (!door.isVisible()) {
                        bot.globals.currentAction = "Going to door";
                        ViewportPath p;
                        p = ViewportPath.convert(RegionPath.buildTo(door));
                        if (p == null) {
                            WebPath wp = Traversal.getDefaultWeb().getPathBuilder().buildTo(door);
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
        }
    }
}
