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
    private SuperMiner bot;

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

    ConfigController(SuperMiner bot) {
        this.bot = bot;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name_T.textProperty().set(bot.getMetaData().getName());
        version_T.textProperty().set("Version " + bot.getMetaData().getVersion());
        author_T.textProperty().set("By " + bot.getMetaData().getAuthor());
        Item_ComboBox.promptTextProperty().set("Ore");
        Power_BT.textProperty().set("Powermine");
        Location_ComboBox.getItems().addAll(
                "Draynor",
                "Falador south-west",
                "Lumbridge Swamp east",
                "Lumbridge Swamp west",
                "Rimmington",
                "Varrock south-east",
                "Varrock south-west"
        );
        Start_BT.setOnAction(getStart_BTAction());
        Bank_BT.setOnAction(getBank_BTAction());
        Power_BT.setOnAction(getPowermine_BTAction());

        Location_ComboBox.setOnAction(getLocation_ComboBoxEvent());
        Item_ComboBox.setOnAction(getOre_ComboBoxEvent());
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
                Bank_BT.setSelected(false);
                bot.bank = false;
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
                    /* Commented out until I figure out how to handle doors in a non mine specific way so that it works
                           for all mines that are behind doors, stairs, ladders, etc.
                    */
                    /*
                    case "Crafting Guild":
                        bot.mineArea = new Area.Rectangular(new Coordinate(2943, 3291, 0), new Coordinate(2937, 3276, 0));
                        bot.bankArea = new Area.Circular(new Coordinate(2955, 3297, 0), 5);
                        bot.bankName = "Clan Camp bank chest";
                        bot.bankType = "Bank chest";
                        Ore_ComboBox.getItems().addAll("Clay", "Silver ore", "Gold ore");
                        break;
                    */
                    case "Draynor":
                        bot.mineArea = new Area.Rectangular(new Coordinate(3138, 3315, 0), new Coordinate(3143, 3320, 0));
                        bot.bankArea = new Area.Circular(new Coordinate(3170, 3280, 0), 5);
                        bot.bankName = "Cabbage Facepunch Bonanza bank chest";
                        bot.bankType = "Bank chest";
                        Item_ComboBox.getItems().addAll("Clay");
                        break;
                    case "Falador south-west":
                        bot.mineArea = new Area.Rectangular(new Coordinate(2930, 3340, 0), new Coordinate(2922, 3334, 0));
                        bot.bankArea = new Area.Circular(new Coordinate(2955, 3297, 0), 5);
                        bot.bankName = "Clan Camp bank chest";
                        bot.bankType = "Bank chest";
                        Item_ComboBox.getItems().addAll("Copper ore", "Tin ore", "Iron ore", "Coal");
                        break;
                    case "Lumbridge Swamp east":
                        bot.mineArea = new Area.Rectangular(new Coordinate(3233, 3151, 0), new Coordinate(3223, 3145, 0));
                        bot.bankArea = new Area.Rectangular(new Coordinate(3272, 3168, 0), new Coordinate(3268, 3161, 0));
                        bot.bankName = "Al Kharid bank";
                        bot.bankType = "Bank booth";
                        Item_ComboBox.getItems().addAll("Copper ore", "Tin ore");
                        break;
                    case "Lumbridge Swamp west":
                        bot.mineArea = new Area.Rectangular(new Coordinate(3149, 3152, 0), new Coordinate(3144, 3144, 0));
                        bot.bankArea = new Area.Rectangular(new Coordinate(3097, 3246, 0), new Coordinate(3092, 3240, 0));
                        bot.bankName = "Draynor bank";
                        bot.bankType = "Counter";
                        Item_ComboBox.getItems().addAll("Coal", "Mithril ore", "Adamantite ore");
                        break;
                    case "Rimmington":
                        bot.mineArea = new Area.Rectangular(new Coordinate(2981, 3242, 0), new Coordinate(2964, 3229, 0));
                        bot.bankArea = new Area.Circular(new Coordinate(2955, 3297, 0), 5);
                        bot.bankName = "Clan Camp bank chest";
                        bot.bankType = "Bank chest";
                        Item_ComboBox.getItems().addAll("Copper ore", "Tin ore", "Clay", "Gold ore", "Iron ore");
                        break;
                    case "Varrock south-east":
                        bot.mineArea = new Area.Rectangular(new Coordinate(3280, 3361, 0), new Coordinate(3291, 3371, 0));
                        bot.bankArea = new Area.Rectangular(new Coordinate(3250, 3420, 0), new Coordinate(3257, 3423, 0));
                        bot.bankName = "Varrock east bank";
                        bot.bankType = "Bank booth";
                        Item_ComboBox.getItems().addAll("Copper ore", "Tin ore", "Iron ore");
                        break;
                    case "Varrock south-west":
                        bot.mineArea = new Area.Rectangular(new Coordinate(3171, 3364, 0), new Coordinate(3188, 3380, 0));
                        bot.bankArea = new Area.Rectangular(new Coordinate(3182, 3433, 0), new Coordinate(3189, 3446, 0));
                        bot.bankName = "Varrock west bank";
                        bot.bankType = "Bank booth";
                        Item_ComboBox.getItems().addAll("Clay", "Tin ore", "Silver ore", "Iron ore");
                        break;
                }
                bot.mineName = Location_ComboBox.getSelectionModel().getSelectedItem().toString() + " mine";
                Item_ComboBox.setDisable(false);
            } else {
                Item_ComboBox.setDisable(true);
            }
        };
    }
    private EventHandler<ActionEvent> getOre_ComboBoxEvent() {
        return event -> {
            Start_BT.setDisable(true);
            Bank_BT.setSelected(false);
            Power_BT.setSelected(false);
            if(Item_ComboBox.getSelectionModel().getSelectedItem() != null) {
                bot.oreName = Item_ComboBox.getSelectionModel().getSelectedItem().toString();
                bot.oreRockName = bot.oreName + " rocks";
                Bank_BT.setDisable(false);
                Power_BT.setDisable(false);
            } else {
                Bank_BT.setDisable(true);
                Power_BT.setDisable(true);
            }
        };
    }
}
