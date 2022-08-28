package com.c159251.a1.jtexteditor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ConfigwindowController {

    @FXML
    public Button configSaveBtn;
    @FXML
    public Button configCancelBtn;
    @FXML
    public Slider fontSizeSlider;
    @FXML
    public TextField userName;
    @FXML
    public TextField fontSize;
    @FXML
    public ComboBox<String> textFontCombo;
    @FXML
    public ComboBox<String> codeFontCombo;
    @FXML
    public ListView themesList;

    @FXML
    void initialize() {

        userName.setText(EditorController.getCONFIG().getUsername());
        fontSize.setText(String.valueOf(EditorController.getCONFIG().getFontSize()));
        fontSizeSlider.adjustValue(EditorController.getCONFIG().getFontSize());
        fontSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> fontSize.setText(String.valueOf(newValue.intValue())));

    }

    public void setConfig(ActionEvent actionEvent) {

        Stage stage = (Stage) fontSizeSlider.getScene().getWindow();
        EditorController.getCONFIG().updateConfig(userName.getText(),Integer.parseInt(fontSize.getText()));
        stage.close();

    }

    public void cancelConfig(ActionEvent actionEvent) {

        Stage stage = (Stage) fontSizeSlider.getScene().getWindow();
        stage.close();

    }

}
