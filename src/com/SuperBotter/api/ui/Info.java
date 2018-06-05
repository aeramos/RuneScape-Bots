package com.SuperBotter.api.ui;

import com.SuperBotter.api.ConfigSettings;
import com.SuperBotter.api.Methods;
import com.runemate.game.api.hybrid.util.Resources;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.script.framework.AbstractBot;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Info extends GridPane implements Initializable {
    private AbstractBot bot;
    private ConfigSettings configSettings;
    private InfoUpdate infoUpdate;
    private String itemType;

    @FXML
    private Text name_T, version_T, author_T, itemPerHourText, itemCountText, itemPerHour, itemCount, xpPerHour, xpGained, runtime, currentAction, itemSelectText;
    @FXML
    private ComboBox itemSelect;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name_T.textProperty().set(bot.getMetaData().getName());
        version_T.textProperty().set("Version " + bot.getMetaData().getVersion());
        author_T.textProperty().set("By " + bot.getMetaData().getAuthor());
        itemSelectText.setText(itemType + ": ");
        itemSelect.getItems().addAll(configSettings.interactableItems.getNames());
        itemSelect.setOnAction(event -> update(infoUpdate));
        itemSelect.getSelectionModel().select(0);
        setVisible(true);
    }

    public Info(AbstractBot bot, ConfigSettings configSettings, InfoUpdate infoUpdate, String itemType) {
        this.bot = bot;
        this.configSettings = configSettings;
        this.infoUpdate = infoUpdate;
        this.itemType = itemType;

        // Load the fxml file using RuneMate's Resources class.
        FXMLLoader loader = new FXMLLoader();
        // Input your Info FXML file location here.
        // NOTE: DO NOT FORGET TO ADD IT TO MANIFEST AS A RESOURCE
        Future<InputStream> stream = bot.getPlatform().invokeLater(() -> Resources.getAsStream("com/SuperBotter/api/ui/Info.fxml"));

        // Set this class as root AND Controller for the Java FX GUI
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load(stream.get());
        } catch (IOException | InterruptedException | ExecutionException | NullPointerException e) {
            Methods.shutdownBot(bot, "Unable to load GUI. Please restart the bot.", false);
        }
    }

    public void update(InfoUpdate infoUpdate) {
        this.infoUpdate = infoUpdate;
        int i = itemSelect.getSelectionModel().getSelectedIndex();
        itemCountText.setText(configSettings.interactableItems.getNames()[i] + " " + configSettings.interactableItems.getPastTenses()[i] + ": ");
        itemCount.setText(String.valueOf(configSettings.interactableItems.getAmounts()[i]));
        itemPerHourText.setText(configSettings.interactableItems.getNames()[i] + " per hour: ");
        itemPerHour.setText(String.valueOf((long)CommonMath.rate(TimeUnit.HOURS, infoUpdate.getRuntime(), configSettings.interactableItems.getAmounts()[i])));
        xpGained.setText(String.valueOf(infoUpdate.getXpDifference()));
        xpPerHour.setText(String.valueOf((long)CommonMath.rate(TimeUnit.HOURS, infoUpdate.getRuntime(), infoUpdate.getXpDifference())));
        runtime.setText(infoUpdate.getRuntimeAsString());
        currentAction.setText(infoUpdate.getCurrentAction());
    }
}