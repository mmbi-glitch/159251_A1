package com.c159251.a1.jtexteditor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
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
    void initialize() {

        userName.setText("Robert");
        fontSize.setText("12");
        fontSizeSlider.adjustValue(12.0);

        textFontCombo.setItems(FXCollections.observableList(Font.getFamilies()));
        textFontCombo.setValue("Arial");
        codeFontCombo.setItems(FXCollections.observableList(Font.getFamilies()));
        codeFontCombo.setValue("Courier");

        fontSizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                fontSize.setText(String.valueOf(newValue.intValue()));
            }
        });

    }

    public void setConfig(ActionEvent actionEvent) {

        Stage stage = (Stage) textFontCombo.getScene().getWindow();
        stage.close();

    }

    public void cancelConfig(ActionEvent actionEvent) {

        Stage stage = (Stage) textFontCombo.getScene().getWindow();
        stage.close();

    }

    public void updateSlider(KeyEvent keyEvent) {
    }
}
