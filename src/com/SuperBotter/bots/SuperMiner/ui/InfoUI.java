package com.SuperBotter.bots.SuperMiner.ui;

import com.SuperBotter.bots.SuperMiner.SuperMiner;
import com.runemate.game.api.hybrid.util.Resources;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *  Info GUI for the SuperMiner Bot
 *
 *  This will show various live stats on the bot
 *      (updated every time updateInfo() is run, which is run at the end of each task
 */
public class InfoUI extends GridPane implements Initializable {

    private SuperMiner bot;

    @FXML
    private Text orePerHour_T, oreCount_T, xpPerHour_T, xpGained_T, Runtime_T, CurrentAction_T;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setVisible(true);
    }

    // An object property is a container of an object, which can be added
    // listeners to. In this case the property contains our controller class
    // (this)

    public InfoUI(SuperMiner bot) {
        this.bot = bot;

        // Load the fxml file using RuneMate's Resources class.
        FXMLLoader loader = new FXMLLoader();

        // Input your InfoUI FXML file location here.
        // NOTE: DO NOT FORGET TO ADD IT TO MANIFEST AS A RESOURCE
        Future<InputStream> stream = bot.getPlatform().invokeLater(() -> Resources.getAsStream("com/SuperBotter/bots/SuperMiner/ui/InfoUI.fxml"));

        // Set this class as root AND Controller for the Java FX GUI
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load(stream.get());
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // This method will update the text that is presented to the end user
    public void update() {
        try {
            Info i = bot.info;

            orePerHour_T.textProperty().set("" + i.orePerHour);
            oreCount_T.textProperty().set("" + i.oreCount);
            xpPerHour_T.textProperty().set("" + i.xpPerHour);
            xpGained_T.textProperty().set("" + i.xpGained);
            Runtime_T.textProperty().set("" + i.runTime);
            CurrentAction_T.textProperty().set(i.currentAction);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}