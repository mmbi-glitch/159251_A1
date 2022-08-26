package com.c159251.a1.jtexteditor;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

public class YesNoCancel extends Alert {
    public YesNoCancel() {
        super(AlertType.WARNING);
        this.setTitle("Unsaved Changes Exist");
        this.setHeaderText("Do you want to save before exiting/clearing?");
        ButtonType yesButton = ButtonType.YES;
        ButtonType noButton = ButtonType.NO;
        ButtonType cancelButton = ButtonType.CANCEL;
        this.getButtonTypes().setAll(yesButton,noButton,cancelButton);
    }




}
