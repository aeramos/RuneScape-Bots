package com.SuperBotter.bots.SuperFisher.ui;

import com.SuperBotter.api.Banks;
import com.SuperBotter.bots.SuperFisher.SuperFisher;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *  Java FX Controller for the Config class
 *  The controller class is where the logic and implementation of GUI events go.
 *
 *  All settings are disabled until location is chosen
 *      Only the start button and the ore selector needs to be disabled, but this way makes the user keep on going down the list of options
 *  When location is chosen, ores at that mine are allowed to be chosen
 *  Then the user can decide if they want to bank the ores they mine or powermine
 *  Then the user can press start and the bot will begin, following the options they selected
 */
class ConfigController implements Initializable {
    private SuperFisher bot;

    // ComboBox
    @FXML
    private ComboBox Location_ComboBox;
    @FXML
    private ComboBox Item_ComboBox;

    // Start button
    @FXML
    private Button Start_BT;

    @FXML
    private RadioButton Bank_BT;
    @FXML
    private RadioButton Power_BT;

    @FXML
    private Text name_T, version_T, author_T;

    ConfigController(SuperFisher bot) {
        this.bot = bot;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name_T.textProperty().set(bot.getMetaData().getName());
        version_T.textProperty().set("Version " + bot.getMetaData().getVersion());
        author_T.textProperty().set("By " + bot.getMetaData().getAuthor());
        Item_ComboBox.promptTextProperty().set("Fish");
        Power_BT.textProperty().set("Powerfish");
        Location_ComboBox.getItems().addAll(
                "Lumbridge Church"
        );
        Start_BT.setOnAction(getStart_BTAction());
        Bank_BT.setOnAction(getBank_BTAction());
        Power_BT.setOnAction(getPower_BTAction());

        Location_ComboBox.setOnAction(getLocation_ComboBoxEvent());
        Item_ComboBox.setOnAction(getItem_ComboBoxEvent());
        bot.guiWait = false;
    }

    private EventHandler<ActionEvent> getStart_BTAction() {
        return event -> {
            try {
                bot.startButtonPressed = true;
                // Set the EmbeddableUI property to reflect your InfoController GUI
                Platform.runLater(() -> bot.setToInfoProperty());

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
    private EventHandler<ActionEvent> getBank_BTAction() {
        return event -> {
            try {
                Power_BT.setSelected(false);
                bot.dontDrop = true;
                Start_BT.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
    private EventHandler<ActionEvent> getPower_BTAction() {
        return event -> {
            try {
                Bank_BT.setSelected(false);
                bot.dontDrop = false;
                Start_BT.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
    private EventHandler<ActionEvent> getLocation_ComboBoxEvent(){
        return event ->{
            Item_ComboBox.getSelectionModel().clearSelection();
            Item_ComboBox.getItems().clear();
            Start_BT.setDisable(true);
            Bank_BT.setDisable(true);
            Power_BT.setDisable(true);
            Bank_BT.setSelected(false);
            Power_BT.setSelected(false);
            if(Location_ComboBox.getSelectionModel().getSelectedItem() != null) {
                switch(Location_ComboBox.getSelectionModel().getSelectedItem().toString()){
                    case "Lumbridge Church":
                        bot.botArea = new Area.Rectangular(new Coordinate(3256, 3203, 0), new Coordinate(3258, 3207, 0));
                        bot.bank = new Banks(Banks.BankName.COMBAT_ACADEMY);
                        Item_ComboBox.getItems().addAll("Raw crayfish");
                        break;
                }
                bot.botAreaName = Location_ComboBox.getSelectionModel().getSelectedItem().toString() + " mine";
                Item_ComboBox.setDisable(false);
            } else {
                Item_ComboBox.setDisable(true);
            }
        };
    }
    private EventHandler<ActionEvent> getItem_ComboBoxEvent() {
        return event -> {
            Start_BT.setDisable(true);
            Bank_BT.setSelected(false);
            Power_BT.setSelected(false);
            if(Item_ComboBox.getSelectionModel().getSelectedItem() != null) {
                bot.itemName = Item_ComboBox.getSelectionModel().getSelectedItem().toString();
                switch (bot.itemName) {
                    case "Raw crayfish":
                        bot.actionName = "Cage";
                        bot.actionIng = "Caging";
                        break;
                }
                Bank_BT.setDisable(false);
                Power_BT.setDisable(false);
            } else {
                Bank_BT.setDisable(true);
                Power_BT.setDisable(true);
            }
        };
    }
}
