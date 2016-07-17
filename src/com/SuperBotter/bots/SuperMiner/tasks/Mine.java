package com.SuperBotter.bots.SuperMiner.tasks;

import com.SuperBotter.bots.SuperMiner.SuperMiner;
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
    @Override
    public boolean validate() {
        // if the inventory is not full and the player isn't banking or dropping
        return !(Inventory.isFull() || Bank.isOpen());
    }
    @Override
    public void execute() {
        // if the player is in the mine
        if(SuperMiner.mineArea.contains(Players.getLocal())) {
            SuperMiner.updateInfo("Mining " + SuperMiner.oreName);
            SuperMiner.oreBeingMined = GameObjects.newQuery().names(SuperMiner.oreRockName).results().nearest();
            // if such a rock actually exists in the region
            if (SuperMiner.oreBeingMined != null && SuperMiner.oreBeingMined.getDefinition() != null) {
                if (SuperMiner.oreBeingMined.isVisible()) {
                    if (SuperMiner.oreBeingMined.interact("Mine", SuperMiner.oreBeingMined.getDefinition().getName())) {
                        // the bot has 3 seconds to click on the rock
                        // before this, the bot would sometimes never click on the rock until the player went to the lobby and back
                        Execution.delayUntil(() -> (Players.getLocal().getAnimationId() != -1 || Players.getLocal().isMoving()), 3000);
                        if (Players.getLocal().isMoving()) {
                            Execution.delayUntil(() -> !Players.getLocal().isMoving());
                        }
                        if (Players.getLocal().getAnimationId() != -1) {
                            // wait until the ore is gone. this means that either the player or another player took it
                            Execution.delayUntil(() -> !GameObjects.newQuery().names(SuperMiner.oreRockName).results().contains(SuperMiner.oreBeingMined));
                        }
                    }
                } else {
                    SuperMiner.updateInfo("Turing to face " + SuperMiner.oreRockName);
                    Camera.turnTo(SuperMiner.oreBeingMined);
                    if (!SuperMiner.oreBeingMined.isVisible()) {
                        SuperMiner.updateInfo("Going to " + SuperMiner.oreRockName);
                        ViewportPath p;
                        p = ViewportPath.convert(RegionPath.buildTo(SuperMiner.oreBeingMined));
                        if (p == null) {
                            WebPath wp = Traversal.getDefaultWeb().getPathBuilder().buildTo(SuperMiner.oreBeingMined);
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
            SuperMiner.updateInfo("Going to " + SuperMiner.mineName);
            SuperMiner.goToArea(SuperMiner.mineArea);
        }
    }
}