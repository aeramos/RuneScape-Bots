package com.SuperBotter.bots.SuperFisher;

import com.SuperBotter.api.ConfigSettings;
import com.SuperBotter.api.Globals;
import com.SuperBotter.api.Methods;
import com.SuperBotter.api.ProtectedItems;
import com.SuperBotter.api.tasks.Drop;
import com.SuperBotter.api.tasks.NonMenuAction;
import com.SuperBotter.api.tasks.Store;
import com.SuperBotter.api.tasks.Urn;
import com.SuperBotter.api.ui.Config;
import com.SuperBotter.api.ui.Info;
import com.SuperBotter.api.ui.InfoUpdate;
import com.SuperBotter.bots.SuperFisher.ui.ConfigController;
import com.runemate.game.api.client.ClientUI;
import com.runemate.game.api.client.embeddable.EmbeddableUI;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;
import com.runemate.game.api.script.framework.task.TaskBot;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class SuperFisher extends TaskBot implements EmbeddableUI, InventoryListener{
    // General variables and statistics
    private int startingXP;

    private StopWatch stopWatch = new StopWatch();

    // GUI variables
    private Info info;
    private SimpleObjectProperty<Node> botInterfaceProperty;
    private ScheduledExecutorService executor;

    private Globals globals;
    private Methods methods;
    private ConfigSettings configSettings;
    private ProtectedItems protectedItems;

    public SuperFisher() {
        startingXP = Skill.FISHING.getExperience();
        globals = new Globals();
        methods = new Methods();
        configSettings = new ConfigSettings();
        protectedItems = new ProtectedItems();
        executor = Executors.newScheduledThreadPool(1);
        setEmbeddableUI(this);
    }

    @Override
    public ObjectProperty<? extends Node> botInterfaceProperty() {
        if (botInterfaceProperty == null) {
            botInterfaceProperty = new SimpleObjectProperty<>(new Config(new ConfigController(getMetaData(), configSettings, protectedItems), getPlatform(), Environment.getBot()));
        }
        return botInterfaceProperty;
    }

    // When called, switch the botInterfaceProperty to reflect the Info
    private void setToInfoProperty(InfoUpdate infoUpdate) {
        info = new Info(this, configSettings, infoUpdate, "Fish");
        botInterfaceProperty.set(info);
    }

    @Override
    public void onItemAdded(ItemEvent event) {
        if (event != null) {
            ItemDefinition definition = event.getItem().getDefinition();
            if (definition != null) {
                if (configSettings.interactableItems != null) {
                    Integer interactableItemIndex = configSettings.interactableItems.getIndexByItemName(definition.getName());
                    if (interactableItemIndex != null) {
                        configSettings.interactableItems.addAmount(interactableItemIndex, 1);
                    }
                }
            }
        }
    }

    @Override
    public void onStart(String... args) {
        stopWatch.reset();
        try {
            GameEvents.Universal.UNEXPECTED_ITEM_HANDLER.disable();
        } catch (UnsupportedOperationException e) {
            ClientUI.showAlert(BotLogger.Level.SEVERE, getMetaData().getName() + ": Unexpected error. Please restart the bot.");
            stop(getMetaData().getName() + ": Unexpected error. Please restart the bot.");
            return;
        }
        getEventDispatcher().addListener(this);
        if (!Execution.delayUntil(() -> !configSettings.guiWait, 60000)) {
            ClientUI.showAlert(BotLogger.Level.SEVERE, getMetaData().getName() + ": Unable to load GUI. Please restart the bot.");
            stop(getMetaData().getName() + ": Unable to load GUI. Please restart the bot.");
            return;
        }
        Execution.delayUntil(() -> (configSettings.startButtonPressed));
        protectedItems.add("Strange rock", 0, ProtectedItems.Status.SAVED);

        final InfoUpdate infoUpdate = new InfoUpdate(stopWatch.getRuntime(), configSettings.interactableItems.getAmounts(), Skill.FISHING.getExperience() - startingXP, globals.currentAction);
        Platform.runLater(() -> setToInfoProperty(infoUpdate));
        executor.scheduleAtFixedRate(() -> {
            final InfoUpdate infoUpdate1 = new InfoUpdate(stopWatch.getRuntime(), configSettings.interactableItems.getAmounts(), Skill.FISHING.getExperience() - startingXP, globals.currentAction);
            getPlatform().invokeLater(() -> info.update(infoUpdate1));
        }, 0, 1, TimeUnit.SECONDS);

        setLoopDelay(0);

        // if urns are being used
        if (protectedItems.getNames(Pattern.compile(" urn")).length != 0) {
            add(new Urn((LoopingBot)Environment.getBot(), globals, protectedItems));
        }
        add(new NonMenuAction((LoopingBot)Environment.getBot(), globals, configSettings, methods, protectedItems));
        if (configSettings.dontDrop) {
            add(new Store((LoopingBot)Environment.getBot(), globals, configSettings, methods, protectedItems));
        } else {
            add(new Drop((LoopingBot)Environment.getBot(), globals, protectedItems));
        }

        // don't add the time it takes for the user to config the bot
        stopWatch.start();
    }
    @Override
    public void onPause() {
        stopWatch.stop();
        globals.currentAction = "Paused";
    }
    @Override
    public void onResume() {
        globals.currentAction = "Resuming";
        stopWatch.start();
    }
    @Override
    public void onStop() {
        stopWatch.stop();
        globals.botIsStopped = true;
        executor.shutdown();
        executor.shutdownNow();
    }
}