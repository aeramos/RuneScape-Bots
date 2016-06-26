package com.SuperBotter.bots.SuperMiner.tasks;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.basic.ViewportPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;

public class Mine extends Task {
    private String ore = "Iron ore rocks"; // becomes "Iron rocks" when mined
    private String mineArea = "Rimmington";
    private Area RimmingtonMine = new Area.Rectangular(new Coordinate(2981, 3242), new Coordinate(2964, 3229));
    @Override
    public boolean validate() {
        //             if the player is idle          and the inventory isn't full  and the bank is closed
        return Players.getLocal().getAnimationId() == -1 && !Inventory.isFull() && !Bank.isOpen();
        // then run execute()
    }
    @Override
    public void execute() {
        switch (mineArea) {
            case "Rimmington":
                if (!RimmingtonMine.contains(Players.getLocal())) {
                    goToArea(RimmingtonMine);
                }
                break;
        }
        GameObject oreToMine = GameObjects.newQuery().names(ore).actions("Mine").results().nearest();
        if (oreToMine != null && oreToMine.getDefinition() != null) {
            if (!oreToMine.isVisible()) {
                Camera.turnTo(oreToMine);
                if (!oreToMine.isVisible()) {
                    ViewportPath p;
                    p = ViewportPath.convert(RegionPath.buildTo(oreToMine));
                    if (p == null) {
                        p = ViewportPath.convert(BresenhamPath.buildTo(oreToMine));
                    }
                    if (p != null) {
                        p.step();
                    }
                }
            } else if (oreToMine.interact("Mine")) {
                Execution.delayUntil(() -> Players.getLocal().getAnimationId() != -1);
            }
        }
    }
    public void goToArea(Area destination) {
        ViewportPath p;
        p = ViewportPath.convert(RegionPath.buildTo(destination.getRandomCoordinate()));
        if (p == null) {
            p = ViewportPath.convert(BresenhamPath.buildTo(destination.getRandomCoordinate()));
        }
        if(p != null) {
            p.step();
        }
    }
}