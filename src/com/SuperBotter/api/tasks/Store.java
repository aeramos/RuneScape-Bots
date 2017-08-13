package com.SuperBotter.api.tasks;

import com.SuperBotter.api.ConfigSettings;
import com.SuperBotter.api.Globals;
import com.SuperBotter.api.Methods;
import com.SuperBotter.api.RequiredItems;
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
    private RequiredItems requiredItems;

    public Store(LoopingBot bot, Globals globals, ConfigSettings configSettings, Methods methods, RequiredItems requiredItems) {
        this.bot = bot;
        this.globals = globals;
        this.configSettings = configSettings;
        this.methods = methods;
        this.requiredItems = requiredItems;
    }
    @Override
    public boolean validate() {
        return RuneScape.isLoggedIn() && (Inventory.isFull() || Bank.isOpen() || requiredItems.getMissingItems() != null);
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
                if (Inventory.isFull()) {
                    if (requiredItems.getNumberOfItems() > 0) {
                        // i could list all the stuff its depositing/not depositing, but the list would be too long and
                        // it might not fit in the runemate window if there's a lot of stuff, and this could make it look ugly
                        globals.currentAction = "Banking";
                        Execution.delayUntil(() -> Bank.depositAllExcept(requiredItems.getNames()), 30000);
                    } else {
                        globals.currentAction = "Depositing inventory";
                        Execution.delayUntil(() -> Bank.depositInventory(), 5000);
                    }
                } else if (requiredItems.getMissingItems().length != 0) {
                    globals.currentAction = "Banking";
                    Integer[] missingItems = requiredItems.getMissingItems();
                    for (int i = 0; i < missingItems.length; i++) {
                        String itemName = requiredItems.getName(missingItems[i]);
                        int amountToWithdraw = requiredItems.getAmount(missingItems[i]) - Inventory.getQuantity(itemName);
                        int amountInBank = Bank.getQuantity(itemName);

                        // if the banking bug occurs, update the amount in bank with the true amount
                        // Link: https://www.runemate.com/community/threads/13685/
                        if (Bank.getQuantity(requiredItems.getName(missingItems[i])) > amountInBank) {
                            amountInBank = Bank.getQuantity(itemName);
                        }
                        if (amountInBank >= amountToWithdraw && amountInBank != 0) {
                            globals.currentAction = "Withdrawing " + itemName + "from bank";
                            // bot has 10 seconds to withdraw the amount needed
                            Execution.delayUntil(() -> Bank.withdraw(itemName, amountToWithdraw), 10000);
                            break; // leave the for loop (one action per loop)
                        } else {
                            Methods.shutdownBot(globals, "Stopping bot - ran out of required items");
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