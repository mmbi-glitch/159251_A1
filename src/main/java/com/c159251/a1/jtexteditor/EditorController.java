package com.c159251.a1.jtexteditor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextExtractor;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.Node;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.io.*;

/** This class is connected with the fxml config file and is responsible for the main program logic. **/

public class EditorController {

    @FXML
    private Button searchForNextBtn;

    private File selectedFile;
    private Clipboard systemClipboard;
    private String clipboardText;
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
    @FXML
    private Button searchBtn;
    @FXML
    private HBox searchBar;
    @FXML
    private TextField searchField;
    @FXML
    private Label searchMatches;

    private SimpleDateFormat formatter;

    private int searchCount;
    private int selectCount;
    ArrayList<Integer> selectFrom;
    ArrayList<Integer> selectTo;


    // ---------------------------- initializing method --------------------------------------- /
    @FXML
    public void initialize() {
        systemClipboard = Clipboard.getSystemClipboard();
        // append date and time to text pane
        formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        textPane.setText(formatter.format(new Date()));
        textPane.appendText("\n\n");
        searchBar.setManaged(false);
        searchBar.setVisible(false);
        selectFrom = new ArrayList<>();
        selectTo = new ArrayList<>();
    }

    // ---------------------------- getters ----------------------------------- //

    public String getClipboardText() {
        return clipboardText;
    }

    public TextArea getTextPane() {
        return textPane;
    }

    public Label getSearchMatchesLabel() {
        return searchMatches;
    }

    public TextField getSearchField() {
        return searchField;
    }

    // ---------------------------- FILE MENU close and open methods  --------------------------------------- /

    @FXML
    public void onFileClose() {
        System.exit(0);
    }

