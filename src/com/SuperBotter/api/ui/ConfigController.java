package com.SuperBotter.api.ui;

import com.SuperBotter.api.CollectableItems;
import com.SuperBotter.api.ConfigSettings;
import com.SuperBotter.api.Location;
import com.SuperBotter.api.ProtectedItems;
import com.runemate.game.api.script.data.ScriptMetaData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ConfigController implements Initializable {
    private final ScriptMetaData metaData;
    private final ConfigSettings configSettings;
    private final ProtectedItems protectedItems;
    private final Location[] locations;
    private final String itemType;
    private final String power;
    private final String[] urns;
    private final String powerIng;

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

    public ConfigController(ScriptMetaData metaData, ConfigSettings configSettings, ProtectedItems protectedItems, Location[] locations, String[] urns, String itemType, String power, String powerIng) {
        this.metaData = metaData;
        this.configSettings = configSettings;
        this.protectedItems = protectedItems;
        this.locations = locations;
        this.itemType = itemType;
        this.power = power;
        this.urns = urns;
        this.powerIng = powerIng;
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        name_T.textProperty().set(metaData.getName());
        version_T.textProperty().set("Version " + metaData.getVersion());
        author_T.textProperty().set("By " + metaData.getAuthor());
        Item_Menu.textProperty().setValue(itemType);
        Power_BT.textProperty().set(power);

        Location_ComboBox.getItems().add("Custom Location (no banking)");
        for (Location location : locations) {
            Location_ComboBox.getItems().add(location.getName());
        }

        Urn_ComboBox.getItems().add("No urn");
        for (String urn : urns) {
            Urn_ComboBox.getItems().add(urn + " urn");
        }

        Start_BT.setOnAction(event -> {
            configSettings.startButtonPressed = true;
        });
        Bank_BT.setOnAction(event -> {
            Power_BT.setSelected(false);
            configSettings.dontDrop = true;
            if (configSettings.collectableItems.size(true) > 0) {
                Start_BT.setDisable(false);
            }
        });
        Power_BT.setOnAction(event -> {
            Bank_BT.setSelected(false);
            configSettings.dontDrop = false;
            if (configSettings.collectableItems.size(true) > 0) {
                Start_BT.setDisable(false);
            }
        });
        Location_ComboBox.setOnAction(event -> {
            String selection = Location_ComboBox.getSelectionModel().getSelectedItem().toString();
            if (selection.equals("Custom Location (no banking)")) {
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
                configSettings.radius = (int)radius_S.getValue();
            } else {
                Item_Menu.getItems().clear();
                Start_BT.setDisable(true);
                bankOrPower_T.setVisible(true);
                radius_T.setVisible(false);
                radius_S.setVisible(false);
                radiusValue_T.setVisible(false);
                configSettings.radius = -1;
                for (Location location : locations) {
                    if (location.getName().equals(selection)) {
                        configSettings.botAreaName = location.getName();
                        configSettings.botArea = location.getArea();
                        configSettings.bank = location.getBank();
                        CollectableItems items = location.getCollectableItems();
                        for (int i = 0; i < items.size(true); i++) {
                            CheckMenuItem menuItem = new CheckMenuItem(items.getNames(true)[i]);
                            final int j = i;
                            menuItem.setOnAction(event1 -> {
                                if (menuItem.isSelected()) {
                                    configSettings.collectableItems.add(items.getNames(true)[j], items.getInteractionNames(true)[j], items.getActionNames(true)[j], items.getActionings(true)[j], items.getPastTenses(true)[j], items.getProtectedItems(true)[j]);
                                } else {
                                    configSettings.collectableItems.remove(configSettings.collectableItems.getIndexByItemName(items.getNames(true)[j], true));
                                }
                                Urn_ComboBox.setDisable(false);

                                // If the urn + the bank/nobank have been done, enable start
                                if (Urn_ComboBox.getSelectionModel().getSelectedItem() != null) {
                                    if (!Objects.equals(Location_ComboBox.getSelectionModel().getSelectedItem().toString(), "Custom Location (" + powerIng + " only)")) {
                                        Bank_BT.setDisable(false);
                                        Power_BT.setDisable(false);
                                    }
                                    if ((Bank_BT.isSelected() || Power_BT.isSelected()) && configSettings.collectableItems.size(true) > 0) {
                                        Start_BT.setDisable(false);
                                    }
                                }
                            });
                            Item_Menu.getItems().add(menuItem);
                        }
                        break;
                    }
                }
                Item_Menu.setDisable(false);
            }
        });
        Urn_ComboBox.setOnAction(event -> {
            if (Urn_ComboBox.getSelectionModel().getSelectedItem() != null) {
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
                if (configSettings.collectableItems.getNames(true).length > 0 && (Bank_BT.isSelected() || Power_BT.isSelected())) {
                    Start_BT.setDisable(false);
                }
            }
        });

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
}
