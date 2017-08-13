package com.SuperBotter.api.ui;

import com.runemate.game.api.client.ClientUI;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.util.Resources;
import com.runemate.game.api.script.framework.core.BotPlatform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Java FX Gui for configuring SuperMiner bot settings before it starts
 */
public class Config extends GridPane implements Initializable {
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        setVisible(true);
    }

    public Config(Object configController, BotPlatform botPlatform, String botName) {
        // Load the fxml file using RuneMate's Resources class.
        FXMLLoader loader = new FXMLLoader();

        // Input your Settings GUI FXML file location here.
        // NOTE: DO NOT FORGET TO ADD IT TO MANIFEST AS A RESOURCE
        Future<InputStream> stream = botPlatform.invokeLater(() -> Resources.getAsStream("com/SuperBotter/api/ui/Config.fxml"));

        // Set FlaxFXController as the class that will be handling our events
        //loader.setController(new ConfigController(metaData, configSettings));
        loader.setController(configController);

        // Set the FXML load's root to this class
        loader.setRoot(this);

        try {
            loader.load(stream.get());
        } catch (IOException | InterruptedException | ExecutionException | NullPointerException e) {
            ClientUI.showAlert(botName + ": Unable to load GUI. Please restart the bot.", Color.RED);
            Environment.getBot().stop();
        }

    }
}