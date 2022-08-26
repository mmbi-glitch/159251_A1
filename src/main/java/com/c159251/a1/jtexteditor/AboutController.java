package com.c159251.a1.jtexteditor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;

public class AboutController {

    public WebView aboutWebView;

    @FXML
    void initialize() {

        File aboutHTML = new File("src/main/resources/com/c159251/a1/jtexteditor/about.html");
        aboutWebView.getEngine().load(aboutHTML.toURI().toString());

    }

    public void closeWindow(ActionEvent actionEvent) {

        Stage stage = (Stage) aboutWebView.getScene().getWindow();
        stage.close();

    }
}
