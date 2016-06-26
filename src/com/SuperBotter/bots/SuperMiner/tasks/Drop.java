package com.SuperBotter.bots.SuperMiner.tasks;

import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceWindows;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;

public class Drop extends Task {
    private String ore = "Iron ore";
    @Override
    public boolean validate() {
        // if the inventory is full
        return Inventory.isFull();
    }

    @Override
    public void execute() {
        // if the inventory is open
        if(InterfaceWindows.getInventory().isOpen()) {
            // do this to all the willow logs in the inventory
            for(SpriteItem item: Inventory.getItems(ore)) {
                // drop them
                if(item.interact("Drop")) {
                    // wait until the player is ready to drop another
                    Execution.delayUntil(() -> !item.isValid(), 1000);
                }
            }
        // if the inventory is closed
        } else {
            // open the inventory
            InterfaceWindows.getInventory().open();
        }
    }
}