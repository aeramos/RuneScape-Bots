package com.SuperBotter.api.tasks;

import com.SuperBotter.api.ConfigSettings;
import com.SuperBotter.api.Globals;
import com.SuperBotter.api.Methods;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
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
    private ConfigSettings configSettings;
    private Methods methods;

    public NonMenuAction(Globals globals, ConfigSettings configSettings, Methods methods) {
        this.globals = globals;
        this.methods = methods;
        this.configSettings = configSettings;
    }

    @Override
    public boolean validate() {
        // if the inventory is not full, the player isn't banking, and the inventory contains the required items
        return !Inventory.isFull() && !Bank.isOpen() && (Inventory.containsAllOf(configSettings.requiredItems) || (!Inventory.containsAllOf(configSettings.requiredItems) && !configSettings.dontDrop));
    }
    @Override
    public void execute() {
        if (Inventory.containsAllOf(configSettings.requiredItems)) {
            Player player = Players.getLocal();
            if (player != null) {
                // if the player is in the area or if they using a custom area
                if (configSettings.radius != -1 || configSettings.botArea.contains(player)) {
                    globals.path = null; // the bot is no longer following this path, so it can be reset
                    if (configSettings.radius != -1) {
                        configSettings.botArea = new Area.Circular(player.getPosition(), configSettings.radius);
                    }
                    if (player.getAnimationId() == -1 || player.isMoving()) {
                        // prevents spam clicking, but allows misclicks (misclicks are too rare to worry about too much.
                        // its either this or i have to maintain a list of Animation IDs that correspond to each action)

                        // can probably be optimized further
                        globals.currentAction = configSettings.actionIng + ' ' + configSettings.itemName;
                        GameObject gameObject = GameObjects.newQuery().names(configSettings.interactWithName).within(configSettings.botArea).results().nearest();
                        Npc npc;
                        boolean notNull = false;
                        boolean isGameObject = false;
                        LocatableEntity locatableEntity = null;
                        if (gameObject != null && gameObject.getDefinition() != null) {
                            locatableEntity = gameObject;
                            notNull = true;
                            isGameObject = true;
                        } else if ((npc = Npcs.newQuery().names(configSettings.interactWithName).within(configSettings.botArea).results().nearest()) != null && npc.getDefinition() != null) {
                            locatableEntity = npc;
                            notNull = true;
                            isGameObject = false;
                        }
                        if (notNull) {
                            if (locatableEntity.isVisible()) {
                                if (locatableEntity.interact(configSettings.actionName, configSettings.interactWithName)) {
                                    // the bot has 3 seconds to click on something
                                    Execution.delayUntil(() -> (player.getAnimationId() != -1 || player.isMoving()), 3000);
                                    if (player.isMoving()) {
                                        Execution.delayUntil(() -> !player.isMoving());
                                        Execution.delay(1000); // a little more than a game tick because the game waits for up to 1 tick
                                    }
                                    // it clicked on something
                                    if (player.getAnimationId() != -1) {
                                        if (isGameObject) {
                                            Validatable finalLocatableEntity = locatableEntity;
                                            Execution.delayUntil(() -> !finalLocatableEntity.isValid() || player.getAnimationId() == -1);
                                        } else if (player.getTarget() != null) {
                                            Execution.delayUntil(() -> player.getTarget() == null);
                                        }
                                    }
                                }
                            } else {
                                globals.currentAction = "Turing to face " + configSettings.interactWithName;
                                Camera.turnTo(locatableEntity);
                                if (!locatableEntity.isVisible()) {
                                    globals.currentAction = "Going to " + configSettings.interactWithName;
                                    ViewportPath p;
                                    p = ViewportPath.convert(RegionPath.buildTo(locatableEntity));
                                    if (p == null) {
                                        WebPath wp = Traversal.getDefaultWeb().getPathBuilder().buildTo(locatableEntity);
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
                    globals.currentAction = "Going to " + configSettings.botAreaName;
                    if (globals.path == null) {
                        globals.path = methods.getPathTo(configSettings.botArea.getRandomCoordinate(), player);
                    }
                    if (globals.path != null) {
                        globals.path.step();
                    }
                }
            }
        } else {
            Methods.shutdownBot(globals, "Stopping bot - ran out of required items");
        }
    }
}