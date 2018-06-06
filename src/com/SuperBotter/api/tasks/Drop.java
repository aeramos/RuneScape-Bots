package com.SuperBotter.api.tasks;

import com.SuperBotter.api.Globals;
import com.SuperBotter.api.ProtectedItems;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceWindows;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.queries.results.ActionBarQueryResults;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.task.Task;

import java.util.Objects;

public class Drop extends Task {
    private LoopingBot bot;
    private Globals globals;
    private ProtectedItems protectedItems;

    public Drop(LoopingBot bot, Globals globals, ProtectedItems protectedItems) {
        this.bot = bot;
        this.globals = globals;
        this.protectedItems = protectedItems;
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
        bot.setLoopDelay(50, 100);
        // scan the inv for stuff to drop
        SpriteItemQueryResults inventory = Inventory.newQuery().results();
        Keyboard.type(" ", false); // press space just in case the full inventory prompt is in the chatbox
        if (inventory != null) {
            for (int i = 0; i < inventory.size(); i++) {
                SpriteItem item = inventory.get(i);
                if (item != null) {
                    String itemName = item.getDefinition().getName();
                    boolean dontDrop = false;
                    if (protectedItems.getNumberOfItems() > 0) {
                        for (int j = 0; j < protectedItems.getNumberOfItems(); j++) {
                            if (Objects.equals(itemName, protectedItems.getName(j))) {
                                dontDrop = true;
                                break; // no need to keep scanning the required items if we already found a match
                            }
                        }
                    }
                    if (!dontDrop) {
                        dropItem(item);
                        return; // leave the for loop after the item is dropped so the inventory gets refreshed
                    }
                }
            }
            // After it has iterated through all the items and nothing was dropped, its time to stop droppping
            globals.isDropping = false;
        }
    }

    private void dropItem(SpriteItem item) {
        String itemName = item.getDefinition().getName();
        if (Inventory.getQuantity(itemName) > 1) {
            ActionBarQueryResults actionBar = getActionBar(itemName);
            if (actionBar != null) {
                actionBarDrop(actionBar);
            } else {
                if (!moveToActionBar(item, itemName)) {
                    manuallyDrop(item, itemName);
                }
            }
        } else {
            manuallyDrop(item, itemName);
        }
    }

    private ActionBarQueryResults getActionBar(String itemName) {
        ActionBarQueryResults queryResults = ActionBar.newQuery().names(itemName).results();
        if (queryResults != null && queryResults.size() != 0 && queryResults.get(0) != null && queryResults.get(0).getActions().get(0).equals("Drop")) {
            return queryResults;
        } else {
            return null;
        }
    }

    private boolean moveToActionBar(SpriteItem item, String itemName) {
        ActionBarQueryResults emptySlots = ActionBar.newQuery().filled(false).empty(true).results();
        // if there are empty slots on the action bar
        if (emptySlots != null && emptySlots.size() != 0) {
            for (int i = 0; i < emptySlots.size(); i++) {
                // code to bypass actionbar bug
                // Link: https://www.runemate.com/community/threads/13693/
                if (emptySlots.get(i) != null && emptySlots.get(i).getName() == null) {
                    globals.currentAction = "Dragging " + itemName + " to Action Bar";
                    // drag item to Action Bar for no longer than 5 seconds
                    return Execution.delayUntil(() -> Mouse.drag(item, emptySlots.get(0).getBounds(), Mouse.Button.LEFT), 5000);
                }
            }
        }
        return false; // if it has reached this part of the code that means that there were no empty slots
    }

    private void manuallyDrop(SpriteItem itemToDrop, String itemName) {
        if (itemToDrop != null) {
            globals.currentAction = "Dropping " + itemName + " manually";
            if (InterfaceWindows.getInventory().isOpen()) {
                Execution.delayUntil(() -> itemToDrop.interact("Drop"));
            } else {
                // open the inventory
                InterfaceWindows.getInventory().open();
            }
        }
    }

    private void actionBarDrop(ActionBarQueryResults itemsToDrop) {
        if (itemsToDrop != null && !itemsToDrop.isEmpty()) {
            for (int i = 0; i < itemsToDrop.size(); i++) {
                if (itemsToDrop.get(i) != null) {
                    globals.currentAction = "Dropping " + itemsToDrop.get(i).getName() + " with the Action Bar";
                    String keybind = itemsToDrop.get(i).getKeyBind();
                    if (keybind != null) {
                        final int j = i; // delay can't use non-final variables
                        Keyboard.pressKey(keybind.codePointAt(0)); // hold the Action Bar keybind
                        Execution.delayUntil(() -> !Inventory.contains(itemsToDrop.get(j).getName()), 10000);
                        Keyboard.releaseKey(keybind.codePointAt(0));
                    } else {
                        final int j = i;
                        Execution.delayUntil(() -> {
                            for (int k = 0; k < Inventory.getQuantity(itemsToDrop.get(j).getName()); k++) {
                                if (Inventory.contains(itemsToDrop.get(j).getName())) {
                                    Execution.delay(100, 150);
                                    itemsToDrop.get(j).interact("Drop");
                                } else {
                                    return true;
                                }
                            }
                            return true;
                        }, 40000);
                    }
                }
            }
        }
    }
}
