package com.SuperBotter.api.tasks;

import com.SuperBotter.api.ConfigSettings;
import com.SuperBotter.api.Globals;
import com.SuperBotter.api.Methods;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;

// had to name it Store instead of Bank to prevent conflicts with RuneMate's Bank (in the API)
public class Store extends Task {
    private Globals globals;
    private ConfigSettings configSettings;
    private Methods methods;
    private String[] dontDeposit;

    public Store(Globals globals, ConfigSettings configSettings, Methods methods, String[] dontDeposit) {
        this.globals = globals;
        this.configSettings = configSettings;
        this.methods = methods;
        this.dontDeposit = dontDeposit;
    }
    @Override
    public boolean validate() {
        return Inventory.isFull() || Bank.isOpen() || Methods.getMissingRequiredItems(configSettings).length != 0;
    }
    @Override
    public void execute() {
        GameObject bankChest = GameObjects.newQuery().names(configSettings.bank.type).results().nearest();
        boolean bankIsOpen = false;
        if (bankChest != null && bankChest.isVisible()) {
            bankIsOpen = Execution.delayUntil(() -> Bank.open(), 5000);
        }
        if (configSettings.bank.area.contains(Players.getLocal()) || bankIsOpen) {
            globals.path = null; // the bot is no longer following this path, so it can be reset
            if (bankIsOpen) {
                if (Inventory.isFull()) {
                    if (dontDeposit.length != 0) {
                        // i could list all the stuff its depositing/not depositing, but the list would be too long and
                        // it might not fit in the runemate window if there's a lot of stuff, and this could make it look ugly
                        globals.currentAction = "Banking";
                        Execution.delayUntil(() -> Bank.depositAllExcept(dontDeposit), 30000);
                    } else {
                        globals.currentAction = "Depositing inventory";
                        Execution.delayUntil(() -> Bank.depositInventory(), 5000);
                    }
                } else if (Methods.getMissingRequiredItems(configSettings).length != 0) {
                    String[] missingItems = Methods.getMissingRequiredItems(configSettings);
                    Integer[] missingItemsAmounts = Methods.getMissingRequiredItemsAmount(configSettings);
                    for (int i = 0; i < missingItems.length; i++) {
                        int amountInBank = Bank.getQuantity(missingItems[i]);
                        if (amountInBank >= missingItemsAmounts[i]) {
                            if (amountInBank != 0) {
                                final int j = i;
                                // bot has 10 seconds to withdraw the amount needed
                                Execution.delayUntil(() -> Bank.withdraw(missingItems[j], missingItemsAmounts[j]), 10000);
                            } else {
                                Methods.shutdownBot(globals, "Stopping bot - ran out of required items");
                            }
                        } else {
                            Methods.shutdownBot(globals, "Stopping bot - ran out of required items");
                        }
                    }
                    for (int i = 0; i < configSettings.requiredItems.length; i++) {
                        if (Inventory.getQuantity(configSettings.requiredItems[i]) < configSettings.requiredItemsAmount[i]) {
                            Bank.withdraw(configSettings.requiredItems[i], configSettings.requiredItemsAmount[i]);
                        }
                    }
                } else { // only the bank was open
                    globals.currentAction = "Closing " + configSettings.bank.name;
                    Bank.close();
                }
            } else {
                if (bankChest != null) {
                    if (!bankChest.isVisible()) {
                        globals.currentAction = "Turning to face " + configSettings.bank.name;
                        Camera.turnTo(bankChest);
                    } else {
                        globals.currentAction = "Opening " + configSettings.bank.name;
                        Bank.open();
                    }
                }
            }
        } else {
            globals.currentAction = "Going to " + configSettings.bank.name;
            if (globals.path == null) {
                globals.path = methods.getPathTo(configSettings.botArea.getRandomCoordinate(), Players.getLocal());
            }
            if (globals.path != null) {
                globals.path.step();
            }
        }
    }
}