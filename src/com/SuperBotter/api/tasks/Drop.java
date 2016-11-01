package com.SuperBotter.api.tasks;

import com.SuperBotter.api.Globals;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceWindows;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.queries.results.ActionBarQueryResults;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;

import java.util.Objects;

public class Drop extends Task {
    private Globals globals;
    private String[] requiredItems;

    public Drop(Globals globals, String[] requiredItems) {
        this.globals = globals;
        this.requiredItems = requiredItems;
    }

    @Override
    public boolean validate() {
        // if the inventory is full
        return Inventory.isFull();
    }
    @Override
    public void execute() {
        boolean droppingDone = false;
        while (!droppingDone) {
            Keyboard.type(" ", false); // press space just in case the full inventory prompt is in the chatbox
            SpriteItemQueryResults originalInv = Inventory.newQuery().results();
            for (int i = 0; i < originalInv.size(); i++) {
                boolean canDrop = true;
                SpriteItemQueryResults currentInv = Inventory.newQuery().results();
                if (currentInv.size() <= i) {
                    i = 0;
                }
                // if the current item in the inventory is an item the bot needs, don't drop it
                String currentItemName = currentInv.get(i).getDefinition().getName();
                for (int j = 0; j < requiredItems.length; j++) {
                    if (Objects.equals(currentItemName, requiredItems[j])) {
                        canDrop = false;
                    }
                }
                if (canDrop) {
                    // if the first inventory action is "Drop" (can drop with the action bar keybind)
                    if (Objects.equals(currentInv.get(i).getDefinition().getInventoryActions().get(0), "Drop")) {
                        ActionBarQueryResults initialItemOnBar = ActionBar.newQuery().names(currentItemName).results();
                        // if the item is not on the action bar
                        if (initialItemOnBar == null || initialItemOnBar.size() == 0) {
                            ActionBarQueryResults emptySlots = ActionBar.getEmptySlots();
                            // if there are empty slots on the action bar
                            if (emptySlots != null && emptySlots.size() != 0 && emptySlots.get(0) != null) {
                                globals.currentAction = "Dragging " + currentItemName + " to Action Bar";
                                final int j = i;
                                // drag item to Action Bar for no longer than 5 seconds
                                Execution.delayUntil(() -> Mouse.drag(currentInv.get(j), emptySlots.get(0).getBounds(), Mouse.Button.LEFT), 5000);
                            }
                        }
                        ActionBarQueryResults itemOnBar = ActionBar.newQuery().names(currentItemName).results();
                        // if the item is on the action bar
                        if (itemOnBar != null && itemOnBar.size() != 0) {
                            // drop it with the action bar
                            actionBarDrop(itemOnBar);
                        }
                    }
                    SpriteItemQueryResults itemInInventory = Inventory.newQuery().names(currentItemName).results();
                    // if the item is still in the inventory (it never got on the action bar or the drop failed)
                    if (itemInInventory != null && itemInInventory.size() != 0) {
                        // manually drop it
                        manuallyDrop(itemInInventory);
                    }
                }
                if (!Inventory.containsAnyExcept(requiredItems) || globals.botIsStopped) {
                    droppingDone = true;
                    break;
                }
            }
        }
    }
    private void manuallyDrop(SpriteItemQueryResults itemsToDrop) {
        if (!(itemsToDrop == null || itemsToDrop.isEmpty())) {
            if (InterfaceWindows.getInventory().isOpen()) {
                Execution.delayUntil(() -> {
                    for (SpriteItem item : itemsToDrop) {
                        globals.currentAction = "Dropping " + item.getDefinition().getName() + " manually";
                        // drop them
                        if (item.interact("Drop")) {
                            // wait until the player is ready to drop another or until 3 seconds have passed
                            Execution.delayUntil(() -> !item.isValid(), 3000);
                        }
                    }
                    return true;
                }, 60000);
                // if the inventory is closed
            } else {
                // open the inventory
                InterfaceWindows.getInventory().open();
            }
        }
    }
    private void actionBarDrop(ActionBarQueryResults itemsToDrop) {
        if (!(itemsToDrop == null || itemsToDrop.isEmpty())) {
            for (int i = 0; i < itemsToDrop.size(); i++) {
                if (itemsToDrop.get(i) != null) {
                    globals.currentAction = "Dropping " + itemsToDrop.get(i).getName() + " with the Action Bar";
                    String keybind = itemsToDrop.get(i).getKeyBind();
                    if (keybind != null) {
                        final int j = i; // delay can't use non-final variables
                        Keyboard.pressKey(keybind.codePointAt(0)); // hold the Action Bar keybind
                        Execution.delayUntil(() -> !Inventory.contains(itemsToDrop.get(j).getName()), 30000);
                        Keyboard.releaseKey(keybind.codePointAt(0));
                    }
                }
            }
        }
    }
}