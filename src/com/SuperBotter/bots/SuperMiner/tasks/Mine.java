package com.SuperBotter.bots.SuperMiner.tasks;

import com.SuperBotter.bots.SuperMiner.SuperMiner;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.ViewportPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;

public class Mine extends Task {
    private SuperMiner bot;

    public Mine(SuperMiner bot) {
        this.bot = bot;
    }

    @Override
    public boolean validate() {
        // if the inventory is not full and the player isn't banking
        return !(Inventory.isFull() || Bank.isOpen());
    }
    @Override
    public void execute() {
        GameObject oreBeingMined;
        // if the player is in the mine
        if(bot.mineArea.contains(Players.getLocal())) {
            bot.updateInfo("Mining " + bot.oreName);
            oreBeingMined = GameObjects.newQuery().names(bot.oreRockName).results().nearest();
            // if such a rock actually exists in the region
            if (oreBeingMined != null && oreBeingMined.getDefinition() != null) {
                if (oreBeingMined.isVisible()) {
                    if (oreBeingMined.interact("Mine", oreBeingMined.getDefinition().getName())) {
                        // the bot has 3 seconds to click on something
                        // before this, the bot would sometimes never click on the rock until the player went to the lobby and back
                        Execution.delayUntil(() -> (Players.getLocal().getAnimationId() != -1 || Players.getLocal().isMoving()), 3000);
                        if (Players.getLocal().isMoving()) {
                            Execution.delayUntil(() -> !Players.getLocal().isMoving());
                            Execution.delay(1000); // a little more than a game tick because the game waits for 1 tick
                        }
                        // if the player is mining (as opposed to woodcutting or something (don't wait for misclicks))
                        switch (Players.getLocal().getAnimationId()) {
                            case 624:
                            case 627:
                            case 629:
                                // wait until the ore is gone. this means that either the player or another player took it
                                Execution.delayUntil(() -> !oreBeingMined.isValid());
                                break;
                        }
                    }
                } else {
                    bot.updateInfo("Turing to face " + bot.oreRockName);
                    Camera.turnTo(oreBeingMined);
                    if (!oreBeingMined.isVisible()) {
                        bot.updateInfo("Going to " + bot.oreRockName);
                        ViewportPath p;
                        p = ViewportPath.convert(RegionPath.buildTo(oreBeingMined));
                        if (p == null) {
                            WebPath wp = Traversal.getDefaultWeb().getPathBuilder().buildTo(oreBeingMined);
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
        // if the player is not in the mine
        } else {
            bot.updateInfo("Going to " + bot.mineName);
            bot.goToArea(bot.mineArea.getRandomCoordinate());
        }
    }
}