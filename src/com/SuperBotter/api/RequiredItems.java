package com.SuperBotter.api;

import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequiredItems {
    private List<String> names = new ArrayList<String>();
    private List<Integer> amounts = new ArrayList<Integer>();

    public RequiredItems() {}

    public RequiredItems(String name, Integer amount) {
        add(name, amount);
    }

    public RequiredItems(String[] names, Integer[] amounts) {
        add(names, amounts);
    }

    public void reset() {
        names = new ArrayList<String>();
        amounts = new ArrayList<Integer>();
    }

    public void add(String name, Integer amount) {
        if (amount >= 0) {
            for (int i = 0; i < names.size(); i++) {
                if (Objects.equals(names.get(i), name)) {
                    amounts.set(i, amounts.get(i) + amount);
                    return;
                }
            }
            names.add(name);
            amounts.add(amount);
        }
    }

    public void add(String[] names, Integer[] amounts) {
        for (int i = 0; i < names.length; i++) {
            add(names[i], amounts[i]);
        }
    }

    public String getName(int index) {
        return names.get(index);
    }

    public String[] getNames() {
        return names.toArray(new String[0]);
    }

    public Integer getAmount(int index) {
        return amounts.get(index);
    }

    public Integer[] getAmounts() {
        return amounts.toArray(new Integer[0]);
    }

    public boolean isEmpty() {
        return amounts.isEmpty();
    }

    public int getNumberOfItems() {
        return amounts.size();
    }
    public Integer[] getMissingItems() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < getNumberOfItems(); i++) {
            int amountOfItem = Inventory.getQuantity(getName(i));
            if (amountOfItem < getAmount(i) || amountOfItem == 0) {
                list.add(i);
            }
        }
        return list.toArray(new Integer[0]);
    }
}
