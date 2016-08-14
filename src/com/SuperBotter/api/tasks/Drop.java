package com.SuperBotter.api.tasks;

import com.SuperBotter.api.Globals;
import com.runemate.game.api.hybrid.input.Keyboard;
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
    private String[] dontDrop;

    public Drop(Globals globals, String[] dontDrop) {
        this.globals = globals;
        this.dontDrop = dontDrop;
    }

    @Override
    public boolean validate() {
        // if the inventory is full
        return Inventory.isFull();
    }
    @Override
    public void execute() {
        Boolean droppingDone = false;
        while (!droppingDone) {
            Keyboard.type(" ", false); // press space just in case the full inv prompt is there
            SpriteItemQueryResults originalInv = Inventory.newQuery().results();
            for (int i = 0; i < originalInv.size(); i++) {
                Boolean canDrop = true;
                SpriteItemQueryResults currentInv = Inventory.newQuery().results();
                if (currentInv.size() <= i) {
                    i = 0;
                }
                String currentItemName = currentInv.get(i).getDefinition().getName();
                for (int j = 0; j < dontDrop.length; j++) {
                    if (Objects.equals(currentItemName, dontDrop[j])) {
                        canDrop = false;
                    }
                }
                if (canDrop) {
                    if (Objects.equals(currentInv.get(i).getDefinition().getInventoryActions().get(0), "Drop")) {
                        actionBarDrop(ActionBar.newQuery().names(currentItemName).results());
                    } else {
                        // manually drop for no more than a minute
                        manuallyDrop(Inventory.newQuery().names(currentItemName).results());
                    }
                }
                if (!Inventory.containsAnyExcept(dontDrop) || globals.botIsStopped) {
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
                        globals.currentAction = "Dropping " + item.getDefinition().getName();
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
                    globals.currentAction = "Dropping " + itemsToDrop.get(i).getName();
                    final int j = i; // delay can't use non-final variables
                    Keyboard.pressKey(itemsToDrop.get(i).getKeyBind().codePointAt(0)); // hold the Action Bar keybind
                    Execution.delayUntil(() -> !Inventory.contains(itemsToDrop.get(j).getName()), 30000);
                    Keyboard.releaseKey(itemsToDrop.get(i).getKeyBind().codePointAt(0));
                }
            }
        }
    }
}