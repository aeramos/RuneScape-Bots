package com.SuperBotter.bots.SuperCookingGuildDoor;

import com.SuperBotter.api.Globals;
import com.SuperBotter.bots.SuperCookingGuildDoor.tasks.EnterDoor;
import com.SuperBotter.bots.SuperCookingGuildDoor.ui.Info;
import com.SuperBotter.bots.SuperCookingGuildDoor.ui.InfoController;
import com.runemate.game.api.client.embeddable.EmbeddableUI;
import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.task.TaskBot;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SuperCookingGuildDoor extends TaskBot implements EmbeddableUI{
    public long clickCount = 0;

    private StopWatch stopWatch = new StopWatch();

    // GUI variables
    public InfoController infoController;
    private Info info;
    private SimpleObjectProperty<Node> botInterfaceProperty;
    private ScheduledExecutorService executor;

    public Globals globals;

    public Player player;

    public SuperCookingGuildDoor() {
        globals = new Globals();
        executor = Executors.newScheduledThreadPool(1);
        setEmbeddableUI(this);
    }
    @Override
    public ObjectProperty<? extends Node> botInterfaceProperty() {
        if (botInterfaceProperty == null) {
            botInterfaceProperty = new SimpleObjectProperty<>(info = new Info(this));
        }
        return botInterfaceProperty;
    }
    // This method is used to update the GUI thread from the bot thread
    private Runnable updateInfo = new Runnable() {
        public void run() {
            try {
                // Assign all values to a new instance of the InfoController class
                infoController = new InfoController(
                        (long) CommonMath.rate(TimeUnit.HOURS, stopWatch.getRuntime(), clickCount), // Ore per hour
                        clickCount,                                                                 // Ore mined
                        stopWatch.getRuntimeAsString(),                                           // Total Runtime
                        globals.currentAction);                                                   // What its doing now

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Be sure to run info.update() through runLater.
            // This will run info.update() on the dedicated JavaFX thread which is the only thread allowed to update
            // anything related to JavaFX rendering
            if (info != null) {
                Platform.runLater(() -> info.update());
            }
        }
    };
    @Override
    public void onStart(String... args) {
        stopWatch.reset();
        GameEvents.Universal.UNEXPECTED_ITEM_HANDLER.disable();
        setLoopDelay(0);
        Execution.delayUntil(() -> info != null);
        executor.scheduleAtFixedRate(updateInfo, 0, 1, TimeUnit.SECONDS);
        player = Players.getLocal();
        add(new EnterDoor(this, new Area.Circular(new Coordinate(3143, 3443, 0), 2)));
        // don't add the time it takes to get everything ready
        stopWatch.start();
    }
    @Override
    public void onPause() {
        stopWatch.stop();
        globals.currentAction = "Paused";
    }
    @Override
    public void onResume() {
        stopWatch.start();
        globals.currentAction = "Resuming";
    }
    @Override
    public void onStop() {
        stopWatch.stop();
        globals.currentAction = "Stopped";
        globals.botIsStopped = true;
        executor.shutdown();
    }
}