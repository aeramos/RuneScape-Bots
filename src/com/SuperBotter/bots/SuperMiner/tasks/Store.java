package com.SuperBotter.bots.SuperMiner.tasks;

import com.SuperBotter.bots.SuperMiner.SuperMiner;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.task.Task;

// had to name it Store instead of Bank to prevent conflicts with RuneMate's Bank (in the API)
public class Store extends Task {
    private SuperMiner bot;

    public Store(SuperMiner bot) {
        this.bot = bot;
    }

    @Override
    public boolean validate() {
        return Inventory.isFull() || Bank.isOpen();
    }
    @Override
    public void execute() {
        if (bot.getBankArea().contains(Players.getLocal())) {
            if (Bank.isOpen()) {
                if (Inventory.isFull()) {
                    bot.updateInfo("Depositing inventory");
                    Bank.depositInventory();
                } else {
                    bot.updateInfo("Closing " + bot.getBankName());
                    Bank.close();
                }
            } else {
                GameObject bankChest = GameObjects.newQuery().names(bot.getBankType()).results().nearest();
                if (bankChest != null) {
                    if (!bankChest.isVisible()) {
                        bot.updateInfo("Turning to face " + bot.getBankName());
                        Camera.turnTo(bankChest);
                    } else {
                        bot.updateInfo("Opening " + bot.getBankName());
                        Bank.open();
                    }
                }
            }
        } else {
            bot.updateInfo("Going to " + bot.getBankName());
            bot.goToArea(bot.getBankArea().getRandomCoordinate());
        }
    }
}