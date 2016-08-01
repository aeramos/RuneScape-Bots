package com.SuperBotter.bots.SuperMiner.tasks;

import com.SuperBotter.bots.SuperMiner.SuperMiner;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.script.framework.task.Task;

public class Drop extends Task {
    private SuperMiner bot;

    public Drop(SuperMiner bot) {
        this.bot = bot;
    }

    @Override
    public boolean validate() {
        // if the inventory is full
        return Inventory.isFull();
    }
    @Override
    public void execute() {
        bot.updateInfo("Dropping " + bot.oreName);
        // press space just in case the full inv prompt is there
        Keyboard.type(" ", false);
        ActionBar.Slot oreSlot = ActionBar.newQuery().names(bot.oreName).results().first();
        if (oreSlot != null) {
            while (Inventory.contains(bot.oreName)) {
                for (int i = 0; i < Inventory.getQuantity(bot.oreName); i++) {
                    Keyboard.typeKey(oreSlot.getKeyBind());
                }
            }
        }
    }
}