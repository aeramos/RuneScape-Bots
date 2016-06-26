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
import com.runemate.game.api.script.framework.task.Task;

// had to name it Store instead of Bank to prevent conflicts with RuneMate's Bank (in the API)
public class Store extends Task {
    private String mineArea = "Rimmington";
    private Area clanCampBankArea = new Area.Rectangular(new Coordinate(2957, 3299), new Coordinate(2954, 3295));
    @Override
    public boolean validate() {
        return Inventory.isFull() || Bank.isOpen();
    }
    @Override
    public void execute() {
        switch (mineArea) {
            case "Rimmington":
                if (clanCampBankArea.contains(Players.getLocal())) {
                    if (Bank.isOpen()) {
                        if (Inventory.isFull()) {
                            Bank.depositInventory();
                        } else {
                            Bank.close();
                        }
                    } else {
                        GameObject bankChest = GameObjects.newQuery().names("Bank chest").results().first();
                        if(!bankChest.isVisible()) {
                            Camera.turnTo(bankChest);
                        }
                        Bank.open();
                    }
                } else {
                    ViewportPath p;
                    p = ViewportPath.convert(RegionPath.buildTo(clanCampBankArea.getRandomCoordinate()));
                    if (p == null) {
                        p = ViewportPath.convert(BresenhamPath.buildTo(clanCampBankArea.getRandomCoordinate()));
                    }
                    if(p != null) {
                        p.step();
                    }
                }
                break;
        }
    }
}