package com.SuperBotter.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InteractableItems {
    // Example use: Trout, Fishing spot, Lure, Luring, Lured, 20
    private List<String> names = new ArrayList<>();
    private List<String> interactionNames = new ArrayList<>();
    private List<String> actionNames = new ArrayList<>();
    private List<String> actionings = new ArrayList<>();
    private List<String> pastTenses = new ArrayList<>();
    private List<Integer> amounts = new ArrayList<>();

    public InteractableItems() {}

    public InteractableItems(String name, String interactionName, String actionName, String actioning, String pastTense) {
        add(name, interactionName, actionName, actioning, pastTense);
    }

    public InteractableItems(String[] names, String[] interactionNames, String[] actionNames, String[] actionings, String[] pastTenses) {
        add(names, interactionNames, actionNames, actionings, pastTenses);
    }

    public int size() {
        return names.size();
    }

    public void clear() {
        names.clear();
        interactionNames.clear();
        actionNames.clear();
        actionings.clear();
        amounts.clear();
    }

    public void add(String name, String interactionName, String actionName, String actioning, String pastTense) {
        for (int i = 0; i < names.size(); i++) {
            if (Objects.equals(names.get(i), name)) {
                return;
            }
        }
        names.add(name);
        interactionNames.add(interactionName);
        actionNames.add(actionName);
        actionings.add(actioning);
        pastTenses.add(pastTense);
        amounts.add(0);
    }

    public void add(String[] names, String[] interactionNames, String[] actionNames, String[] actionings, String[] pastTenses) {
        for (int i = 0; i < names.length; i++) {
            add(names[i], interactionNames[i], actionNames[i], actionings[i], pastTenses[i]);
        }
    }

    public void remove(int index) {
        names.remove(index);
        interactionNames.remove(index);
        actionNames.remove(index);
        actionings.remove(index);
        pastTenses.remove(index);
        amounts.remove(index);
    }

    public String[] getNames() {
        return names.toArray(new String[0]);
    }

    public String[] getInteractionNames() {
        return interactionNames.toArray(new String[0]);
    }

    public String[] getActionNames() {
        return actionNames.toArray(new String[0]);
    }

    public String[] getActionings() {
        return actionings.toArray(new String[0]);
    }

    public Integer[] getAmounts() {
        return amounts.toArray(new Integer[0]);
    }

    public String[] getPastTenses() {
        return pastTenses.toArray(new String[0]);
    }

    public void addAmount(int index, int amount) {
        amounts.set(index, amounts.get(index) + amount);
    }

    public int getNumberOfItems() {
        return names.size();
    }

    public Integer getIndexByInteractionName(String name) {
        for (int i = 0; i < interactionNames.size(); i++) {
            if (Objects.equals(name, interactionNames.get(i))) {
                return i;
            }
        }
        return null;
    }

    public Integer getIndexByItemName(String name) {
        for (int i = 0; i < names.size(); i++) {
            if (Objects.equals(name, names.get(i))) {
                return i;
            }
        }
        return null;
    }
}
