package com.c159251.a1.jtexteditor;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
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
    public Button cutBtn;
    public Button copyBtn;
    public Button pasteBtn;
    public Button selectBtn;
    public int selectFrom;
    public int selectTo;
    public String copiedText;
    public String cutText;

    public void initialize() {
        copiedText = "";
        cutText = "";
    }

    // close file on 'close' button press
    @FXML
    protected void onFileClose(ActionEvent actionEvent) {
        System.exit(0);
    }

    // open txt file on 'open' button press
    @FXML
    public void onFileOpen(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files (*.odt, *.txt)", "*.txt", "*.odt")
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
                fileToText.append(line).append("\n");
            }
            // if successfully loaded, populate textPane with file text
            if (!fileToText.isEmpty()) {
                textPane.setText(fileToText.toString());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    // select text method
    public void selectText() {
        selectFrom = textPane.getCaretPosition() - textPane.getSelectedText().length();
        selectTo = textPane.getCaretPosition();
        System.out.println("selected text = " + textPane.getText(selectFrom, selectTo));
        System.out.println("length of selected text = " + textPane.getSelectedText().length());
        System.out.println("pos = " + selectFrom + " " + selectTo);
    }

    public void copyText() {
        copiedText = textPane.getText(selectFrom, selectTo);
        if (!cutText.isBlank()) {
            cutText = "";
        }
        System.out.println("copied text = " + copiedText);
    }

    public void cutText() {
        cutText = textPane.getText(selectFrom, selectTo);
        textPane.deleteText(selectFrom, selectTo);
        if (!copiedText.isBlank()) {
            copiedText = "";
        }
        System.out.println("cut text = " + cutText);
    }

    public void pasteText() {
        if (!copiedText.isBlank()) {
            textPane.insertText(textPane.getCaretPosition(), copiedText);
            System.out.println("inserted copied text");
        }
        if (!cutText.isBlank()) {
            textPane.insertText(textPane.getCaretPosition(), cutText);
            System.out.println("inserted cut text");
        }
    }
}