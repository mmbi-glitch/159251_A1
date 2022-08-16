package com.c159251.a1.jtexteditor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.*;

/** This class is connected with the fxml config file and is responsible for the main program logic. **/

public class EditorController {

    private static File selectedFile;
    private static Clipboard systemClipboard;
    @FXML
    private MenuItem closeFile;
    @FXML
    private MenuItem openFile;
    @FXML
    private MenuItem saveFile;
    @FXML
    private MenuItem saveFileAs;
    @FXML
    private TextArea textPane;
    @FXML
    private Button cutBtn;
    @FXML
    private Button copyBtn;
    @FXML
    private Button pasteBtn;

    public int selectFrom;
    public int selectTo;

    @FXML
    public void initialize() {
        systemClipboard = Clipboard.getSystemClipboard();
        // append date and time to text pane
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        textPane.setText(formatter.format(new Date()));
        textPane.appendText("\n\n");
    }

    // close program on 'close' button press
    @FXML
    protected void onFileClose() {
        System.exit(0);
    }

    // open txt file on 'open' button press
    @FXML
    public void onFileOpen() {
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

    public void copyText() {
        ClipboardContent content = new ClipboardContent();
        content.putString(textPane.getSelectedText());
        systemClipboard.setContent(content);
    }

    public void cutText() {
        ClipboardContent content = new ClipboardContent();
        String text = textPane.getSelectedText();
        selectFrom = textPane.getCaretPosition() - text.length();
        selectTo = textPane.getCaretPosition();
        textPane.deleteText(selectFrom, selectTo);
        content.putString(text);
        systemClipboard.setContent(content);
    }

    public void pasteText() {
        if (!systemClipboard.getString().isBlank()) {
            textPane.insertText(textPane.getCaretPosition(), systemClipboard.getString());
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