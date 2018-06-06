package com.SuperBotter.api.tasks;

import com.SuperBotter.api.Globals;
import com.SuperBotter.api.ProtectedItems;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.task.Task;

import java.util.regex.Pattern;

public class Urn extends Task {
    private LoopingBot bot;
    private Globals globals;
    private ProtectedItems protectedItems;

    private String baseUrnName;

    public Urn(LoopingBot bot, Globals globals, ProtectedItems protectedItems) {
        this.bot = bot;
        this.globals = globals;
        this.protectedItems = protectedItems;
    }

    @Override
    public boolean validate() {
        baseUrnName = protectedItems.getNames(Pattern.compile(" urn$"))[0];
        // if the inventory contains a full urn
        return Inventory.contains(baseUrnName + " (full)");
    }

    @Override
    public void execute() {
        bot.setLoopDelay(50, 100);
        globals.currentAction = "Teleporting" + baseUrnName;

        SpriteItem urn = Inventory.newQuery().names(baseUrnName + " (full)").results().get(0);
        if (urn != null) {
            if (urn.interact("Teleport urn")) {
                // now that the urn is done, get a new one from the bank
                protectedItems.add(baseUrnName + " (r)", 1, ProtectedItems.Status.WANTED);
            }
        }
    }
}
