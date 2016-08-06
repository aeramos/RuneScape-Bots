package com.SuperBotter.bots.SuperMiner.tasks;

import com.SuperBotter.bots.SuperMiner.SuperMiner;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceWindows;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.script.Execution;
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
        while (Inventory.contains(bot.oreName) || !Inventory.newQuery().filter(a -> a.getDefinition().getName().contains("Uncut")).results().isEmpty()) {
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
            } else {
                Execution.delayUntil(() -> manuallyDrop(Inventory.newQuery().names(bot.oreName).results()), 60000);
            }
            Execution.delayUntil(() -> manuallyDrop(Inventory.newQuery().filter(a -> a.getDefinition().getName().contains("Uncut")).results()), 60000);
        }
    }
    // separate function to prevent code repetition
    private static boolean manuallyDrop(SpriteItemQueryResults itemsToDrop) {
        if (!itemsToDrop.isEmpty()) {
            if (InterfaceWindows.getInventory().isOpen()) {
                for (SpriteItem item : itemsToDrop) {
                    // drop them
                    if (item.interact("Drop")) {
                        // wait until the player is ready to drop another or until a bit more than 1 game tick has passed
                        Execution.delayUntil(() -> !item.isValid(), 1000);
                    }
                }
                // if the inventory is closed
            } else {
                // open the inventory
                InterfaceWindows.getInventory().open();
            }
        }
        return true;
    }
}