package com.SuperBotter.api.tasks;

import com.SuperBotter.api.CollectableItems;
import com.SuperBotter.api.Globals;
import com.SuperBotter.api.ProtectedItems;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.queries.results.ActionBarQueryResults;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.task.Task;

public class Drop extends Task {
    private final LoopingBot bot;
    private final Globals globals;
    private final ProtectedItems protectedItems;
    private final CollectableItems collectableItems;

    public Drop(LoopingBot bot, Globals globals, ProtectedItems protectedItems, CollectableItems collectableItems) {
        this.bot = bot;
        this.globals = globals;
        this.protectedItems = protectedItems;
        this.collectableItems = collectableItems;
    }

    @Override
    public boolean validate() {
        if (globals.isDropping || Inventory.isFull()) {
            globals.isDropping = true;
            return true;
        } else {
            return false;
        }
    }

    /*  Iterate through inventory items
        If it is not a protected item, drop + break (end task & restart)
    */
    @Override
    public void execute() {
        bot.setLoopDelay(0);
        // scan the inv for stuff to drop
        SpriteItemQueryResults inventory = Inventory.getItems();
        Keyboard.type(" ", false); // press space just in case the full inventory prompt is in the chatbox
        for (int i = 0; i < inventory.size(); i++) {
            SpriteItem item = inventory.get(i);
            if (item != null) {
                String itemName = item.getDefinition().getName();
                boolean dontDrop = false;
                for (int j = 0; j < protectedItems.size(); j++) {
                    if (itemName.equals(protectedItems.getName(j))) {
                        dontDrop = true;
                        break;
                    }
                }
                if (!dontDrop) {
                    for (ProtectedItems itemProtected : collectableItems.getProtectedItems(false)) {
                        for (int j = 0; j < itemProtected.size(); j++) {
                            if (itemName.equals(itemProtected.getName(j))) {
                                dontDrop = true;
                                break;
                            }
                        }
                    }
                }
                if (!dontDrop) {
                    dropItem(item, itemName);
                    return; // leave the for loop after the item is dropped so the inventory gets refreshed
                }
            }
        }
        // After it has iterated through all the items and nothing was dropped, its time to stop dropping
        globals.isDropping = false;
    }

    private void dropItem(SpriteItem item, String itemName) {
        ActionBar.Slot actionBar = getActionBar(itemName);
        if (actionBar != null) {
            actionBarDrop(actionBar, item);
        } else if (Inventory.getQuantity(itemName) <= 2) {
            manuallyDrop(item, itemName);
        } else {
            if (!moveToActionBar(item, itemName)) {
                manuallyDrop(item, itemName);
            }
        }
    }

    private ActionBar.Slot getActionBar(String itemName) {
        ActionBarQueryResults queryResults = ActionBar.newQuery().names(itemName).results();
        if (queryResults != null && queryResults.size() != 0 && queryResults.get(0) != null) {
            return queryResults.get(0);
        } else {
            return null;
        }
    }

    private boolean moveToActionBar(SpriteItem item, String itemName) {
        ActionBarQueryResults emptySlots = ActionBar.newQuery().filled(false).empty(true).results();

        if (emptySlots != null && emptySlots.size() != 0) {
            // code to bypass actionbar bug
            // Link: https://www.runemate.com/community/threads/13693/
            if (emptySlots.get(0) != null && emptySlots.get(0).getName() == null) {
                globals.currentAction = "Dragging " + itemName + " to Action Bar";
                // drag item to Action Bar for no longer than 5 seconds
                return Execution.delayUntil(() -> Mouse.drag(item, emptySlots.get(0).getBounds(), Mouse.Button.LEFT), 5000);
            }
        }
        return false; // if it has reached this part of the code that means that there were no empty slots
    }

    private void manuallyDrop(SpriteItem item, String itemName) {
        globals.currentAction = "Dropping " + itemName + " manually";
        Execution.delayUntil(() -> item.interact("Drop"), 4000);
    }

    private void actionBarDrop(ActionBar.Slot actionBar, SpriteItem item) {
        globals.currentAction = "Dropping " + actionBar.getName() + " with the Action Bar";
        String keybind = actionBar.getKeyBind();
        if (keybind != null && item.getDefinition().getInventoryActions().get(0).equals("Drop")) {
            Keyboard.pressKey(keybind.codePointAt(0)); // hold the Action Bar keybind
            Execution.delayUntil(() -> !Inventory.contains(actionBar.getName()), 10000);
            Keyboard.releaseKey(keybind.codePointAt(0));
        } else {
            for (int i = 0; i < Inventory.getQuantity(actionBar.getName()); i++) {
                Execution.delay(75);
                Execution.delayUntil(() -> actionBar.interact("Drop"), 1500);
            }
        }
    }
}
