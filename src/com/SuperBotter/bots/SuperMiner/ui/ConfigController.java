package com.SuperBotter.bots.SuperMiner.ui;

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
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Java FX Controller for the Config class
 * The controller class is where the logic and implementation of GUI events go.
 * <p>
 * All settings are disabled until location is chosen
 * Only the start button and the ore selector needs to be disabled, but this way makes the user keep on going down the list of options
 * When location is chosen, ores at that mine are allowed to be chosen
 * Then the user can decide if they want to bank the ores they mine or powermine
 * Then the user can press start and the configSettings will begin, following the options they selected
 */
public class ConfigController implements Initializable {
    private ScriptMetaData metaData;
    private ConfigSettings configSettings;
    private ProtectedItems protectedItems;

    // ComboBox
    @FXML
    private ComboBox Location_ComboBox, Urn_ComboBox;

    @FXML
    private MenuButton Item_Menu;

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
        Item_Menu.textProperty().setValue("Ore");
        Power_BT.textProperty().set("Powermine");
        Location_ComboBox.getItems().addAll(
                "Custom Location (powermining only)",
                "Al Kharid",
                "Draynor",
                "Dwarven Mine",
                "Falador south-west",
                "Lumbridge Swamp east",
                "Lumbridge Swamp west",
                "Rimmington",
                "Varrock south-east",
                "Varrock south-west"
        );
        Urn_ComboBox.getItems().addAll(
                "No urn",
                "Cracked mining urn",
                "Fragile mining urn",
                "Mining urn",
                "Strong mining urn",
                "Decorated mining urn"
        );
        Start_BT.setOnAction(getStart_BTAction());
        Bank_BT.setOnAction(getBank_BTAction());
        Power_BT.setOnAction(getPower_BTAction());
        Location_ComboBox.setOnAction(getLocation_ComboBoxEvent());
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

    private void addItemOptions(String... items) {
        for (int i = 0; i < items.length; i++) {
            CheckMenuItem item = new CheckMenuItem(items[i]);
            item.setOnAction((action) -> {
                if (item.isSelected()) {
                    configSettings.interactableItems.add(item.getText(), item.getText() + " rocks", "Mine", "Mining", "mined");
                } else {
                    configSettings.interactableItems.remove(configSettings.interactableItems.getIndexByItemName(item.getText()));
                }
                Urn_ComboBox.setDisable(false);

                // If the urn + the bank/nobank have been done, enable start
                if (Urn_ComboBox.getSelectionModel().getSelectedItem() != null) {
                    if (!Objects.equals(Location_ComboBox.getSelectionModel().getSelectedItem().toString(), "Custom Location (powermining only)")) {
                        Bank_BT.setDisable(false);
                        Power_BT.setDisable(false);
                    }
                    if ((Bank_BT.isSelected() || Power_BT.isSelected()) && configSettings.interactableItems.size() > 0) {
                        Start_BT.setDisable(false);
                    }
                }
            });
            Item_Menu.getItems().add(item);
        }
    }

