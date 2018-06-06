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

    private Integer[] missingItems;

    public Store(LoopingBot bot, Globals globals, ConfigSettings configSettings, Methods methods, ProtectedItems protectedItems) {
        this.bot = bot;
        this.globals = globals;
        this.configSettings = configSettings;
        this.methods = methods;
        this.protectedItems = protectedItems;
    }

    @Override
    public boolean validate() {
        missingItems = protectedItems.getMissingItems(new ProtectedItems.Status[]{ProtectedItems.Status.REQUIRED, ProtectedItems.Status.WANTED});
        return RuneScape.isLoggedIn() && (Inventory.isFull() || Bank.isOpen() || missingItems != null);
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
            globals.path = null; // the bot is no longer following this path, so it can be cleared
            if (bankIsOpen) {
                // REQUIRED, WANTED, and HELD items
                String[] cantBeBanked = Methods.concatenate(Methods.concatenate(protectedItems.getNames(protectedItems.getIndices(ProtectedItems.Status.REQUIRED)), protectedItems.getNames(protectedItems.getIndices(ProtectedItems.Status.WANTED))), protectedItems.getNames(protectedItems.getIndices(ProtectedItems.Status.HELD)));

                // if the inventory contains anything that CAN be banked
                if (Inventory.containsAnyExcept(cantBeBanked)) {
                    // if the inventory contains anything that CAN'T be banked, don't deposit all
                    if (Inventory.containsAnyOf(cantBeBanked)) {
                        globals.currentAction = "Banking";
                        // Bank everything except for the requiredItems and the wantedItems
                        Execution.delayUntil(() -> Bank.depositAllExcept(cantBeBanked), 30000);
                    } else {
                        globals.currentAction = "Depositing inventory";
                        Execution.delayUntil(() -> Bank.depositInventory(), 5000);
                    }
                } else if (missingItems.length != 0) {
                    globals.currentAction = "Banking";
                    for (int i = 0; i < missingItems.length; i++) {
                        String itemName = protectedItems.getName(missingItems[i]);
                        int amountToWithdraw = protectedItems.getAmount(missingItems[i]) - Inventory.getQuantity(itemName);
                        int amountInBank = Bank.getQuantity(itemName);

                        // if the banking bug occurs, update the amount in bank with the true amount
                        // Link: https://www.runemate.com/community/threads/13685/
                        if (Bank.getQuantity(itemName) > amountInBank) {
                            amountInBank = Bank.getQuantity(itemName);
                        }

                        if (amountInBank != 0) {
                            if (itemName.contains(" urn (r)")) {
                                protectedItems.remove(itemName);
                                protectedItems.add(itemName, 0, ProtectedItems.Status.HELD);
                            }
                            if (amountInBank >= amountToWithdraw) {
                                globals.currentAction = "Withdrawing " + amountToWithdraw + " " + itemName + " from bank";
                                // bot has 10 seconds to withdraw the amount needed
                                Execution.delayUntil(() -> Bank.withdraw(itemName, amountToWithdraw), 10000);
                                break; // leave the for loop (one action per loop)

                                // if the item was just wanted, take all that you can
                            } else {
                                final int amountInBank2 = amountInBank;
                                Execution.delayUntil(() -> Bank.withdraw(itemName, amountInBank2), 10000);
                            }

                            // if the item is required, stop the bot
                        } else if (protectedItems.getStatus(missingItems[i]) == ProtectedItems.Status.REQUIRED) {
                            Methods.shutdownBot(bot, globals, "Ran out of required items", true);
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
