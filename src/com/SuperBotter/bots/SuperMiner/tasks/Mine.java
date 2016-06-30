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
import com.runemate.game.api.script.framework.task.Task;

public class Mine extends Task {
    @Override
    public boolean validate() {
        //             if the player is idle          and the inventory isn't full  and the bank is closed
        if (!SuperMiner.hasMined) {
            return Players.getLocal().getAnimationId() == -1 && !Inventory.isFull() && !Bank.isOpen();
        } else {
            if (Inventory.isFull() || Bank.isOpen()) {
                return false;
            }
            return Players.getLocal().getAnimationId() == -1 || (SuperMiner.isMining || SuperMiner.oreToMine == null);
        }
        // then run execute()
    }
    @Override
    public void execute() {
        SuperMiner.hasMined = true;
        if(!SuperMiner.mineArea.contains(Players.getLocal())) {
            SuperMiner.currentAction = "Going to " + SuperMiner.mineName;
            SuperMiner.updateInfo();
            SuperMiner.goToArea(SuperMiner.mineArea);
        }
        SuperMiner.currentAction = "Mining " + SuperMiner.oreName;
        SuperMiner.oreToMine = GameObjects.newQuery().names(SuperMiner.oreRockName).results().nearest();
        if (SuperMiner.oreToMine == null) {
            SuperMiner.updateInfo();
            return;
        } else {
            SuperMiner.oreToMineCoordHash = SuperMiner.oreToMine.getPosition().hashCode();
        }
        if (SuperMiner.oreToMine != null && SuperMiner.oreToMine.getDefinition() != null) {
            if (!SuperMiner.oreToMine.isVisible()) {
                Camera.turnTo(SuperMiner.oreToMine);
                if (!SuperMiner.oreToMine.isVisible()) {
                    ViewportPath p;
                    p = ViewportPath.convert(RegionPath.buildTo(SuperMiner.oreToMine));
                    if (p == null) {
                        WebPath wp = Traversal.getDefaultWeb().getPathBuilder().buildTo(SuperMiner.oreToMine);
                        if (wp != null) {
                            wp.step();
                            SuperMiner.isMining = false;
                        }
                    }
                    // if Web path was done then p is still null and this will not run
                    if (p != null) {
                        p.step();
                        SuperMiner.isMining = false;
                    }
                }
            } else if (SuperMiner.oreToMineCoordHash != SuperMiner.isBeingMinedCoordHash || SuperMiner.startMineTime + 7500 <= SuperMiner.stopWatch.getRuntime()) {
                if (SuperMiner.oreToMine.interact("Mine", SuperMiner.oreToMine.getDefinition().getName())) {
                    SuperMiner.startMineTime = SuperMiner.stopWatch.getRuntime();
                    SuperMiner.isMining = true;
                    SuperMiner.isBeingMinedCoordHash = SuperMiner.oreToMineCoordHash;
                }
            }
        }
        SuperMiner.updateInfo();
    }
}