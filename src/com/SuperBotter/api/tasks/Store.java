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

import java.util.ArrayList;

import static com.SuperBotter.api.Methods.concatenate;
import static com.SuperBotter.api.Methods.shutdownBot;

// had to name it Store instead of Bank to prevent conflicts with RuneMate's Bank (in the API)
public class Store extends Task {
    private final LoopingBot bot;
    private final Globals globals;
    private final ConfigSettings configSettings;
    private final Methods methods;
    private final ProtectedItems protectedItems;

    private ArrayList<Integer[]> missingItems;
    private ArrayList<Integer[]> amountsToWithdraw;
    private ArrayList<String[]> itemNames;
    private ArrayList<ProtectedItems.Status[]> missingStatuses;
    private boolean areMissingItems;

    public Store(LoopingBot bot, Globals globals, ConfigSettings configSettings, Methods methods, ProtectedItems protectedItems) {
        this.bot = bot;
        this.globals = globals;
        this.configSettings = configSettings;
        this.methods = methods;
        this.protectedItems = protectedItems;
        this.missingItems = new ArrayList<>();
        this.amountsToWithdraw = new ArrayList<>();
        this.itemNames = new ArrayList<>();
        this.missingStatuses = new ArrayList<>();
    }

    @Override
    public boolean validate() {
        this.missingItems.clear();
        this.amountsToWithdraw.clear();
        this.itemNames.clear();
        this.missingStatuses.clear();
        this.areMissingItems = false;

        Integer[] tempMissing = protectedItems.getMissingItems(ProtectedItems.Status.REQUIRED, ProtectedItems.Status.WANTED);
        if (tempMissing.length > 0) {
            areMissingItems = true;
        }
        missingItems.add(tempMissing);
        amountsToWithdraw.add(protectedItems.getAmounts(tempMissing));
        itemNames.add(protectedItems.getNames(tempMissing));
        missingStatuses.add(protectedItems.getStatuses(tempMissing));
        for (ProtectedItems itemProtected : configSettings.collectableItems.getProtectedItems(false)) {
            Integer[] tempItems = itemProtected.getMissingItems(ProtectedItems.Status.REQUIRED, ProtectedItems.Status.WANTED);
            if (tempItems.length > 0) {
                areMissingItems = true;
            }
            missingItems.add(tempItems);
            amountsToWithdraw.add(itemProtected.getAmounts(tempItems));
            itemNames.add(itemProtected.getNames(tempItems));
            missingStatuses.add(itemProtected.getStatuses(tempItems));
        }
        return RuneScape.isLoggedIn() && (Inventory.isFull() || Bank.isOpen() || areMissingItems);
    }

    @Override
    public void execute() {
        bot.setLoopDelay(50, 100);
        GameObject bankChest = GameObjects.newQuery().names(configSettings.bank.type).results().nearest();
        boolean bankIsOpen = false;
        if (bankChest != null && bankChest.isVisible()) {
            bankIsOpen = Execution.delayUntil(() -> Bank.open(), 5000);
        }
        if (configSettings.bank.area.contains(Players.getLocal()) || bankIsOpen) {
            globals.path = null; // the bot is no longer following this path, so it can be cleared
            if (bankIsOpen) {
                String[] cantBeBanked = protectedItems.getNames(ProtectedItems.Status.REQUIRED, ProtectedItems.Status.WANTED, ProtectedItems.Status.HELD);
                for (ProtectedItems itemProtected : configSettings.collectableItems.getProtectedItems(false)) {
                    cantBeBanked = concatenate(cantBeBanked, itemProtected.getNames(ProtectedItems.Status.REQUIRED, ProtectedItems.Status.WANTED, ProtectedItems.Status.HELD));
                }
                if (Inventory.containsAnyExcept(cantBeBanked)) {
                    if (Inventory.containsAnyOf(cantBeBanked)) {
                        globals.currentAction = "Depositing items";
                        final String[] cantBeBankedFinal = cantBeBanked;
                        Execution.delayUntil(() -> Bank.depositAllExcept(cantBeBankedFinal), 30000);
                    } else {
                        globals.currentAction = "Depositing inventory";
                        Execution.delayUntil(() -> Bank.depositInventory(), 5000);
                    }
                } else if (areMissingItems) {
                    globals.currentAction = "Banking";
                    for (int i = 0; i < missingItems.size(); i++) {
                        int arrayLength = missingItems.get(i).length;
                        for (int j = 0; j < arrayLength; j++) {
                            String itemName = itemNames.get(i)[j];
                            int amountToWithdraw = amountsToWithdraw.get(i)[j] - Inventory.getQuantity(itemName);
                            int amountInBank = Bank.getQuantity(itemName);

                            // if the banking bug occurs, update the amount in bank with the true amount
                            // Link: https://www.runemate.com/community/threads/13685/
                            if (Bank.getQuantity(itemName) > amountInBank) {
                                amountInBank = Bank.getQuantity(itemName);
                            }

                            if ((amountInBank >= amountToWithdraw && amountInBank > 0) || missingStatuses.get(i)[j] == ProtectedItems.Status.WANTED) {
                                if (itemName.contains(" urn (r)")) {
                                    protectedItems.remove(itemName);
                                    protectedItems.add(itemName, 0, ProtectedItems.Status.HELD);
                                    j--;
                                    arrayLength--;
                                }
                                globals.currentAction = "Withdrawing " + (amountToWithdraw > 0 ? amountToWithdraw + " " : "") + itemName + " from bank";
                                Execution.delayUntil(() -> Bank.withdraw(itemName, amountToWithdraw), 10000);
                            } else {
                                if (i == 0) {
                                    shutdownBot(bot, globals, "Ran out of required item: " + itemName, true);
                                } else {
                                    configSettings.collectableItems.setEnabled(i - 1, false);
                                    break;
                                }
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
