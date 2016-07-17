package com.SuperBotter.bots.SuperMiner.ui;

import com.SuperBotter.bots.SuperMiner.SuperMiner;
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

import java.net.URL;
import java.util.ResourceBundle;

/**
 *  Java FX Controller for the FXGui class
 *  The controller class is where the logic and implementation of GUI events go.
 *
 *  All settings are disabled until location is chosen
 *      Only the start button and the ore selector needs to be disabled, but this way makes the user keep on going down the list of options
 *  When location is chosen, ores at that mine are allowed to be chosen
 *  Then the user can decide if they want to bank the ores they mine or powermine
 *  Then the user can press start and the bot will begin, following the options they selected
 */
class FXController implements Initializable {
    private SuperMiner bot;

    // ComboBox
    @FXML
    private ComboBox Location_ComboBox;
    @FXML
    private ComboBox Ore_ComboBox;

    // Start button
    @FXML
    private Button Start_BT;

    // Bank or Powermine
    private Boolean dropTypeHasBeenSelected = false;
    @FXML
    private RadioButton Bank_BT;
    @FXML
    private RadioButton Powermine_BT;

    FXController(SuperMiner bot) {
        this.bot = bot;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Location_ComboBox.getItems().addAll("Rimmington");

        Start_BT.setOnAction(getStart_BTAction());
        Bank_BT.setOnAction(getBank_BTAction());
        Powermine_BT.setOnAction(getPowermine_BTAction());

        Location_ComboBox.setOnAction(getLocation_ComboBoxEvent());
        Ore_ComboBox.setOnAction(getOre_ComboBoxEvent());
        bot.guiWait = false;
    }

    private EventHandler<ActionEvent> getStart_BTAction() {
        return event -> {
            try {
                bot.startButtonPressed = true;
                // Set the EmbeddableUI property to reflect your Info GUI
                Platform.runLater(() -> bot.setToInfoProperty());

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
    private EventHandler<ActionEvent> getBank_BTAction() {
        return event -> {
            try {
                dropTypeHasBeenSelected = true;
                bot.bank = true;
                Start_BT.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
    private EventHandler<ActionEvent> getPowermine_BTAction() {
        return event -> {
            try {
                dropTypeHasBeenSelected = true;
                bot.bank = false;
                Start_BT.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
    private EventHandler<ActionEvent> getLocation_ComboBoxEvent(){
        return event ->{
            if(Location_ComboBox.getSelectionModel().getSelectedItem() != null) {
                Ore_ComboBox.setDisable(false);
                switch(Location_ComboBox.getSelectionModel().getSelectedItem().toString()){
                    case "Rimmington":
                        bot.mineArea = new Area.Rectangular(new Coordinate(2981, 3242), new Coordinate(2964, 3229));
                        bot.bankArea = new Area.Circular(new Coordinate(2955, 3297), 5);
                        bot.bankName = "Clan Camp bank chest";
                        bot.bankType = "Bank chest";
                        Ore_ComboBox.getItems().addAll("Tin ore", "Copper ore", "Clay", "Gold ore", "Iron ore");
                        break;
                }
                bot.mineName = Location_ComboBox.getSelectionModel().getSelectedItem().toString() + " mine";
            } else {
                Start_BT.setDisable(true);
            }
        };
    }
    private EventHandler<ActionEvent> getOre_ComboBoxEvent() {
        return event -> {
            if(Ore_ComboBox.getSelectionModel().getSelectedItem() != null) {
                Bank_BT.setDisable(false);
                Powermine_BT.setDisable(false);
                bot.oreName = Ore_ComboBox.getSelectionModel().getSelectedItem().toString();
                bot.oreRockName = bot.oreName + " rocks";
            } else {
                Start_BT.setDisable(true);
            }
        };
    }
}
