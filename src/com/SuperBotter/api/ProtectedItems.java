package com.SuperBotter.api;

import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ProtectedItems {
    private List<String> names = new ArrayList<String>();
    private List<Integer> amounts = new ArrayList<Integer>();
    private List<Status> statuses = new ArrayList<Status>();

    public enum Status {
        // HELD -     Just hold in inventory
        // SAVED -    Bank
        // WANTED -   Don't drop + withdraw from bank if available
        // REQUIRED - Don't drop + withdraw from bank if available + if its not available, stop the bot
        HELD, SAVED, WANTED, REQUIRED
    }

    public ProtectedItems() {
    }

    public ProtectedItems(String name, Integer amount, Status status) {
        add(name, amount, status);
    }

    public ProtectedItems(String[] names, int[] amounts, Status[] statuses) {
        add(names, amounts, statuses);
    }

    public void reset() {
        names = new ArrayList<String>();
        amounts = new ArrayList<Integer>();
        statuses = new ArrayList<Status>();
    }

    public void add(String name, int amount, Status status) {
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

    public void add(String[] names, int[] amounts, Status[] statuses) {
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

    public String[] getNames(Pattern regex) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < getNumberOfItems(); i++) {
            // if there is a match, add it to the list
            if (regex.matcher(names.get(i)).find()) {
                list.add(names.get(i));
            }
        }
        return list.toArray(new String[0]);
    }

    public String[] getNames(Status... statuses) {
        ArrayList<String> list = new ArrayList<>();
        for (Status status : statuses) {
            for (int i = 0; i < names.size(); i++) {
                if (this.statuses.get(i) == status) {
                    list.add(names.get(i));
                }
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

    public Integer[] getAmounts(Integer[] indices) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int index : indices) {
            list.add(amounts.get(index));
        }
        return list.toArray(new Integer[0]);
    }

    public Status getStatus(int index) {
        return statuses.get(index);
    }

    public Status[] getStatuses() {
        return statuses.toArray(new Status[0]);
    }

    public Status[] getStatuses(Integer[] indices) {
        ArrayList<Status> list = new ArrayList<>();
        for (int index : indices) {
            list.add(statuses.get(index));
        }
        return list.toArray(new Status[0]);
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

    public Integer[] getIndices(Pattern regex) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < getNumberOfItems(); i++) {
            // if there is a match, add it to the list
            if (regex.matcher(names.get(i)).find()) {
                list.add(i);
            }
        }
        return list.toArray(new Integer[0]);
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

    public Integer[] getIndices(Status status) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i) == status) {
                list.add(i);
            }
        }
        return list.toArray(new Integer[0]);
    }

    // All items that can't be dropped that aren't in the inventory right now
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

    public Integer[] getMissingItems(Status... statuses) {
        ArrayList<Integer> list = new ArrayList<>();
        Integer[] missingItems = getMissingItems();
        for (int i = 0; i < missingItems.length; i++) {
            for (Status status : statuses) {
                if (this.statuses.get(missingItems[i]) == status) {
                    list.add(missingItems[i]);
                    break;
                }
            }
        }
        return list.toArray(new Integer[0]);
    }
}