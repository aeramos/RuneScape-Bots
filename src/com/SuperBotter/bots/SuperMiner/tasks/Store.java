package com.SuperBotter.bots.SuperMiner.tasks;

import com.SuperBotter.bots.SuperMiner.SuperMiner;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.task.Task;

// had to name it Store instead of Bank to prevent conflicts with RuneMate's Bank (in the API)
public class Store extends Task {
    @Override
    public boolean validate() {
        return Inventory.isFull() || Bank.isOpen();
    }
    @Override
    public void execute() {
        SuperMiner.isMining = false;
        if (SuperMiner.bankArea.contains(Players.getLocal())) {
            if (Bank.isOpen()) {
                if (Inventory.isFull()) {
                    SuperMiner.updateInfo("Depositing inventory");
                    Bank.depositInventory();
                } else {
                    SuperMiner.updateInfo("Closing " + SuperMiner.bankName);
                    Bank.close();
                }
            } else {
                GameObject bankChest = GameObjects.newQuery().names(SuperMiner.bankType).results().first();
                if(!bankChest.isVisible()) {
                    SuperMiner.updateInfo("Turning to face " + SuperMiner.bankName);
                    Camera.turnTo(bankChest);
                }
                SuperMiner.updateInfo("Opening bank " + SuperMiner.bankName);
                Bank.open();
            }
        } else {
            SuperMiner.updateInfo("Going to " + SuperMiner.bankName);
            SuperMiner.goToArea(SuperMiner.bankArea);
        }
    }
}