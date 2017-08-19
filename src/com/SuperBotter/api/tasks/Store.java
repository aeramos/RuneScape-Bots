package com.SuperBotter.api.tasks;

import com.SuperBotter.api.ConfigSettings;
import com.SuperBotter.api.Globals;
import com.SuperBotter.api.Methods;
import com.SuperBotter.api.ProtectedItems;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.task.Task;

// had to name it Store instead of Bank to prevent conflicts with RuneMate's Bank (in the API)
public class Store extends Task {
    private LoopingBot bot;
    private Globals globals;
    private ConfigSettings configSettings;
    private Methods methods;
    private ProtectedItems protectedItems;

    public Store(LoopingBot bot, Globals globals, ConfigSettings configSettings, Methods methods, ProtectedItems protectedItems) {
        this.bot = bot;
        this.globals = globals;
        this.configSettings = configSettings;
        this.methods = methods;
        this.protectedItems = protectedItems;
    }
    @Override
    public boolean validate() {
        return RuneScape.isLoggedIn() && (Inventory.isFull() || Bank.isOpen() || protectedItems.getMissingRequiredItems() != null);
    }
    @Override
    public void execute() {
        bot.setLoopDelay(50, 200);
        GameObject bankChest = GameObjects.newQuery().names(configSettings.bank.type).results().nearest();
        boolean bankIsOpen = false;
        if (bankChest != null && bankChest.isVisible()) {
            bankIsOpen = Execution.delayUntil(() -> Bank.open(), 5000);
        }
        if (configSettings.bank.area.contains(Players.getLocal()) || bankIsOpen) {
            globals.path = null; // the bot is no longer following this path, so it can be reset
            if (bankIsOpen) {
                Integer[] requiredItems = protectedItems.getRequiredItems();
                String[] requiredNames = protectedItems.getNames(requiredItems);
                Integer[] wantedItems = protectedItems.getWantedItems();
                String[] wantedNames = protectedItems.getNames(wantedItems);
                if (Inventory.isFull()) {
                    // if the Inventory contains any items that we're not supposed to bank
                    if (Inventory.containsAnyOf(requiredNames) || Inventory.containsAnyOf(wantedNames)) {
                        globals.currentAction = "Banking";
                        // Bank everything except for the requiredItems and the wantedItems
                        Execution.delayUntil(() -> Bank.depositAllExcept(Methods.concatenate(requiredNames, wantedNames)), 30000);
                    } else {
                        globals.currentAction = "Depositing inventory";
                        Execution.delayUntil(() -> Bank.depositInventory(), 5000);
                    }
                } else if (protectedItems.getMissingRequiredItems().length != 0 || protectedItems.getMissingWantedItems().length != 0) {
                    globals.currentAction = "Banking";
                    Integer[] missingItems = Methods.concatenate(requiredItems, wantedItems);
                    for (int i = 0; i < missingItems.length; i++) {
                        String itemName = protectedItems.getName(missingItems[i]);
                        int amountToWithdraw = protectedItems.getAmount(missingItems[i]) - Inventory.getQuantity(itemName);
                        int amountInBank = Bank.getQuantity(itemName);

                        // if the banking bug occurs, update the amount in bank with the true amount
                        // Link: https://www.runemate.com/community/threads/13685/
                        if (Bank.getQuantity(protectedItems.getName(missingItems[i])) > amountInBank) {
                            amountInBank = Bank.getQuantity(itemName);
                        }
                        if (amountInBank >= amountToWithdraw && amountInBank != 0) {
                            globals.currentAction = "Withdrawing " + itemName + " from bank";
                            // bot has 10 seconds to withdraw the amount needed
                            Execution.delayUntil(() -> Bank.withdraw(itemName, amountToWithdraw), 10000);
                            break; // leave the for loop (one action per loop)

                            // if the item is required (status 2), stop the bot
                        } else if (protectedItems.getStatus(protectedItems.getIndex(itemName)) == 2){
                            Methods.shutdownBot(globals, "Stopping bot - ran out of required items");

                            // if the item was just wanted (status 1), take all that you can
                        } else {
                            if (amountInBank != 0) {
                                final int amountInBank2 = amountInBank;
                                Execution.delayUntil(() -> Bank.withdraw(itemName, amountInBank2), 10000);
                            }
                        }
                    }
                } else { // only the bank was open
                    globals.currentAction = "Closing " + configSettings.bank.name;
                    Execution.delayUntil(() -> Bank.close(), 5000);
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
                globals.path = methods.getPathTo(configSettings.bank.area.getRandomCoordinate(), Players.getLocal());
            }
            if (globals.path != null) {
                globals.path.step();
            }
        }
    }
}