package com.SuperBotter.bots.SuperMiner.tasks;

import com.SuperBotter.bots.SuperMiner.SuperMiner;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.script.framework.task.Task;

public class Drop extends Task {
    @Override
    public boolean validate() {
        // if the inventory is full
        return Inventory.isFull() || SuperMiner.isDropping;
    }
    @Override
    public void execute() {
        SuperMiner.updateInfo("Dropping " + SuperMiner.oreName);
        SuperMiner.isMining = false;
        SuperMiner.isDropping = true;
        ActionBar.Slot oreSlot = ActionBar.newQuery().names(SuperMiner.oreName).results().first();
        if (oreSlot != null) {
            for (int i = 0; i < Inventory.getQuantity(SuperMiner.oreName); i++) {
                Keyboard.typeKey(oreSlot.getKeyBind());
            }
        }
        if (Inventory.getQuantity(SuperMiner.oreName) == 0) {
            SuperMiner.isDropping = false;
        }
    }
}