package com.SuperBotter.bots;

import com.SuperBotter.api.*;
import com.SuperBotter.api.tasks.Drop;
import com.SuperBotter.api.tasks.NonMenuAction;
import com.SuperBotter.api.tasks.Store;
import com.SuperBotter.api.tasks.Urn;
import com.SuperBotter.api.ui.Config;
import com.SuperBotter.api.ui.ConfigController;
import com.SuperBotter.api.ui.Info;
import com.SuperBotter.api.ui.InfoUpdate;
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

public class SuperBot extends TaskBot implements EmbeddableUI, InventoryListener {
    private final Location[] locations;
    private final String[] urns;
    private final String itemType;
    private final String power;
    private final String powerIng;
    private final int startingXP;
    private final StopWatch stopWatch = new StopWatch();

    private Info info;
    private SimpleObjectProperty<Node> botInterfaceProperty;
    private final ScheduledExecutorService executor;

    private final Globals globals;
    private final Methods methods;
    private final ConfigSettings configSettings;
    private final ProtectedItems protectedItems;

    private final Skill skill;

    public SuperBot(Skill skill, Location[] locations, String[] urns, String itemType, String power, String powerIng) {
        this.skill = skill;
        startingXP = skill.getExperience();
        this.locations = locations;
        this.urns = urns;
        this.itemType = itemType;
        this.power = power;
        this.powerIng = powerIng;
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
            botInterfaceProperty = new SimpleObjectProperty<>(new Config(new ConfigController(getMetaData(), configSettings, protectedItems, locations, urns, itemType, power, powerIng), getPlatform(), Environment.getBot()));
        }
        return botInterfaceProperty;
    }

    // When called, switch the botInterfaceProperty to reflect the Info
    private void setToInfoProperty(InfoUpdate infoUpdate) {
        info = new Info(this, configSettings, infoUpdate, itemType);
        botInterfaceProperty.set(info);
    }

    @Override
    public void onItemAdded(ItemEvent event) {
        if (event != null) {
            ItemDefinition definition = event.getItem().getDefinition();
            if (definition != null) {
                if (configSettings.collectableItems != null) {
                    Integer collectableItemIndex = configSettings.collectableItems.getIndexByItemName(definition.getName(), true);
                    if (collectableItemIndex != null) {
                        configSettings.collectableItems.addAmount(collectableItemIndex, 1);
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        globals.currentAction = "Resuming";
        stopWatch.start();
    }

    @Override
    public void onStop() {
        stopWatch.stop();
        executor.shutdown();
        executor.shutdownNow();
    }

    @Override
    public void onPause() {
        stopWatch.stop();
        globals.currentAction = "Paused";
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
        if (!Execution.delayUntil(() -> !configSettings.guiWait, 15000)) {
            ClientUI.showAlert(BotLogger.Level.SEVERE, getMetaData().getName() + ": Unable to load GUI. Please restart the bot.");
            stop(getMetaData().getName() + ": Unable to load GUI. Please restart the bot.");
            return;
        }
        Execution.delayUntil(() -> (configSettings.startButtonPressed));
        protectedItems.add("Strange rock", 0, ProtectedItems.Status.SAVED);

        final InfoUpdate infoUpdate = new InfoUpdate(stopWatch.getRuntime(), configSettings.collectableItems.getAmounts(true), skill.getExperience() - startingXP, globals.currentAction);
        Platform.runLater(() -> setToInfoProperty(infoUpdate));
        executor.scheduleAtFixedRate(() -> {
            final InfoUpdate infoUpdate1 = new InfoUpdate(stopWatch.getRuntime(), configSettings.collectableItems.getAmounts(true), skill.getExperience() - startingXP, globals.currentAction);
            getPlatform().invokeLater(() -> info.update(infoUpdate1));
        }, 0, 1, TimeUnit.SECONDS);

        setLoopDelay(0); // each Task sets its own loop delay in execute, so this will ensure that the bot gets started as soon as possible

        // if urns are being used
        if (protectedItems.getNames(Pattern.compile(" urn")).length != 0) {
            add(new Urn((LoopingBot)Environment.getBot(), globals, protectedItems));
        }
        add(new NonMenuAction((LoopingBot)Environment.getBot(), globals, configSettings, methods, protectedItems));
        if (configSettings.dontDrop) {
            add(new Store((LoopingBot)Environment.getBot(), globals, configSettings, methods, protectedItems));
        } else {
            add(new Drop((LoopingBot)Environment.getBot(), globals, protectedItems, configSettings.collectableItems));
        }

        // don't add the time it takes for the user to config the bot
        stopWatch.start();
    }
}
