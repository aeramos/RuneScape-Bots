package com.SuperBotter.bots.SuperFisher.ui;

import com.SuperBotter.api.Bank;
import com.SuperBotter.api.ConfigSettings;
import com.SuperBotter.api.ProtectedItems;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.script.data.ScriptMetaData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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
public class ConfigController implements Initializable {
    private ScriptMetaData metaData;
    private ConfigSettings configSettings;
    private ProtectedItems protectedItems;

    // ComboBox
    @FXML
    private ComboBox Location_ComboBox, Item_ComboBox, Urn_ComboBox;

    // Start button
    @FXML
    private Button Start_BT;

    @FXML
    private RadioButton Bank_BT, Power_BT;

    @FXML
    private Text name_T, version_T, author_T, radius_T, radiusValue_T, bankOrPower_T;

    @FXML
    private Slider radius_S;

    public ConfigController(ScriptMetaData metaData, ConfigSettings configSettings, ProtectedItems protectedItems) {
        this.metaData = metaData;
        this.configSettings = configSettings;
        this.protectedItems = protectedItems;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name_T.textProperty().set(metaData.getName());
        version_T.textProperty().set("Version " + metaData.getVersion());
        author_T.textProperty().set("By " + metaData.getAuthor());
        Item_ComboBox.promptTextProperty().set("Fish");
        Power_BT.textProperty().set("Powerfish");
        Location_ComboBox.getItems().addAll(
                "Custom Location (powerfishing only)",
                "Al Kharid west",
                "Lum Bridge",
                "Lumbridge Church",
                "Lumbridge Swamp east"
        );
        Urn_ComboBox.getItems().addAll(
                "No urn",
                "Cracked fishing urn",
                "Fragile fishing urn",
                "Fishing urn",
                "Strong fishing urn",
                "Decorated fishing urn"
        );
        Start_BT.setOnAction(getStart_BTAction());
        Bank_BT.setOnAction(getBank_BTAction());
        Power_BT.setOnAction(getPower_BTAction());
        Location_ComboBox.setOnAction(getLocation_ComboBoxEvent());
        Item_ComboBox.setOnAction(getItem_ComboBoxEvent());
        Urn_ComboBox.setOnAction(getUrn_ComboBoxEvent());

        // custom radius
        radius_T.setVisible(false);
        radius_S.setVisible(false);
        radiusValue_T.setVisible(false);
        radius_S.valueProperty().addListener((observable, oldValue, newValue) -> {
            configSettings.radius = (int)radius_S.getValue();
            radiusValue_T.textProperty().set(String.valueOf(configSettings.radius));
        });
        configSettings.guiWait = false;
    }

