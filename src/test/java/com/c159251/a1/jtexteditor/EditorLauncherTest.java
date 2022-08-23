package com.c159251.a1.jtexteditor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;


import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@ExtendWith(ApplicationExtension.class)
class EditorLauncherTest {

    EditorController editorController;

    @Start
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(EditorLauncher.class.getResource("jtexteditor-layout.fxml"));
        Parent root = fxmlLoader.load();
        editorController = fxmlLoader.getController();
        editorController.initialize();
        Scene primaryScene = new Scene(root, 720, 480);
        primaryStage.setTitle("Simple Text Editor");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    @Test
    void containsTextWindow() {

            assertEquals("Testing in JavaFX is testing me.\n", editorController.getTextFromTxtFile(new File("src/test/java/com/c159251/a1/jtexteditor/basic_test.txt")));

    }


}