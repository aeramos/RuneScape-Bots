package com.SuperBotter.bots.SuperMiner;

import com.SuperBotter.bots.SuperMiner.tasks.Drop;
import com.SuperBotter.bots.SuperMiner.tasks.Mine;
import com.SuperBotter.bots.SuperMiner.tasks.Store;
import com.SuperBotter.bots.SuperMiner.ui.FXGui;
import com.SuperBotter.bots.SuperMiner.ui.Info;
import com.SuperBotter.bots.SuperMiner.ui.InfoUI;
import com.runemate.game.api.client.embeddable.EmbeddableUI;
import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;
import com.runemate.game.api.script.framework.task.TaskScript;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

import java.util.concurrent.TimeUnit;

public class SuperMiner extends TaskScript implements EmbeddableUI, InventoryListener{
    // General variables and statistics
    private Area mineArea;
    public Area getMineArea() {
        return mineArea;
    }
    public void setMineArea(Area mineArea) {
        this.mineArea = mineArea;
    }

    private String mineName;
    public String getMineName() {
        return mineName;
    }
    public void setMineName(String mineName) {
        this.mineName = mineName;
    }

    private Boolean bank;
    public Boolean getBank() {
        return bank;
    }
    public void setBank(Boolean bank) {
        this.bank = bank;
    }

    private Area bankArea;
    public Area getBankArea() {
        return bankArea;
    }
    public void setBankArea(Area bankArea) {
        this.bankArea = bankArea;
    }

    private String bankName;
    public String getBankName() {
        return bankName;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    private String bankType;
    public String getBankType() {
        return bankType;
    }
    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    private String oreRockName;
    public String getOreRockName() {
        return oreRockName;
    }
    public void setOreRockName(String oreRockName) {
        this.oreRockName = oreRockName;
    }

    private String oreName;
    public String getOreName() {
        return oreName;
    }
    public void setOreName(String oreName) {
        this.oreName = oreName;
    }

    private long oreCount = 0;

    private StopWatch stopWatch = new StopWatch();

    // GUI variables
    public Info info;
    private FXGui configUI;
    private InfoUI infoUI;
    private SimpleObjectProperty<Node> botInterfaceProperty;
    public Boolean guiWait = true;
    public Boolean startButtonPressed = false;

    public SuperMiner() {
        setEmbeddableUI(this);
    }
    @Override
    public ObjectProperty<? extends Node> botInterfaceProperty() {
        if (botInterfaceProperty == null) {
            botInterfaceProperty = new SimpleObjectProperty<>(configUI = new FXGui(this));
            infoUI = new InfoUI(this);
        }
        return botInterfaceProperty;
    }
    // When called, switch the botInterfaceProperty to reflect the InfoUI
    public void setToInfoProperty(){
        botInterfaceProperty.set(infoUI);
    }
    // This method is used to update the GUI thread from the bot thread
    public void updateInfo(String currentAction) {
        try {
            // Assign all values to a new instance of the Info class
            info = new Info(
                    (long) CommonMath.rate(TimeUnit.HOURS, stopWatch.getRuntime(), oreCount), // Ore per hour
                    oreCount,                                                                 // Ore mined
                    stopWatch.getRuntimeAsString(),                                           // Total Runtime
                    currentAction);                                                           // What its doing now

        }catch(Exception e){
            e.printStackTrace();
        }

        // Be sure to run infoUI.update() through runLater.
        // This will run infoUI.update() on the dedicated JavaFX thread which is the only thread allowed to update
        // anything related to JavaFX rendering
        Platform.runLater(() -> infoUI.update());

    }
    @Override
    public void onItemAdded(ItemEvent event) {
        if (event != null) {
            ItemDefinition definition = event.getItem().getDefinition();
            if (definition != null) {
                if (oreName != null && definition.getName().contains(oreName)) {
                    oreCount++;
                }
            }
        }
    }
    @Override
    public void onStart(String... args) {
        stopWatch.reset();
        GameEvents.RS3.UNEXPECTED_ITEM_HANDLER.disable();
        getEventDispatcher().addListener(this);
        if (!Execution.delayUntil(() -> !guiWait, 60000)) {
            System.err.println("Still waiting for GUI after a minute, stopping.");
            stop();
            return;
        }
        Execution.delayUntil(() -> (startButtonPressed));
        setLoopDelay(100, 300); // in ms (1000ms = 1s)
        add(new Mine(this));
        if (bank) {
            add(new Store(this));
        } else {
            add(new Drop(this));
        }
        // there's no point in adding the time it takes for the user to config the bot
        stopWatch.start();
    }
    @Override
    public void onPause() {
        stopWatch.stop();
    }
    @Override
    public void onResume() {
        stopWatch.start();
    }
    @Override
    public void onStop() {
        stopWatch.stop();
        stopWatch.reset();
    }

    // run by Mine and Store to go to their different areas
    public void goToArea(Coordinate destination) {
        Path p;
        p = RegionPath.buildTo(destination);
        if (p == null) {
            WebPath wp = Traversal.getDefaultWeb().getPathBuilder().buildTo(destination);
            if (wp != null) {
                wp.step();
            }
        }
        // if Web path was done then p is still null and this will not run
        if(p != null) {
            p.step();
        }
    }
}