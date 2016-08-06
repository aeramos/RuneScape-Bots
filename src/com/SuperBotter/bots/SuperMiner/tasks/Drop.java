package com.SuperBotter.bots.SuperMiner.tasks;

import com.SuperBotter.bots.SuperMiner.SuperMiner;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceWindows;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.queries.results.ActionBarQueryResults;
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
            // press space just in case the full inv prompt is there
            Keyboard.type(" ", false);
            Execution.delayUntil(() -> actionBarDrop(ActionBar.newQuery().names(bot.oreName).results()), 30000);
            Execution.delayUntil(() -> manuallyDrop(Inventory.newQuery().names(bot.oreName).results()), 60000);
            Execution.delayUntil(() -> manuallyDrop(Inventory.newQuery().filter(a -> a.getDefinition().getName().contains("Uncut")).results()), 60000);
        }
    }
    // separate function to prevent code repetition
    private boolean manuallyDrop(SpriteItemQueryResults itemsToDrop) {
        if (!(itemsToDrop == null || itemsToDrop.isEmpty())) {
            if (InterfaceWindows.getInventory().isOpen()) {
                for (SpriteItem item : itemsToDrop) {
                    bot.updateInfo("Dropping " + item.getDefinition().getName());
                    // drop them
                    if (item.interact("Drop")) {
                        // wait until the player is ready to drop another or until 3 seconds have passed
                        Execution.delayUntil(() -> !item.isValid(), 3000);
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
    // separate function for consistency, simplicity, and so i could end it if it takes to long (with execution)
    private boolean actionBarDrop(ActionBarQueryResults itemsToDrop) {
        if (!(itemsToDrop == null || itemsToDrop.isEmpty())) {
            for (int i = 0; i < itemsToDrop.size(); i++) {
                if (itemsToDrop.get(i) != null) {
                    bot.updateInfo("Dropping " + itemsToDrop.get(i).getName());
                    Keyboard.typeKey(itemsToDrop.get(i).getKeyBind());
                }
            }
        }
        return true;
    }
}