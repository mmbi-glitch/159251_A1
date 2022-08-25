package com.c159251.a1.jtexteditor;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.service.query.PointQuery;
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
        editorController = fxmlLoader.getController();
        editorController.initialize();
        systemClipboard = Clipboard.getSystemClipboard();
        Scene primaryScene = new Scene(root, 720, 480);
        primaryStage.setTitle("Simple Text Editor");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    // ----------- load files tests ------------------------ //

    @Test
    @Order(1)
    void loadFromTxtFile() {
        editorController.loadTextFromTxtFile(new File("src/test/resources/basic_test.txt"));
        Assertions.assertThat(editorController.getTextPane()).hasText("Testing in JavaFX is testing me.\n");
    }

    @Test
    @Order(2)
    void loadFromPdfFile() {
        editorController.loadTextFromPdfFile(new File("src/test/resources/basic_test.pdf"));
        Assertions.assertThat(editorController.getTextPane()).hasText("Testing in JavaFX is testing me. \n");
    }
    @Test
    @Order(3)
    void loadFromOdtFile() {
        editorController.loadTextFromOdtFile(new File("src/test/resources/basic_test.odt"));
        Assertions.assertThat(editorController.getTextPane()).hasText("Testing in JavaFX is testing me.\n");
    }

    // -------------------- new file and new window tests --------------------------- //

    @Test
    @Order(4)
    void newFile(FxRobot robot) {
        editorController.loadTextFromPdfFile(new File("src/test/resources/basic_test.pdf"));
        robot.clickOn("#fileMenu");
        robot.clickOn("#newFile");
        robot.clickOn(editorController.getYesBtn());
        Assertions.assertThat(editorController.getTextPane().getText()).isEqualTo("");
    }

    @Test
    @Order(5)
    void newFileNoClear(FxRobot robot) {
        editorController.loadTextFromPdfFile(new File("src/test/resources/basic_test.pdf"));
        robot.clickOn("#fileMenu");
        robot.clickOn("#newFile");
        robot.clickOn(editorController.getNoBtn());
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
        try {
            Platform.runLater(() -> Stage.getWindows().get(Stage.getWindows().size() - 1).hide());
        }
        catch (Exception ignored) {}
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @Order(7)
    void newWindowFrom1to0Instances(FxRobot robot) {
        robot.clickOn("#fileMenu");
        robot.clickOn("#closeFile");
        robot.clickOn(editorController.getYesBtn());
        assertEquals(0, Stage.getWindows().size());
    }

    @Test
    @Order(8)
    void newWindowFrom2to1Instances(FxRobot robot) {
        robot.clickOn("#fileMenu");
        robot.clickOn("#newWindow");
        try {
            Platform.runLater(() -> Stage.getWindows().get(Stage.getWindows().size() - 1).requestFocus());
        }
        catch (Exception ignored) {}
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("#fileMenu");
        robot.clickOn("#closeFile");
        WaitForAsyncUtils.waitForFxEvents();
        editorController.getFileMenu().hide();
        robot.clickOn(editorController.getYesBtn());
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(1, Stage.getWindows().size());
    }

    // ----------- clipboard cut/copy/paste tests ------------- //

    @Test
    @Order(9)
    void copyText(FxRobot robot) {
        editorController.loadTextFromTxtFile(new File("src/test/resources/basic_test.txt"));
        editorController.getTextPane().selectRange(11, 17);
        robot.clickOn("#copyBtn");
        Assertions.assertThat(editorController.getClipboardText()).isEqualTo("JavaFX");
    }

    @Test
    @Order(10)
    void cutText(FxRobot robot) {
        editorController.loadTextFromTxtFile(new File("src/test/resources/basic_test.txt"));
        editorController.getTextPane().selectRange(11, 17);
        robot.clickOn("#cutBtn");
        Assertions.assertThat(editorController.getTextPane()).hasText("Testing in  is testing me.\n");
        Assertions.assertThat(editorController.getClipboardText()).isEqualTo("JavaFX");
    }

    @Test
    @Order(11)
    void copyAndPasteText(FxRobot robot) {
        editorController.loadTextFromTxtFile(new File("src/test/resources/basic_test.txt"));
        editorController.getTextPane().selectRange(11, 17);
        robot.clickOn("#copyBtn");
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(editorController.getClipboardText()).isEqualTo("JavaFX");
        robot.clickOn("#textPane");
        editorController.getTextPane().positionCaret(17);
        robot.clickOn("#pasteBtn");
        Assertions.assertThat(editorController.getTextPane()).hasText("Testing in JavaFXJavaFX is testing me.\n");
    }

    // ------------ searching text tests ---------------- //

    @Test
    @Order(12)
    void searchBlank(FxRobot robot) {
        editorController.loadTextFromTxtFile(new File("src/test/resources/basic_test.txt"));
        robot.clickOn("#searchBtn");
        robot.clickOn("#searchField");
        robot.write("Java");
        robot.push(KeyCode.BACK_SPACE).push(KeyCode.BACK_SPACE).push(KeyCode.BACK_SPACE).push(KeyCode.BACK_SPACE);
        Assertions.assertThat(editorController.getSearchMatchesLabel()).hasText("No matches");
    }
    @Test
    @Order(13)
    void searchNoMatches(FxRobot robot) {
        editorController.loadTextFromTxtFile(new File("src/test/resources/basic_test.txt"));
        robot.clickOn("#searchBtn");
        robot.clickOn("#searchField");
        robot.write("JavaFex");
        Assertions.assertThat(editorController.getSearchMatchesLabel()).hasText("No matches");
    }
    @Test
    @Order(14)
    void search1Matches(FxRobot robot) {
        editorController.loadTextFromTxtFile(new File("src/test/resources/basic_test.txt"));
        robot.clickOn("#searchBtn");
        robot.clickOn("#searchField");
        robot.write("JavaFX");
        Assertions.assertThat(editorController.getSearchMatchesLabel()).hasText("1 of 1 matches");
    }

    @Test
    @Order(15)
    void search2Matches(FxRobot robot) {
        robot.clickOn("#textPane");
        robot.write("Hello Hello how are you doing?");
        robot.clickOn("#searchBtn");
        robot.clickOn("#searchField");
        robot.write("Hello");
        Assertions.assertThat(editorController.getSearchMatchesLabel()).hasText("1 of 2 matches");
    }

    @Test
    @Order(16)
    void search2of2Matches(FxRobot robot) {
        robot.clickOn("#textPane");
        robot.write("Hello Hello how are you doing?");
        robot.clickOn("#searchBtn");
        robot.clickOn("#searchField");
        robot.write("Hello");
        robot.clickOn("#searchForNextBtn");
        Assertions.assertThat(editorController.getSearchMatchesLabel()).hasText("2 of 2 matches");
    }

    // ----------------------------------- saving files tests ------------------------ //

    @Test
    @Order(17)
    void saveToText() {
        editorController.setSelectedFile(new File("src/test/resources/testSave.txt"));
        editorController.getTextPane().setText("Testing file");
        editorController.saveTextToTxtFile(editorController.getSelectedFile());
        File tempFile = new File("src/test/resources/testSave.txt");
        assertTrue(tempFile.exists());
        //clean up
        tempFile.delete();
        assertFalse(tempFile.exists());
    }

    @Test
    @Order(18)
    void saveToOdt() {
        editorController.setSelectedFile(new File("src/test/resources/testSave.odt"));
        editorController.getTextPane().setText("Testing ODT file");
        editorController.saveTextToOdtFile(editorController.getSelectedFile());
        File tempFile = new File("src/test/resources/testSave.odt");
        assertTrue(tempFile.exists());
        //clean up
        tempFile.delete();
        assertFalse(tempFile.exists());
    }

    @Test
    @Order(19)
    void saveToPdf() {
        editorController.setSelectedFile(new File("src/test/resources/testSave.pdf"));
        editorController.getTextPane().setText("Testing PDF file");
        editorController.saveTextToPdfFile(editorController.getSelectedFile());
        File tempFile = new File("src/test/resources/testSave.pdf");
        assertTrue(tempFile.exists());
        //clean up
        tempFile.delete();
        assertFalse(tempFile.exists());
    }
}
