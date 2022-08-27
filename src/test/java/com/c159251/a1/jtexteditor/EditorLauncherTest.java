package com.c159251.a1.jtexteditor;

import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;


import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EditorLauncherTest {

    EditorController editorController;
    Clipboard systemClipboard;

    @Start
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(EditorLauncher.class.getResource("jtexteditor-layout.fxml"));
        Parent root = fxmlLoader.load();
        systemClipboard = Clipboard.getSystemClipboard();
        editorController = fxmlLoader.getController();
        editorController.initialize();
        Scene primaryScene = new Scene(root, 720, 480);
        primaryStage.setTitle("Simple Text Editor");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }


    // ----------- load files tests ------------------------ //

    @Test
    @Order(1)
    void loadFromTxtFile() {

        Platform.runLater( () -> {
            try {
                editorController.loadTextFromTxtFile(new File("src/test/resources/basic_test.txt"));
            } catch (Exception ignored) {

            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(editorController.getTextPane().getText()).isEqualTo("Testing in JavaFX is testing me.\n");
    }

    @Test
    @Order(2)
    void loadFromPdfFile() {
        Platform.runLater( () -> {
            try {
                editorController.loadTextFromPdfFile(new File("src/test/resources/basic_test.pdf"));
            } catch (Exception ignored) {

            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(editorController.getTextPane().getText()).isEqualTo("Testing in JavaFX is testing me. \n");
    }

    @Test
    @Order(3)
    void loadFromOdtFile() {
        Platform.runLater( () -> {
            try {
                editorController.loadTextFromOdtFile(new File("src/test/resources/basic_test.odt"));
            } catch (Exception ignored) {

            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(editorController.getTextPane().getText()).isEqualTo("Testing in JavaFX is testing me.\n");
    }

    // -------------------- new file and new window tests --------------------------- //

    @Test
    @Order(4)
    void newFileClear(FxRobot robot) {
        robot.clickOn("#textPane");
        robot.write("Testing testing");
        robot.clickOn("#fileMenu");
        robot.clickOn("#newFile");
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn(editorController.getNoBtn());
        Assertions.assertThat(editorController.getTextPane().getText()).isEqualTo("");
    }

    @Test
    @Order(5)
    void newFileDontClear(FxRobot robot) {
        robot.clickOn("#textPane");
        robot.write("Testing testing");
        robot.clickOn("#fileMenu");
        robot.clickOn("#newFile");
        robot.clickOn(editorController.getCancelBtn());
        Assertions.assertThat(editorController.getTextPane().getText()).isNotBlank();
    }

    @Test
    @Order(6)
    void newWindowFrom1to2Instances(FxRobot robot) {
        robot.clickOn("#fileMenu");
        robot.clickOn("#newWindow");
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(2, Stage.getWindows().size());
        // clean up afterwards
        try { Platform.runLater(() -> Stage.getWindows().get(Stage.getWindows().size() - 1).hide()); }
        catch (Exception e) {e.printStackTrace();}
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @Order(7)
    void newWindowFrom1to0Instances(FxRobot robot) {
        robot.clickOn("#fileMenu");
        robot.clickOn("#closeFile");
        assertEquals(0, Stage.getWindows().size());
    }

    // ----------- clipboard cut/copy/paste tests ------------- //

    @Test
    @Order(8)
    void copyText(FxRobot robot) {
        robot.clickOn("#textPane");
        robot.write("Testing in JavaFX is testing me.");
        WaitForAsyncUtils.waitForFxEvents();
        editorController.getTextPane().selectRange(11, 17);
        robot.clickOn("#copyBtn");
        Assertions.assertThat(editorController.getClipboardText()).isEqualTo("JavaFX");
    }

    @Test
    @Order(9)
    void cutText(FxRobot robot) {
        robot.clickOn("#textPane");
        robot.write("Testing in JavaFX is testing me.");
        WaitForAsyncUtils.waitForFxEvents();
        editorController.getTextPane().selectRange(11, 17);
        robot.clickOn("#cutBtn");
        Assertions.assertThat(editorController.getTextPane().getText()).isEqualTo("Testing in  is testing me.\n");
        Assertions.assertThat(editorController.getClipboardText()).isEqualTo("JavaFX");
    }

    @Test
    @Order(10)
    void copyAndPasteText(FxRobot robot) {
        robot.clickOn("#textPane");
        robot.write("Testing in JavaFX is testing me.");
        WaitForAsyncUtils.waitForFxEvents();
        editorController.getTextPane().selectRange(11, 17);
        robot.clickOn("#copyBtn");
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(editorController.getClipboardText()).isEqualTo("JavaFX");
        robot.clickOn("#textPane");
        editorController.getTextPane().displaceCaret(17);
        robot.clickOn("#pasteBtn");
        Assertions.assertThat(editorController.getTextPane().getText()).isEqualTo("Testing in JavaFXJavaFX is testing me.\n");
    }

    // ------------ searching text tests ---------------- //

    @Test
    @Order(11)
    void searchBlank(FxRobot robot) {
        robot.clickOn("#textPane");
        robot.write("Testing in JavaFX is testing me.");
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("#searchBtn");
        robot.clickOn("#searchField");
        robot.write("Java");
        robot.push(KeyCode.BACK_SPACE).push(KeyCode.BACK_SPACE).push(KeyCode.BACK_SPACE).push(KeyCode.BACK_SPACE);
        Assertions.assertThat(editorController.getSearchMatchesLabel()).hasText("No matches");
    }
    @Test
    @Order(12)
    void searchNoMatches(FxRobot robot) {
        Platform.runLater( () -> {
            try {
                editorController.loadTextFromTxtFile(new File("src/test/resources/basic_test.txt"));
            } catch (Exception ignored) {

            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("#searchBtn");
        robot.clickOn("#searchField");
        robot.write("JavaFex");
        Assertions.assertThat(editorController.getSearchMatchesLabel()).hasText("No matches");
    }
    @Test
    @Order(13)
    void search1Matches(FxRobot robot) {
        Platform.runLater( () -> {
            try {
                editorController.loadTextFromTxtFile(new File("src/test/resources/basic_test.txt"));
            } catch (Exception ignored) {

            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("#searchBtn");
        robot.clickOn("#searchField");
        robot.write("JavaFX");
        Assertions.assertThat(editorController.getSearchMatchesLabel()).hasText("1 of 1 matches");
    }

    @Test
    @Order(14)
    void search2Matches(FxRobot robot) {
        robot.clickOn("#textPane");
        robot.write("Hello Hello how are you doing?");
        robot.clickOn("#searchBtn");
        robot.clickOn("#searchField");
        robot.write("Hello");
        Assertions.assertThat(editorController.getSearchMatchesLabel()).hasText("1 of 2 matches");
    }

    @Test
    @Order(15)
    void search2of2Matches(FxRobot robot) {
        robot.clickOn("#textPane");
        robot.write("Hello Hello how are you doing?");
        robot.clickOn("#searchBtn");
        robot.clickOn("#searchField");
        robot.write("Hello");
        robot.clickOn("#searchForNextBtn");
        Assertions.assertThat(editorController.getSearchMatchesLabel()).hasText("2 of 2 matches");
    }

    @Test
    @Order(16)
    void saveToText(FxRobot robot) {

        editorController.setSelectedFile(new File("src/test/java/com/c159251/a1/jtexteditor/testSave.txt"));
        robot.clickOn("#textPane");
        robot.write("Testing text save");
        editorController.saveTextToTxtFile(editorController.getSelectedFile());
        File tempFile = new File("src/test/java/com/c159251/a1/jtexteditor/testSave.txt");
        assertTrue(tempFile.exists());
        //clean up
        assertTrue(tempFile.delete());
//        assertFalse(tempFile.exists());


    }


    @Test
    @Order(17)
    void saveToOdt(FxRobot robot) {

        editorController.setSelectedFile(new File("src/test/java/com/c159251/a1/jtexteditor/testSave.odt"));
        robot.clickOn("#textPane");
        robot.write("Testing ODT save");
        editorController.saveTextToOdtFile(editorController.getSelectedFile());
        File tempFile = new File("src/test/java/com/c159251/a1/jtexteditor/testSave.odt");
        assertTrue(tempFile.exists());
        //clean up
        assertTrue(tempFile.delete());
 //       assertFalse(tempFile.exists());


    }

    @Test
    @Order(18)
    void saveToPdf(FxRobot robot) {

        editorController.setSelectedFile(new File("src/test/java/com/c159251/a1/jtexteditor/testSave.pdf"));
        robot.clickOn("#textPane");
        robot.write("Testing PDF save");
        editorController.saveTextToOdtFile(editorController.getSelectedFile());
        File tempFile = new File("src/test/java/com/c159251/a1/jtexteditor/testSave.pdf");
        assertTrue(tempFile.exists());
        //clean up
        assertTrue(tempFile.delete());
 //       assertFalse(tempFile.exists());


    }

    @Test
    @Order(19)
    void testWordCount() {

        Platform.runLater( () -> {
            try {
                editorController.getTextPane().replaceText("Testing PDF file");
            } catch (Exception ignored) {

            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(editorController.countWords(),"Words 3:16 Chars");

        Platform.runLater( () -> {
            try {
                editorController.getTextPane().replaceText("Testing PDF file Again");
            } catch (Exception ignored) {

            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(editorController.countWords(),"Words 4:22 Chars");


    }

    @Test
    @Order(20)
    void testAddTimeStamp(FxRobot robot) {
        robot.clickOn("#timeStampBtn");
        assertTrue(editorController.getTextPane().getText().contains(editorController.getTimeStamp()));

    }

    @Test
    @Order(21)
    void testTimerRunning() {
        assertEquals(editorController.getTimerRunning(), Animation.Status.RUNNING);
    }
}
