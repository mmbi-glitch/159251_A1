package com.c159251.a1.jtexteditor;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

public class YesNoCancel extends Alert {
    public YesNoCancel() {

        super(AlertType.WARNING);
        this.setTitle("Unsaved Changes Exist");
        this.setHeaderText("Do you want to save before exit?");
        this.initStyle(StageStyle.UTILITY);
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.getButtonTypes().setAll(yesButton,noButton,cancelButton);


    }




}
