package com.SuperBotter.api.tasks;

import com.SuperBotter.api.Globals;
import com.SuperBotter.api.Methods;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.Task;

// had to name it Store instead of Bank to prevent conflicts with RuneMate's Bank (in the API)
public class Store extends Task {
    private Globals globals;
    private Area bankArea;
    private String[] dontDeposit;
    private String bankName, bankType;

    public Store(Globals globals, Area bankArea, String[] dontDeposit, String bankName, String bankType) {
        this.globals = globals;
        this.bankArea = bankArea;
        this.dontDeposit = dontDeposit;
        this.bankName = bankName;
        this.bankType = bankType;
    }

    @Override
    public boolean validate() {
        return Inventory.isFull() || Bank.isOpen();
    }
    @Override
    public void execute() {
        if (bankArea.contains(Players.getLocal())) {
            if (Bank.isOpen()) {
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
                } else {
                    globals.currentAction = "Closing " + bankName;
                    Bank.close();
                }
            } else {
                GameObject bankChest = GameObjects.newQuery().names(bankType).results().nearest();
                if (bankChest != null) {
                    if (!bankChest.isVisible()) {
                        globals.currentAction = "Turning to face " + bankName;
                        Camera.turnTo(bankChest);
                    } else {
                        globals.currentAction = "Opening " + bankName;
                        Bank.open();
                    }
                }
            }
        } else {
            globals.currentAction = "Going to " + bankName;
            Methods.goToArea(bankArea.getRandomCoordinate());
        }
    }
}