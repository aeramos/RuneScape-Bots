package com.SuperBotter.api.tasks;

import com.SuperBotter.api.Globals;
import com.SuperBotter.api.Methods;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.ViewportPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;

public class NonMenuAction extends Task {
    private Globals globals;
    private Area botArea;
    private String botAreaName, itemName, interactWithName, actionName, actionIng;

    public NonMenuAction(Globals globals, Area botArea, String botAreaName, String itemName, String interactWithName, String actionName, String actionIng) {
        this.globals = globals;
        this.botArea = botArea;
        this.botAreaName = botAreaName;
        this.itemName = itemName;
        this.interactWithName = interactWithName;
        this.actionName = actionName;
        this.actionIng = actionIng;
    }

    @Override
    public boolean validate() {
        // if the inventory is not full and the player isn't banking
        return !(Inventory.isFull() || Bank.isOpen());
    }
    @Override
    public void execute() {
        GameObject itemInteractedWith;
        // if the player is in the area or if there is no area
        if(botArea == null || botArea.contains(Players.getLocal())) {
            globals.currentAction = actionIng + ' ' + itemName;
            itemInteractedWith = GameObjects.newQuery().names(interactWithName).results().nearest();
            // if the fishing spot/ore rock/tree actually exists
            if (itemInteractedWith != null && itemInteractedWith.getDefinition() != null) {
                if (itemInteractedWith.isVisible()) {
                    if (itemInteractedWith.interact(actionName, itemInteractedWith.getDefinition().getName())) {
                        // the bot has 3 seconds to click on something
                        Execution.delayUntil(() -> (Players.getLocal().getAnimationId() != -1 || Players.getLocal().isMoving()), 3000);
                        if (Players.getLocal().isMoving()) {
                            Execution.delayUntil(() -> !Players.getLocal().isMoving());
                            Execution.delay(1000); // a little more than a game tick because the game waits for up to 1 tick
                        }
                        // if the player is interacting with the correct object
                        if (Players.getLocal().getAnimationId() != -1) {
                            if (Players.getLocal().getTarget() == itemInteractedWith) {
                                Execution.delayUntil(() -> !itemInteractedWith.isValid());
                            }
                        }
                    }
                } else {
                    globals.currentAction = "Turing to face " + interactWithName;
                    Camera.turnTo(itemInteractedWith);
                    if (!itemInteractedWith.isVisible()) {
                        globals.currentAction = "Going to " + interactWithName;
                        ViewportPath p;
                        p = ViewportPath.convert(RegionPath.buildTo(itemInteractedWith));
                        if (p == null) {
                            WebPath wp = Traversal.getDefaultWeb().getPathBuilder().buildTo(itemInteractedWith);
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
        // if the player is not in the area
        } else {
            globals.currentAction = "Going to " + botAreaName;
            Methods.goToArea(botArea.getRandomCoordinate());
        }
    }
}