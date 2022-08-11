package com.c159251.a1.jtexteditor;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

/** This class is connected with the fxml config file and is responsible for the main program logic. **/

public class EditorController {

    private static File selectedFile;
    // using a different var for saving file (using the same var can cause errors)
    private static File savedFile;

    private MenuItem closeFile;
    private MenuItem openFile;
    private MenuItem saveFile;
    private MenuItem saveFileAs;
    private TextArea textPane;

    // close file on 'close' button press
    @FXML
    protected void onFileClose(ActionEvent ae) {
        System.exit(0);
    }

    // open txt file on 'open' button press
    @FXML
    protected void onFileOpen(ActionEvent ae) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt")
        );
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            loadTextFromFile(selectedFile);
        }
        // reset currently saved file if opening a new file
        if (savedFile != null && savedFile != selectedFile) {
            savedFile = null;
        }
    }

    protected void loadTextFromFile(File fileToLoad) {
        StringBuilder fileToText;
        // load text in file using a buffered file reader
        try (BufferedReader fileReader = new BufferedReader(new FileReader(fileToLoad))) {
            // Load all lines of text, one by one, into a StringBuilder
            String line;
            fileToText = new StringBuilder();
            while ((line = fileReader.readLine()) != null) {
                fileToText.append(line + "\n");
            }
            // if successfully loaded, populate textPane with file text
            if (!fileToText.isEmpty()) {
                textPane.setText(fileToText.toString());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    protected void onFileSave() {
        //if save is triggered with no stored file, then it should try as a 'save as'
        if (savedFile == null) {
            onFileSaveAs();
            return;
        }
        saveTextToFile(savedFile);
    }

    @FXML
    protected void onFileSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt")
        );
        // if saved file is null, set to default directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        // otherwise, set to parent directory of saved file
        if (savedFile != null) {
            fileChooser.setInitialDirectory(savedFile.getParentFile());
        }
        savedFile = fileChooser.showSaveDialog(null);
        if (savedFile != null) {
            saveTextToFile(savedFile);
        }
    }

    protected void saveTextToFile(File fileToSave) {
        //if the text is not blank then
        if (!textPane.getText().isEmpty()) {
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(savedFile))) {
                fileWriter.write(textPane.getText());
                fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}