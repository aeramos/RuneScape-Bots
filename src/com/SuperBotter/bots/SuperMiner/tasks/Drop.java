package com.SuperBotter.bots.SuperMiner.tasks;

import com.SuperBotter.bots.SuperMiner.SuperMiner;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceWindows;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;

public class Drop extends Task {
    @Override
    public boolean validate() {
        // if the inventory is full
        return Inventory.isFull();
    }

    @Override
    public void execute() {
        SuperMiner.currentAction = "Dropping " + SuperMiner.oreName;
        SuperMiner.isMining = false;
        // if the inventory is open
        if(InterfaceWindows.getInventory().isOpen()) {
            // do this to all the ore in the inventory
            for(SpriteItem item: Inventory.getItems(SuperMiner.oreName)) {
                // drop them
                if(item.interact("Drop")) {
                    // wait until the player is ready to drop another or until 2 seconds have passed
                    Execution.delayUntil(() -> !item.isValid(), 2000);
                }
            }
        // if the inventory is closed
        } else {
            // open the inventory
            InterfaceWindows.getInventory().open();
        }
        SuperMiner.updateInfo();
    }
}