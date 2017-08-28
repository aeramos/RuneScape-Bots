package com.SuperBotter.api.tasks;

import com.SuperBotter.api.ConfigSettings;
import com.SuperBotter.api.Globals;
import com.SuperBotter.api.Methods;
import com.SuperBotter.api.ProtectedItems;
import com.runemate.game.api.hybrid.RuneScape;
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
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.task.Task;

public class NonMenuAction extends Task {
    private LoopingBot bot;
    private Globals globals;
    private ConfigSettings configSettings;
    private Methods methods;
    private ProtectedItems protectedItems;

    private Integer[] requiredItems;

    public NonMenuAction(LoopingBot bot, Globals globals, ConfigSettings configSettings, Methods methods, ProtectedItems protectedItems) {
        this.bot = bot;
        this.globals = globals;
        this.methods = methods;
        this.configSettings = configSettings;
        this.protectedItems = protectedItems;
    }

    @Override
    public boolean validate() {
        requiredItems = protectedItems.getMissingItems(new ProtectedItems.Status[]{ProtectedItems.Status.REQUIRED});
        // if the inventory is not full, the bot isn't dropping, the player isn't banking, and the inventory contains the required items / isn't banking
        return RuneScape.isLoggedIn() && !globals.isDropping && !Inventory.isFull() && !Bank.isOpen() && (requiredItems.length == 0 || !configSettings.dontDrop);
    }
    @Override
    public void execute() {
        bot.setLoopDelay(100, 300);
        // if the player has all of the items it needs
        if (requiredItems.length == 0) {
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
            Methods.shutdownBot(bot, globals, "Ran out of required items", true);
        }
    }
}