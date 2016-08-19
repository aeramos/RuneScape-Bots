package com.SuperBotter.bots.SuperMiner;

import com.SuperBotter.api.Banks;
import com.SuperBotter.api.Globals;
import com.SuperBotter.api.Methods;
import com.SuperBotter.api.tasks.Drop;
import com.SuperBotter.api.tasks.NonMenuAction;
import com.SuperBotter.api.tasks.Store;
import com.SuperBotter.api.ui.InfoController;
import com.SuperBotter.bots.SuperMiner.ui.Config;
import com.SuperBotter.bots.SuperMiner.ui.Info;
import com.runemate.game.api.client.embeddable.EmbeddableUI;
import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.location.Area;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SuperMiner extends TaskScript implements EmbeddableUI, InventoryListener{
    // General variables and statistics
    private int startingXP = Skill.MINING.getExperience();

    public Area mineArea;
    public String mineName;
    public Boolean dontDrop;
    public String oreRockName;
    public String oreName;
    private long oreCount = 0;

    private StopWatch stopWatch = new StopWatch();

    // GUI variables
    public InfoController infoController;
    private Config config;
    private Info info;
    private SimpleObjectProperty<Node> botInterfaceProperty;
    public Boolean guiWait = true;
    public Boolean startButtonPressed = false;
    private ScheduledExecutorService executor;

    private Globals globals = new Globals();
    private Methods methods = new Methods();
    public Banks bank;

    public SuperMiner() {
        executor = Executors.newScheduledThreadPool(1);
        setEmbeddableUI(this);
    }
    @Override
    public ObjectProperty<? extends Node> botInterfaceProperty() {
        if (botInterfaceProperty == null) {
            botInterfaceProperty = new SimpleObjectProperty<>(config = new Config(this));
        }
        return botInterfaceProperty;
    }
    // When called, switch the botInterfaceProperty to reflect the Info
    public void setToInfoProperty(){
        info = new Info(this);
        botInterfaceProperty.set(info);
        executor.scheduleAtFixedRate(updateInfo, 0, 1, TimeUnit.SECONDS);
    }
    // This method is used to update the GUI thread from the bot thread
    private Runnable updateInfo = new Runnable() {
        public void run() {
            try {
                // Assign all values to a new instance of the InfoController class
                infoController = new InfoController(
                        (long) CommonMath.rate(TimeUnit.HOURS, stopWatch.getRuntime(), oreCount), // Ore per hour
                        oreCount,                                                                 // Ore mined
                        (long) CommonMath.rate(TimeUnit.HOURS, stopWatch.getRuntime(), (Skill.MINING.getExperience() - startingXP)),
                        (Skill.MINING.getExperience() - startingXP),
                        stopWatch.getRuntimeAsString(),                                           // Total Runtime
                        globals.currentAction);                                                           // What its doing now

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Be sure to run info.update() through runLater.
            // This will run info.update() on the dedicated JavaFX thread which is the only thread allowed to update
            // anything related to JavaFX rendering
            Platform.runLater(() -> info.update());

        }
    };
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
        add(new NonMenuAction(globals, methods, mineArea, mineName, oreName, oreRockName, "Mine", "Mining"));
        if (dontDrop) {
            add(new Store(globals, methods, bank, new String[0]));
        } else {
            add(new Drop(globals, new String[0]));
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
        globals.botIsStopped = true;
        executor.shutdown();
    }
}