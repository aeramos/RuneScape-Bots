package com.SuperBotter.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollectableItems {
    // Example use: Trout, Fishing spot, Lure, Luring, Lured, 20, ["Feathers"]
    private List<String> names = new ArrayList<>();
    private List<String> interactionNames = new ArrayList<>();
    private List<String> actionNames = new ArrayList<>();
    private List<String> actionings = new ArrayList<>();
    private List<String> pastTenses = new ArrayList<>();
    private List<Integer> amounts = new ArrayList<>();
    private List<ProtectedItems> protectedItems = new ArrayList<>();

    private List<Boolean> enableds = new ArrayList<>();

    public CollectableItems() {
    }

    public CollectableItems(String name, String interactionName, String actionName, String actioning, String pastTense, ProtectedItems protectedItems) {
        add(name, interactionName, actionName, actioning, pastTense, protectedItems);
    }

    public CollectableItems(String[] names, String[] interactionNames, String[] actionNames, String[] actionings, String[] pastTenses, ProtectedItems[] protectedItems) {
        add(names, interactionNames, actionNames, actionings, pastTenses, protectedItems);
    }

    public int size(boolean disabled) {
        if (disabled) {
            return names.size();
        } else {
            int size = 0;
            for (Boolean enabled : enableds) {
                if (enabled) {
                    size++;
                }
            }
            return size;
        }
    }

    public void add(String name, String interactionName, String actionName, String actioning, String pastTense, ProtectedItems protectedItems) {
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
        if (protectedItems == null) {
            protectedItems = new ProtectedItems();
        }
        this.protectedItems.add(protectedItems);
        enableds.add(true);
    }

    public void add(String[] names, String[] interactionNames, String[] actionNames, String[] actionings, String[] pastTenses, ProtectedItems[] protectedItems) {
        for (int i = 0; i < names.length; i++) {
            add(names[i], interactionNames[i], actionNames[i], actionings[i], pastTenses[i], protectedItems[i]);
        }
    }

    public void remove(int index) {
        names.remove(index);
        interactionNames.remove(index);
        actionNames.remove(index);
        actionings.remove(index);
        pastTenses.remove(index);
        amounts.remove(index);
        protectedItems.remove(index);
        enableds.remove(index);
    }

    public String[] getNames(boolean disabled) {
        if (disabled) {
            return names.toArray(new String[0]);
        } else {
            ArrayList<String> names = new ArrayList<>();
            for (int i = 0; i < this.names.size(); i++) {
                if (enableds.get(i)) {
                    names.add(this.names.get(i));
                }
            }
            return names.toArray(new String[0]);
        }
    }

    public String[] getInteractionNames(boolean disabled) {
        if (disabled) {
            return interactionNames.toArray(new String[0]);
        } else {
            ArrayList<String> interactionNames = new ArrayList<>();
            for (int i = 0; i < this.interactionNames.size(); i++) {
                if (enableds.get(i)) {
                    interactionNames.add(this.interactionNames.get(i));
                }
            }
            return interactionNames.toArray(new String[0]);
        }
    }

    public String[] getActionNames(boolean disabled) {
        if (disabled) {
            return actionNames.toArray(new String[0]);
        } else {
            ArrayList<String> actionNames = new ArrayList<>();
            for (int i = 0; i < this.actionNames.size(); i++) {
                if (enableds.get(i)) {
                    actionNames.add(this.actionNames.get(i));
                }
            }
            return actionNames.toArray(new String[0]);
        }
    }

    public String[] getActionings(boolean disabled) {
        if (disabled) {
            return actionings.toArray(new String[0]);
        } else {
            ArrayList<String> actionings = new ArrayList<>();
            for (int i = 0; i < this.actionings.size(); i++) {
                if (enableds.get(i)) {
                    actionings.add(this.actionings.get(i));
                }
            }
            return actionings.toArray(new String[0]);
        }
    }

    public Integer[] getAmounts(boolean disabled) {
        if (disabled) {
            return amounts.toArray(new Integer[0]);
        } else {
            ArrayList<Integer> amounts = new ArrayList<>();
            for (int i = 0; i < this.amounts.size(); i++) {
                if (enableds.get(i)) {
                    amounts.add(this.amounts.get(i));
                }
            }
            return amounts.toArray(new Integer[0]);
        }
    }

    public String[] getPastTenses(boolean disabled) {
        if (disabled) {
            return pastTenses.toArray(new String[0]);
        } else {
            ArrayList<String> pastTenses = new ArrayList<>();
            for (int i = 0; i < this.pastTenses.size(); i++) {
                if (enableds.get(i)) {
                    pastTenses.add(this.pastTenses.get(i));
                }
            }
            return pastTenses.toArray(new String[0]);
        }
    }

    public ProtectedItems[] getProtectedItems(boolean disabled) {
        if (disabled) {
            return protectedItems.toArray(new ProtectedItems[0]);
        } else {
            ArrayList<ProtectedItems> protectedItems = new ArrayList<>();
            for (int i = 0; i < this.protectedItems.size(); i++) {
                if (enableds.get(i)) {
                    protectedItems.add(this.protectedItems.get(i));
                }
            }
            return protectedItems.toArray(new ProtectedItems[0]);
        }
    }

    public Boolean[] getEnableds() {
        return enableds.toArray(new Boolean[0]);
    }

    public void setEnabled(int index, Boolean value) {
        enableds.set(index, value);
    }

    public void addAmount(int index, int amount) {
        amounts.set(index, amounts.get(index) + amount);
    }

    public Integer getIndexByInteractionName(String name, boolean disabled) {
        int index = 0;
        for (int i = 0; i < interactionNames.size(); i++) {
            if (name.equals(interactionNames.get(i))) {
                return index;
            }

            if (enableds.get(i) || disabled) {
                index++;
            }
        }
        return null;
    }

    public Integer getIndexByItemName(String name, boolean disabled) {
        int index = 0;
        for (int i = 0; i < names.size(); i++) {
            if (name.equals(names.get(i))) {
                return index;
            }

            if (enableds.get(i) || disabled) {
                index++;
            }
        }
        return null;
    }
}