    private EventHandler<ActionEvent> getLocation_ComboBoxEvent() {
        return event -> {
            Item_Menu.getItems().clear();
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
            if (Location_ComboBox.getSelectionModel().getSelectedItem() != null) {
                switch (Location_ComboBox.getSelectionModel().getSelectedItem().toString()) {
                    case "Custom Location (powermining only)":
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
                        addItemOptions("Adamantite ore", "Clay", "Coal", "Copper ore", "Gold ore", "Iron ore", "Mithril ore", "Runite ore", "Silver ore", "Tin ore");
                        break;
                    case "Al Kharid":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(3292, 3285, 0), new Coordinate(3309, 3315, 0));
                        configSettings.bank = new Bank(Bank.BankName.AL_KHARID);
                        addItemOptions("Adamantite ore", "Coal", "Copper ore", "Gold ore", "Iron ore", "Mithril ore", "Silver ore", "Tin ore");
                        break;
                    /*// Commented out until the default web supports the crafting guild or until I learn how to use custom webs
                    case "Crafting Guild":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(2943, 3291, 0), new Coordinate(2937, 3276, 0));
                        configSettings.bank = new Banks(Banks.BankName.CLAN_CAMP);
                        Item_ComboBox.getItems().addAll("Clay", "Gold ore", "Silver ore");
                        break;
                    */
                    case "Draynor":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(3138, 3315, 0), new Coordinate(3143, 3320, 0));
                        configSettings.bank = new Bank(Bank.BankName.CABBAGE_FACEPUNCH_BONANZA);
                        addItemOptions("Clay");
                        break;
                    case "Dwarven Mine":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(3033, 9759, 0), new Coordinate(3059, 9786, 0));
                        configSettings.bank = new Bank(Bank.BankName.FALADOR_EAST);
                        addItemOptions("Adamantite ore", "Coal", "Copper ore", "Gold ore", "Iron ore", "Mithril ore", "Tin ore");
                        break;
                    case "Falador south-west":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(2930, 3340, 0), new Coordinate(2922, 3334, 0));
                        configSettings.bank = new Bank(Bank.BankName.CLAN_CAMP);
                        addItemOptions("Coal", "Copper ore", "Iron ore", "Tin ore");
                        break;
                    case "Lumbridge Swamp east":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(3233, 3151, 0), new Coordinate(3223, 3145, 0));
                        configSettings.bank = new Bank(Bank.BankName.AL_KHARID);
                        addItemOptions("Copper ore", "Tin ore");
                        new Bank(Bank.BankName.AL_KHARID);
                        break;
                    case "Lumbridge Swamp west":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(3149, 3152, 0), new Coordinate(3144, 3144, 0));
                        configSettings.bank = new Bank(Bank.BankName.DRAYNOR);
                        addItemOptions("Adamantite ore", "Coal", "Mithril ore");
                        break;
                    case "Rimmington":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(2981, 3242, 0), new Coordinate(2964, 3229, 0));
                        configSettings.bank = new Bank(Bank.BankName.CLAN_CAMP);
                        addItemOptions("Clay", "Copper ore", "Gold ore", "Iron ore", "Tin ore");
                        break;
                    case "Varrock south-east":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(3280, 3361, 0), new Coordinate(3291, 3371, 0));
                        configSettings.bank = new Bank(Bank.BankName.VARROCK_EAST);
                        addItemOptions("Copper ore", "Iron ore", "Tin ore");
                        break;
                    case "Varrock south-west":
                        configSettings.botArea = new Area.Rectangular(new Coordinate(3171, 3364, 0), new Coordinate(3188, 3380, 0));
                        configSettings.bank = new Bank(Bank.BankName.VARROCK_WEST);
                        addItemOptions("Clay", "Iron ore", "Silver ore", "Tin ore");
                        break;
                }
                if (Objects.equals(Location_ComboBox.getSelectionModel().getSelectedItem().toString(), "Custom Location (powermining only)")) {
                    configSettings.botAreaName = null;
                } else {
                    configSettings.botAreaName = Location_ComboBox.getSelectionModel().getSelectedItem().toString() + " mine";
                }
                Item_Menu.setDisable(false);
            }
        };
    }

    private EventHandler<ActionEvent> getUrn_ComboBoxEvent() {
        return event -> {
            if (Urn_ComboBox.getSelectionModel().getSelectedItem() != null) {
                String selection = Urn_ComboBox.getSelectionModel().getSelectedItem().toString();
                protectedItems.remove(Pattern.compile(" urn"));
                if (!selection.equals("No urn")) {
                    protectedItems.add(selection + " (r)", 1, ProtectedItems.Status.WANTED);
                    protectedItems.add(selection, 0, ProtectedItems.Status.HELD);
                    protectedItems.add(selection + " (full)", 0, ProtectedItems.Status.HELD);
                }
                if (Objects.equals(Location_ComboBox.getSelectionModel().getSelectedItem().toString(), "Custom Location (powermining only)")) {
                    Power_BT.setSelected(true);
                    configSettings.dontDrop = false;
                } else {
                    Bank_BT.setDisable(false);
                    Power_BT.setDisable(false);
                }
                // If the item + the bank/nobank have been done, enable start
                if (configSettings.interactableItems.getNames().length > 0 && (Bank_BT.isSelected() || Power_BT.isSelected())) {
                    Start_BT.setDisable(false);
                }
            }
        };
    }
}
