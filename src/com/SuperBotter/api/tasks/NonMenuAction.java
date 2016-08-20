package com.SuperBotter.api.tasks;

import com.SuperBotter.api.Globals;
import com.SuperBotter.api.Methods;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.ViewportPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Validatable;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;

public class NonMenuAction extends Task {
    private Globals globals;
    private Methods methods;
    private Area botArea;
    private String botAreaName, itemName, interactWithName, actionName, actionIng;

    public NonMenuAction(Globals globals, Methods methods, Area botArea, String botAreaName, String itemName, String interactWithName, String actionName, String actionIng) {
        this.globals = globals;
        this.methods = methods;
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
        Player player = Players.getLocal();
        // if the player is in the area or if there is no area
        if(botArea == null || botArea.contains(player)) {
            if (player.getAnimationId() == -1 || player.isMoving()) {
                // prevents spam clicking, but allows misclicks (misclicks are too rare to worry about too much.
                // its either this or i have to maintain a list of Animation IDs that correspond to each action)

                globals.currentAction = actionIng + ' ' + itemName;
                GameObject gameObject = GameObjects.newQuery().names(interactWithName).results().nearest();
                Npc npc;
                boolean notNull = false;
                Interactable interactable = null;
                Locatable locatable = null;
                Validatable validatable = null;
                if (gameObject != null && gameObject.getDefinition() != null) {
                    interactable = gameObject;
                    locatable = gameObject;
                    validatable = gameObject;
                    notNull = true;
                } else if ((npc = Npcs.newQuery().names(interactWithName).results().nearest()) != null && npc.getDefinition() != null) {
                    interactable = npc;
                    locatable = npc;
                    validatable = npc;
                    notNull = true;
                }
                if (notNull) {
                    if (interactable.isVisible()) {
                        if (interactable.interact(actionName, interactWithName)) {
                            // the bot has 3 seconds to click on something
                            Execution.delayUntil(() -> (player.getAnimationId() != -1 || player.isMoving()), 3000);
                            if (player.isMoving()) {
                                Execution.delayUntil(() -> !player.isMoving());
                                Execution.delay(1000); // a little more than a game tick because the game waits for up to 1 tick
                            }
                            // it clicked on something
                            if (player.getAnimationId() != -1) {
                                Validatable finalValidatable = validatable;
                                Execution.delayUntil(() -> !finalValidatable.isValid() || player.getTarget() == null);
                            }
                        }
                    } else {
                        globals.currentAction = "Turing to face " + interactWithName;
                        Camera.turnTo(locatable);
                        if (!interactable.isVisible()) {
                            globals.currentAction = "Going to " + interactWithName;
                            ViewportPath p;
                            p = ViewportPath.convert(RegionPath.buildTo(locatable));
                            if (p == null) {
                                WebPath wp = Traversal.getDefaultWeb().getPathBuilder().buildTo(locatable);
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
        // if the player is not in the area
        } else {
            globals.currentAction = "Going to " + botAreaName;
            methods.goToArea(botArea.getRandomCoordinate());
        }
    }
}