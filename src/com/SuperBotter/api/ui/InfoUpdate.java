package com.SuperBotter.api.ui;

import com.runemate.game.api.hybrid.util.Time;

public class InfoUpdate {
    private long runtime;
    private Integer[] amounts;
    private long xpDifference;
    private String currentAction;

    public InfoUpdate(long runtime, Integer[] amounts, long xpDifference, String currentAction) {
        this.runtime = runtime;
        this.amounts = amounts;
        this.xpDifference = xpDifference;
        this.currentAction = currentAction;
    }

    public long getRuntime() {
        return runtime;
    }

    public String getRuntimeAsString() {
        return Time.format(runtime);
    }

    public int getAmount(int i) {
        return amounts[i];
    }

    public long getXpDifference() {
        return xpDifference;
    }

    public String getCurrentAction() {
        return currentAction;
    }
}
