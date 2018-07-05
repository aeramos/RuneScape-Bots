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
import com.runemate.game.api.hybrid.entities.definitions.GameObjectDefinition;
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

    public NonMenuAction(LoopingBot bot, Globals globals, ConfigSettings configSettings, Methods methods, ProtectedItems protectedItems) {
        this.bot = bot;
        this.globals = globals;
        this.methods = methods;
        this.configSettings = configSettings;
        this.protectedItems = protectedItems;
    }

    @Override
    public boolean validate() {
        // if the inventory is not full, the bot isn't dropping, the player isn't banking, and the inventory contains the required items / isn't banking
        if (RuneScape.isLoggedIn() && !globals.isDropping && !Inventory.isFull() && !Bank.isOpen()) {
            if ((protectedItems.getMissingItems(ProtectedItems.Status.REQUIRED).length > 0 && configSettings.dontDrop) || configSettings.collectableItems.size(false) == 0) {
                Methods.shutdownBot(bot, globals, "Ran out of required items", true);
                return false;
            }

            for (int i = 0; i < configSettings.collectableItems.getProtectedItems(false).length; i++) {
                if (configSettings.collectableItems.getProtectedItems(false)[i].getMissingItems(ProtectedItems.Status.REQUIRED).length > 0) {
                    if (!configSettings.dontDrop) {
                        configSettings.collectableItems.remove(i);
                        i--;
                    } else {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void execute() {
        bot.setLoopDelay(50, 150);
        Player player = Players.getLocal();
        if (player != null) {
            // if the player is in the area or if they are using a custom area
            if (configSettings.radius != -1 || configSettings.botArea.contains(player)) {
                globals.path = null; // the bot is no longer following this path, so it can be cleared
                if (configSettings.radius != -1) {
                    configSettings.botArea = new Area.Circular(player.getPosition(), configSettings.radius);
                }
                if (player.getAnimationId() == -1 || player.isMoving()) {
                    // prevents spam clicking, but allows misclicks (misclicks are too rare to worry about too much.
                    // its either this or i have to maintain a list of Animation IDs that correspond to each action)

                    // getting the thing to interact with can probably be optimized further (maybe split GameObject and NPC with conditional)
                    GameObject gameObject = GameObjects.newQuery().names(configSettings.collectableItems.getInteractionNames(false)).within(configSettings.botArea).results().nearest();
                    Npc npc;
                    boolean isGameObject = false;
                    LocatableEntity locatableEntity = null;
                    int collectableItemID = -1;
                    GameObjectDefinition definition;
                    if (gameObject != null && (definition = gameObject.getDefinition()) != null) {
                        locatableEntity = gameObject;
                        collectableItemID = configSettings.collectableItems.getIndexByInteractionName(definition.getName(), false);
                        isGameObject = true;
                    } else if ((npc = Npcs.newQuery().names(configSettings.collectableItems.getInteractionNames(false)).within(configSettings.botArea).results().nearest()) != null && npc.getDefinition() != null) {
                        locatableEntity = npc;
                        collectableItemID = configSettings.collectableItems.getIndexByInteractionName(npc.getName(), false);
                        isGameObject = false;
                    }
                    if (collectableItemID != -1) {
                        globals.currentAction = configSettings.collectableItems.getActionings(false)[collectableItemID] + ' ' + configSettings.collectableItems.getNames(false)[collectableItemID];
                        if (locatableEntity.isVisible()) {
                            if (locatableEntity.interact(configSettings.collectableItems.getActionNames(false)[collectableItemID], configSettings.collectableItems.getInteractionNames(false)[collectableItemID])) {
                                // the bot has 3 seconds to click on something
                                Execution.delayUntil(() -> (player.getAnimationId() != -1 || player.isMoving()), 3000);
                                if (player.isMoving()) {
                                    Execution.delayUntil(() -> !player.isMoving());
                                    Execution.delay(1000); // a little more than a game tick because the game waits for up to 1 tick (600ms)
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
                            globals.currentAction = "Turing to face " + configSettings.collectableItems.getInteractionNames(false)[collectableItemID];
                            Camera.turnTo(locatableEntity);
                            if (!locatableEntity.isVisible()) {
                                globals.currentAction = "Going to " + configSettings.collectableItems.getInteractionNames(false)[collectableItemID];
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
                if (player.getAnimationId() == -1) {
                    globals.currentAction = "Going to " + configSettings.botAreaName;
                    if (globals.path == null) {
                        globals.path = methods.getPathTo(configSettings.botArea.getRandomCoordinate(), player);
                    }
                    if (globals.path != null) {
                        globals.path.step();
                    }
                }
            }
        }
    }
}
