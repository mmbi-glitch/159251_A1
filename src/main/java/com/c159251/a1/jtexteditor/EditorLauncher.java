package com.c159251.a1.jtexteditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.util.Objects;

/** This class is solely responsible for launching the Application window. **/

public class EditorLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(EditorLauncher.class.getResource("jtexteditor-layout.fxml"));
        Parent root = fxmlLoader.load();
        EditorController editorController = fxmlLoader.getController();
        editorController.initialize();
        Scene primaryScene = new Scene(root, 720, 480);
        primaryStage.setTitle("NEW FILE - JText Editor");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            YesNoCancel alert = new YesNoCancel();
            alert.showAndWait().ifPresent(response -> {
                if(Objects.equals(response, ButtonType.YES)) {
                    editorController.onFileSave();
                }
                if (Objects.equals(response, ButtonType.CANCEL)) {
                    e.consume();
                }
            });
        });
    }

    public static void main(String[] args) {
        launch();
    }


}