package com.SuperBotter.bots.SuperMiner.ui;

import com.SuperBotter.api.ui.InfoController;
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
 *  InfoController GUI for the SuperMiner Bot
 *
 *  This will show various live stats on the bot
 *      (updated every time updateInfo() is run, which is run at the end of each task
 */
public class Info extends GridPane implements Initializable {

    private SuperMiner bot;

    @FXML
    private Text name_T, version_T, author_T, itemPerHourLabel_T, itemCountLabel_T, itemPerHour_T, itemCount_T, xpPerHour_T, xpGained_T, runtime_T, currentAction_T;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name_T.textProperty().set(bot.getMetaData().getName());
        version_T.textProperty().set("Version " + bot.getMetaData().getVersion());
        author_T.textProperty().set("By " + bot.getMetaData().getAuthor());
        itemPerHourLabel_T.textProperty().set(bot.oreName + " per hour: ");
        itemCountLabel_T.textProperty().set(bot.oreName + " mined: ");
        setVisible(true);
    }

    // An object property is a container of an object, which can be added
    // listeners to. In this case the property contains our controller class
    // (this)

    public Info(SuperMiner bot) {
        this.bot = bot;

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
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // This method will update the text that is presented to the end user
    public void update() {
        try {
            InfoController i = bot.infoController;

            itemPerHour_T.textProperty().set("" + i.itemPerHour);
            itemCount_T.textProperty().set("" + i.itemCount);
            xpPerHour_T.textProperty().set("" + i.xpPerHour);
            xpGained_T.textProperty().set("" + i.xpGained);
            runtime_T.textProperty().set("" + i.runtime);
            currentAction_T.textProperty().set(i.currentAction);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}