    private EventHandler<ActionEvent> getStart_BTAction() {
        return event -> {
            try {
                configSettings.startButtonPressed = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
    private EventHandler<ActionEvent> getBank_BTAction() {
        return event -> {
            try {
                Power_BT.setSelected(false);
                configSettings.dontDrop = true;
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
                configSettings.dontDrop = false;
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
            bankOrPower_T.setVisible(true);
            radius_T.setVisible(false);
            radius_S.setVisible(false);
            radiusValue_T.setVisible(false);
            configSettings.radius = -1;
            if(Location_ComboBox.getSelectionModel().getSelectedItem() != null) {
                switch (Location_ComboBox.getSelectionModel().getSelectedItem().toString()) {
                    case "Custom Location (powerfishing only)":
                        configSettings.botArea = null;
                        configSettings.bank = null;
                        Power_BT.setDisable(true);
                        Bank_BT.setDisable(true);
                        Power_BT.setSelected(true);
                        Bank_BT.setSelected(false);
                        bankOrPower_T.setVisible(false);
                        radius_T.setVisible(true);
                        radius_S.setVisible(true);
                        radiusValue_T.setVisible(true);
                        configSettings.radius = 10; // the default amount
                        configSettings.dontDrop = false;
                        Item_ComboBox.getItems().addAll("Raw anchovies", "Raw crayfish", "Raw herring", "Raw pike", "Raw salmon", "Raw sardine", "Raw shrimps", "Raw trout");
                        break;
                    case "Al Kharid west":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(3255, 3159, 0), new Coordinate(3258, 3164, 0));
                        configSettings.bank = new Bank(Bank.BankName.AL_KHARID);
                        Item_ComboBox.getItems().addAll("Raw anchovies", "Raw herring", "Raw sardine", "Raw shrimps");
                        break;
                    case "Lum Bridge":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(3239, 3241, 0), new Coordinate(3242, 3257, 0));
                        configSettings.bank = new Bank(Bank.BankName.COMBAT_ACADEMY);
                        Item_ComboBox.getItems().addAll("Raw pike", "Raw salmon", "Raw trout");
                        break;
                    case "Lumbridge Church":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(3256, 3203, 0), new Coordinate(3258, 3207, 0));
                        configSettings.bank = new Bank(Bank.BankName.COMBAT_ACADEMY);
                        Item_ComboBox.getItems().addAll("Raw crayfish");
                        break;
                    case "Lumbridge Swamp east":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(3239, 3146, 0), new Coordinate(3246, 3157, 0));
                        configSettings.bank = new Bank(Bank.BankName.COMBAT_ACADEMY);
                        Item_ComboBox.getItems().addAll("Raw anchovies", "Raw herring", "Raw sardine", "Raw shrimps");
                        break;
                }
                configSettings.botAreaName = Location_ComboBox.getSelectionModel().getSelectedItem().toString() + " fishing spot";
                Item_ComboBox.setDisable(false);
            }
        };
    }
    private EventHandler<ActionEvent> getItem_ComboBoxEvent() {
        return event -> {
            if(Item_ComboBox.getSelectionModel().getSelectedItem() != null) {
                configSettings.itemName = Item_ComboBox.getSelectionModel().getSelectedItem().toString();
                protectedItems.removeAllExcept(Pattern.compile(" urn"));
                switch (configSettings.itemName) {
                    case "Raw anchovies":
                    case "Raw shrimps":
                        configSettings.actionName = "Net";
                        configSettings.actionIng = "Netting";
                        break;
                    case "Raw crayfish":
                    case "Raw lobster":
                        configSettings.actionName = "Cage";
                        configSettings.actionIng = "Caging";
                        break;
                    case "Raw herring":
                    case "Raw pike":
                    case "Raw sardine":
                        configSettings.actionName = "Bait";
                        configSettings.actionIng = "Baiting";
                        protectedItems.add("Fishing bait", 0, ProtectedItems.Status.REQUIRED);
                        break;
                    case "Raw salmon":
                    case "Raw trout":
                        configSettings.actionName = "Lure";
                        configSettings.actionIng = "Luring";
                        protectedItems.add("Feather", 0, ProtectedItems.Status.REQUIRED);
                        break;
                    case "Raw tuna":
                    case "Raw swordfish":
                    case "Raw shark":
                        configSettings.actionName = "Harpoon";
                        configSettings.actionIng = "Harpooning";
                        break;
                }
                Urn_ComboBox.setDisable(false);

                // If the urn + the bank/nobank have been done, enable start
                if (Urn_ComboBox.getSelectionModel().getSelectedItem() != null) {
                    if (!Objects.equals(Location_ComboBox.getSelectionModel().getSelectedItem().toString(), "Custom Location (powerfishing only)")) {
                        Bank_BT.setDisable(false);
                        Power_BT.setDisable(false);
                    }
                    if (Bank_BT.isSelected() || Power_BT.isSelected()) {
                        Start_BT.setDisable(false);
                    }
                }
            } else {
                protectedItems.removeAllExcept(Pattern.compile(" urn"));
            }
        };
    }
    private EventHandler<ActionEvent> getUrn_ComboBoxEvent() {
        return event -> {
            if(Urn_ComboBox.getSelectionModel().getSelectedItem() != null) {
                String selection = Urn_ComboBox.getSelectionModel().getSelectedItem().toString();
                protectedItems.remove(Pattern.compile(" urn"));
                if (!selection.equals("No urn")) {
                    protectedItems.add(selection + " (r)", 1, ProtectedItems.Status.WANTED);
                    protectedItems.add(selection, 0, ProtectedItems.Status.HELD);
                    protectedItems.add(selection + " (full)", 0, ProtectedItems.Status.HELD);
                }
                if (Objects.equals(Location_ComboBox.getSelectionModel().getSelectedItem().toString(), "Custom Location (powerfishing only)")) {
                    Power_BT.setSelected(true);
                    configSettings.dontDrop = false;
                } else {
                    Bank_BT.setDisable(false);
                    Power_BT.setDisable(false);
                }
                // If the item + the bank/nobank have been done, enable start
                if (Item_ComboBox.getSelectionModel().getSelectedItem() != null && (Bank_BT.isSelected() || Power_BT.isSelected())) {
                    Start_BT.setDisable(false);
                }
            }
        };
    }
}
