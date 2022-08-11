package com.c159251.a1.jtexteditor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.*;

/** This class is connected with the fxml config file and is responsible for the main program logic. **/

public class EditorController {

    public static File selectedFile;
    public MenuItem closeFile;
    public MenuItem openFile;
    public MenuItem saveFile;
    public MenuItem saveFileAs;
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
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            loadTextFromFile(selectedFile);
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
    @FXML
    protected void onFileSave() {
        //if save is triggered with no stored file, then it should try as a 'save as'
        if (selectedFile == null) {
            onFileSaveAs();
            return;
        }
        saveTextToFile(selectedFile);
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
        if (selectedFile != null) {
            fileChooser.setInitialDirectory(selectedFile.getParentFile());
        }
        selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            saveTextToFile(selectedFile);
        }
    }

    public void saveTextToFile(File fileToSave) {
        //if the text is not blank, then write text to file
        if (!textPane.getText().isBlank()) {
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileToSave))) {
                fileWriter.write(textPane.getText());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}