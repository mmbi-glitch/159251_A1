package com.c159251.a1.jtexteditor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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
import java.util.Date;

import java.io.*;

/** This class is connected with the fxml config file and is responsible for the main program logic. **/

public class EditorController {

    private File selectedFile;
    private Clipboard systemClipboard;
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

    private SimpleDateFormat formatter;
    public int selectFrom;
    public int selectTo;

    @FXML
    public void initialize() {
        systemClipboard = Clipboard.getSystemClipboard();
        // append date and time to text pane
        formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");
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
            loadTextFromFile(selectedFile);
        }
    }

    protected void loadTextFromPdfFile(File fileToLoad) {
        try (PDDocument document = PDDocument.load(fileToLoad)) {
            PDFTextStripper extractor = new PDFTextStripper();
            String fileToText = extractor.getText(document);
            if (!fileToText.isBlank()) {
                textPane.setText(formatter.format(new Date()));
                textPane.appendText("\n\n" + fileToText);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadTextFromOdtFile(File fileToLoad) {
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
                textPane.setText(formatter.format(new Date()));
                textPane.appendText("\n\n" + fileToText);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        //fixing this particular action, it needs the anchor value (anchor and caretPosition make up the selection range)
        selectFrom = Math.min(textPane.getCaretPosition(),textPane.getAnchor());
        selectTo = Math.max(textPane.getCaretPosition(),textPane.getAnchor());
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