    @FXML
    public void onFileOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plain Text (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("OpenDocument Text (*.odt)", "*.odt"),
                new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf")
        );
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            if (selectedFile.getName().contains(".odt")) {
                loadTextFromOdtFile(selectedFile);
                return;
            }
            if (selectedFile.getName().contains(".pdf")) {
                loadTextFromPdfFile(selectedFile);
                return;
            }
            loadTextFromTxtFile(selectedFile);
        }
    }

    // ---------------------------- 'loading text from file' methods --------------------------------------- /

    public void loadTextFromPdfFile(File fileToLoad) {
        try (PDDocument document = PDDocument.load(fileToLoad)) {
            PDFTextStripper extractor = new PDFTextStripper();
            String fileToText = extractor.getText(document);
            if (!fileToText.isBlank()) {
//                textPane.setText(formatter.format(new Date()));
                textPane.setText(fileToText);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadTextFromOdtFile(File fileToLoad) {
        try (OdfTextDocument document = OdfTextDocument.loadDocument(fileToLoad)) {
            OfficeTextElement root = document.getContentRoot();
            StringBuilder fileToText = new StringBuilder();
            OdfElement element = root.getFirstChildElement();
            OdfTextExtractor extractor;
            while (element != null) {
                extractor = OdfTextExtractor.newOdfTextExtractor(element);
                fileToText.append(extractor.getText()).append("\n");
                root.removeChild(element);
                element = root.getFirstChildElement();
            }
            if (!fileToText.toString().isBlank()) {
//                textPane.setText(formatter.format(new Date()));
                textPane.setText(fileToText.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected void loadTextFromTxtFile(File fileToLoad) {
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

    // ------------------------- EDIT MENU & BUTTON cut/copy/paste/search methods -------------------------- /

    public void cutText() {
        ClipboardContent content = new ClipboardContent();
        String text = textPane.getSelectedText();
        //fixing this particular action, it needs the anchor value (anchor and caretPosition make up the selection range)
        int selectFrom = Math.min(textPane.getCaretPosition(),textPane.getAnchor());
        int selectTo = Math.max(textPane.getCaretPosition(),textPane.getAnchor());
        textPane.deleteText(selectFrom, selectTo);
        content.putString(text);
        systemClipboard.setContent(content);
        clipboardText = systemClipboard.getString();
        onSearchTextChanged();
    }


    public void copyText() {
        ClipboardContent content = new ClipboardContent();
        content.putString(textPane.getSelectedText());
        systemClipboard.setContent(content);
        clipboardText = systemClipboard.getString();
    }

    public void pasteText() {
        if (!systemClipboard.getString().isBlank()) {
            // fixing paste issue where the caret position keeps moving
            int caretPos = textPane.getCaretPosition(); // storing the caret position
            textPane.insertText(caretPos, systemClipboard.getString());
            onSearchTextChanged(); // this function messes with the caret position
            textPane.positionCaret(caretPos + systemClipboard.getString().length()); // resetting the caret position
        }
    }

    public void searchText() {
        searchBar.setManaged(true);
        searchBar.setVisible(true);
    }

    public void onSearchTextChanged() {
        String searchedText = searchField.getText();
        // first, the text could be blank, so we deal with that issue first
        if (searchedText.isBlank()) {
            textPane.selectRange(0,0);
            searchMatches.setText("No matches");
            return;
        }

        // then, if it's not blank, then we need to search for it

        // init vars here
        searchCount = 0;
        int searchFrom = 0;
        int searchTo = textPane.getText().length();
        selectFrom.clear();
        selectTo.clear();

        // first, we see if there's one instance of searched text in the entire text of the textarea
        if (textPane.getText().contains(searchedText)) {
            // searching for the starting index of the searched text
            int searchIndex = textPane.getText().indexOf(searchedText);
            // add the indices to the arraylists
            selectFrom.add(searchIndex);
            selectTo.add(searchIndex + searchedText.length());
            searchFrom = selectTo.get(searchCount);
            searchCount++;
            // then, we check if there are more instances
            // but this time, we only check from the text in the textarea that starts from the ENDING index
            // of the previous found instance of the searched text
            while (textPane.getText(searchFrom, searchTo).contains(searchedText)) {
                // we have to add the searchFrom param here to specify that we want to search from the given index
                searchIndex = textPane.getText().indexOf(searchedText, searchFrom);
                // this is the same as from above - no change
                selectFrom.add(searchIndex);
                selectTo.add(searchIndex + searchedText.length());
                searchFrom = selectTo.get(searchCount);
                searchCount++;
            }
            // select text to the first occurrence
            selectCount = 0;
            textPane.selectRange(selectFrom.get(selectCount), selectTo.get(selectCount));
            searchMatches.setText((selectCount + 1) + " of " + searchCount + " matches");
        }
        else {
            // otherwise, we just set it to none found
            textPane.selectRange(0,0);
            searchMatches.setText("No matches");
        }
    }

    public void searchForNext() {
        selectCount++;
        if (selectCount == searchCount) {
            selectCount = 0;
        }
        if (!selectFrom.isEmpty() && !selectTo.isEmpty()) {
            textPane.selectRange(selectFrom.get(selectCount), selectTo.get(selectCount));
            searchMatches.setText((selectCount + 1) + " of " + searchCount + " matches");
        }
    }

    public void searchForPrevious() {
        selectCount--;
        if (selectCount == -1) {
            selectCount = searchCount - 1;
        }
        if (!selectFrom.isEmpty() && !selectTo.isEmpty()) {
            textPane.selectRange(selectFrom.get(selectCount), selectTo.get(selectCount));
            searchMatches.setText((selectCount + 1) + " of " + searchCount + " matches");
        }
    }

    public void closeSearch() {
        searchBar.setManaged(false);
        searchBar.setVisible(false);
    }


    // ---------------------------- FILE MENU save and save as methods ------------------------------------ /

    @FXML
    protected void onFileSave() {
        //if save is triggered with no stored file, then it should try as a 'save as'
        if (selectedFile == null) {
            onFileSaveAs();
            return;
        }
        if (selectedFile.getName().contains(".odt")) {
            saveTextToOdtFile(selectedFile);
            return;
        }
        saveTextToTxtFile(selectedFile);
    }

    @FXML
    protected void onFileSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plain Text (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("OpenDocument Text (*.odt)", "*.odt"),
                new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf")
        );        // if saved file is null, set to default directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        // otherwise, set to parent directory of saved file
        if (selectedFile != null) {
            fileChooser.setInitialDirectory(selectedFile.getParentFile());
        }
        selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            if (selectedFile.getName().contains(".odt")) {
                saveTextToOdtFile(selectedFile);
                return;
            }
            if (selectedFile.getName().contains(".pdf")) {
                saveTextToPdfFile(selectedFile);
                return;
            }
            saveTextToTxtFile(selectedFile);
        }
    }

    // ---------------------------- 'saving text to file' methods --------------------------------------- /

    public void saveTextToPdfFile(File fileToSave) {
        if (!textPane.getText().isBlank()) {
            try (PdfDocument pdf = new PdfDocument(new PdfWriter(fileToSave))) {
                Document doc = new Document(pdf);
                doc.add(new Paragraph(textPane.getText()));
                doc.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void saveTextToOdtFile(File fileToSave) {
        if (!textPane.getText().isBlank()) {
            try (OdfTextDocument document = OdfTextDocument.newTextDocument()) {
                OfficeTextElement officeText = document.getContentRoot();
                Node childNode = officeText.getLastChild();
                OdfTextParagraph paragraph;
                if (childNode instanceof OdfTextParagraph odfTextParagraph) {
                    paragraph = odfTextParagraph;
                }
                else {
                    paragraph = new OdfTextParagraph(document.getContentDom());
                }
                paragraph.addContentWhitespace(textPane.getText());
                document.save(fileToSave);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void saveTextToTxtFile(File fileToSave) {
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