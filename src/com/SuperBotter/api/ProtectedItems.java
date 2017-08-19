package com.SuperBotter.api;

import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ProtectedItems {
    private List<String> names = new ArrayList<String>();
    private List<Integer> amounts = new ArrayList<Integer>();

    // 0 = Don't drop
    // 1 = Don't drop + withdraw from bank if available
    // 2 = Don't drop + withdraw from bank if available + if its not available, stop the bot
    private List<Integer> statuses = new ArrayList<Integer>();

    public ProtectedItems() {}

    public ProtectedItems(String name, Integer amount, int status) {
        add(name, amount, status);
    }

    public ProtectedItems(String[] names, int[] amounts, int[] statuses) {
        add(names, amounts, statuses);
    }

    public void reset() {
        names = new ArrayList<String>();
        amounts = new ArrayList<Integer>();
        statuses = new ArrayList<Integer>();
    }

    public void add(String name, int amount, int status) {
        if (amount >= 0) {
            for (int i = 0; i < names.size(); i++) {
                if (Objects.equals(names.get(i), name)) {
                    amounts.set(i, amounts.get(i) + amount);
                    return;
                }
            }
            names.add(name);
            amounts.add(amount);
            statuses.add(status);
        }
    }

    public void add(String[] names, int[] amounts, int[] statuses) {
        for (int i = 0; i < names.length; i++) {
            add(names[i], amounts[i], statuses[i]);
        }
    }

    public void remove(int index) {
        names.remove(index);
        amounts.remove(index);
        statuses.remove(index);
    }

    public void remove(Integer[] indices) {
        for (int i = 0; i < indices.length; i++) {
            remove(i);
        }
    }

    public void remove(String name) {
        remove(getIndex(name));
    }

    public void remove(String[] names) {
        remove(getIndices(names));
    }

    public void remove(Pattern regex) {
        for (int i = 0; i < getNumberOfItems(); i++) {
            // if there is not a match, remove it
            if (regex.matcher(names.get(i)).find()) {
                remove(i);
            }
        }
    }

    public void removeAllExcept(int index) {
        for (int i = 0; i < getNumberOfItems(); i++) {
            if (i != index) {
                remove(i);
            }
        }
    }

    public void removeAllExcept(Integer[] indices) {
        for (int i = 0; i < getNumberOfItems(); i++) {
            for (int j = 0; j < indices.length; j++) {
                if (i != indices[j]) {
                    remove(i);
                }
            }
        }
    }

    public void removeAllExcept(Pattern regex) {
        for (int i = 0; i < getNumberOfItems(); i++) {
            // if there is not a match, remove it
            if (!regex.matcher(names.get(i)).find()) {
                remove(i);
            }
        }
    }

    public String getName(int index) {
        return names.get(index);
    }

    public String[] getNames() {
        return names.toArray(new String[0]);
    }

    public String[] getNames(Integer[] indices) {
        ArrayList<String> list = new ArrayList<String>();
        for (int j = 0; j < indices.length; j++) {
            if (indices[j] < getNumberOfItems()) {
                list.add(getName(indices[j]));
            }
        }
        return list.toArray(new String[0]);
    }

    public Integer getAmount(int index) {
        return amounts.get(index);
    }

    public Integer[] getAmounts() {
        return amounts.toArray(new Integer[0]);
    }

    public Integer getStatus(int index) {
        return statuses.get(index);
    }

    public Integer[] getStatuses() {
        return statuses.toArray(new Integer[0]);
    }

    public boolean isEmpty() {
        return statuses.isEmpty();
    }

    public int getNumberOfItems() {
        return statuses.size();
    }

    public Integer getIndex(String name) {
        for (int i = 0; i < names.size(); i++) {
            if (Objects.equals(name, names.get(i))) {
                return i;
            }
        }
        return null;
    }

    public Integer[] getIndices(String[] names) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < names.length; i++) {
            Integer index = getIndex(names[i]);
            if (index != null) {
                list.add(index);
            }
        }
        return list.toArray(new Integer[0]);
    }

    // All items that can't be dropped
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

    public Integer[] getRequiredItems() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i) == 2) {
                list.add(i);
            }
        }
        return list.toArray(new Integer[0]);
    }

    public Integer[] getMissingRequiredItems() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        Integer[] missingItems = getMissingItems();
        for (int i = 0; i < missingItems.length; i++) {
            if (statuses.get(missingItems[i]) == 2) {
                list.add(i);
            }
        }
        return list.toArray(new Integer[0]);
    }

    public Integer[] getWantedItems() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i) == 1) {
                list.add(i);
            }
        }
        return list.toArray(new Integer[0]);
    }

    public Integer[] getMissingWantedItems() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        Integer[] missingItems = getMissingItems();
        for (int i = 0; i < missingItems.length; i++) {
            if (statuses.get(missingItems[i]) == 1) {
                list.add(i);
            }
        }
        return list.toArray(new Integer[0]);
    }

    public Integer[] getProtectedItems() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i) == 0) {
                list.add(i);
            }
        }
        return list.toArray(new Integer[0]);
    }

    public Integer[] getMissingProtectedItems() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        Integer[] missingItems = getMissingItems();
        for (int i = 0; i < missingItems.length; i++) {
            if (statuses.get(missingItems[i]) == 0) {
                list.add(i);
            }
        }
        return list.toArray(new Integer[0]);
    }
}
