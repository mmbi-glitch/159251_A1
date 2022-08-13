package com.c159251.a1.jtexteditor;

import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.Charsets;
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

    public static File selectedFile;
    public static Clipboard systemClipboard;
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
    public Label dateTimeLabel;
    public AnchorPane topAnchor;

    @FXML
    public void initialize() {
        systemClipboard = Clipboard.getSystemClipboard();
        //set date and time in menu
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm");
        AnchorPane.setRightAnchor(dateTimeLabel,5.0);
        dateTimeLabel.setText(formatter.format(new Date()));

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
                textPane.setText(fileToText);
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
                fileToText.append(extractor.getText() + "\n");
                root.removeChild(element);
                element = root.getFirstChildElement();
            }
            System.out.println(fileToText.toString());
            System.out.println(fileToText);
            if (!fileToText.isEmpty()) {
                textPane.setText(fileToText.toString());
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
                if (OdfTextParagraph.class.isInstance(childNode)) {
                    paragraph = (OdfTextParagraph) childNode;
                }
                else {
                    paragraph = new OdfTextParagraph(document.getContentDom());
                }
                System.out.println(childNode.getNodeName());
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