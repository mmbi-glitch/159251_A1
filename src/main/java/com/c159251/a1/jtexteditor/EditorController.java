package com.c159251.a1.jtexteditor;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

/** This class is connected with the fxml config file and is responsible for the main program logic. **/

public class EditorController {

    public MenuItem closeFile;
    public MenuItem openFile;
    public TextArea textPane;

    // close file on 'close' button press
    @FXML
    protected void onFileClose(ActionEvent ae) {
        System.exit(0);
    }

    // open txt file on 'open' button press
    @FXML
    public void onFileOpen(ActionEvent ae) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt")
        );
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            loadTextFromFile(selectedFile);
        }
    }

    public void loadTextFromFile(File fileToLoad) {
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
}