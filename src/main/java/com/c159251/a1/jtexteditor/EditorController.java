package com.c159251.a1.jtexteditor;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.controlsfx.control.action.Action;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
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
import java.util.*;
import java.io.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** This class is connected with the fxml config file and is responsible for the main program logic. **/

public class EditorController {

    @FXML
    public Button settingsButton;
    @FXML
    private Menu fileMenu;
    @FXML
    private CodeArea textPane;
    @FXML
    private Button timeStampBtn;
    private String timeStamp;
    @FXML
    private MenuItem newFile;
    @FXML
    private Button searchForNextBtn;

    private File selectedFile;
    private String selectedFileExtension;
    private Clipboard systemClipboard;
    private String clipboardText;
    private int elapsedSeconds;
    private Boolean changesMade;
    private Date saveTime;

    private static Config CONFIG = new Config();

    @FXML
    private Label wordCounts;
    @FXML
    private Label saveStatus;
    @FXML
    private Label timer;
    @FXML
    private MenuItem closeFile;
    @FXML
    private MenuItem openFile;
    @FXML
    private MenuItem saveFile;
    @FXML
    private MenuItem saveFileAs;
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

    private Scene currentScene;

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");

    private int searchCount;
    private int selectCount;
    private ArrayList<Integer> selectFrom;
    private ArrayList<Integer> selectTo;

    private Timeline secondsTimer = new Timeline(new KeyFrame(
            Duration.millis(1000),
            ae -> setTimerText()));

    private YesNoCancel alert;



    // ---------------------------- initializing method --------------------------------------- /

    protected void initialize() {
        systemClipboard = Clipboard.getSystemClipboard();
        // config applies details

        searchBar.setManaged(false);
        searchBar.setVisible(false);
        selectFrom = new ArrayList<>();
        selectTo = new ArrayList<>();
        wordCounts.setText("Words 0:0 Chars");
        timer.setText("00:00:00");
        secondsTimer.setCycleCount(Animation.INDEFINITE);
        secondsTimer.play();
        setNewStatus();
        this.setConfigs();

        //set up listener for text pane changes
        selectedFileExtension = "";
        textPane.textProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedFileExtension.equals("")) {
                textPane.getStylesheets().remove(EditorLauncher.class.getResource("cpp-code.css").toExternalForm());
                textPane.getStylesheets().remove(EditorLauncher.class.getResource("java-code.css").toExternalForm());
            }
            if (selectedFileExtension.equals(".cpp")) {
                textPane.getStylesheets().remove(EditorLauncher.class.getResource("java-code.css").toExternalForm());
                textPane.getStylesheets().add(EditorLauncher.class.getResource("cpp-code.css").toExternalForm());
                applyHighlighting(SyntaxHighlighter.CPP_PATTERN);
            }
            if (selectedFileExtension.equals(".java")) {
                textPane.getStylesheets().remove(EditorLauncher.class.getResource("cpp-code.css").toExternalForm());
                textPane.getStylesheets().add(EditorLauncher.class.getResource("java-code.css").toExternalForm());
                applyHighlighting(SyntaxHighlighter.JAVA_PATTERN);
            }
            setChangedStatus();
            setWordCountLabel();
        });
        textPane.setParagraphGraphicFactory(LineNumberFactory.get(textPane));
        textPane.getStylesheets().add(EditorLauncher.class.getResource("base.css").toExternalForm());
    }

    // ---------------------------- getters ----------------------------------- //

    public String getClipboardText() {
        return clipboardText;
    }

    public CodeArea getTextPane() {
        return textPane;
    }

    public Label getSearchMatchesLabel() {
        return searchMatches;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public void setTitle(String newFileName) {
        Stage thisStage = (Stage) textPane.getScene().getWindow();
        thisStage.setTitle(newFileName + " - JText Editor");
    }

    public static Config getCONFIG() { return EditorController.CONFIG; }

    public String getStatusInfo() {
        return saveStatus.getText();
    }

    public Animation.Status getTimerRunning() {
        return secondsTimer.getStatus();
    }

    public String getTimeStamp(){
        return timeStamp;
    }



    public javafx.scene.Node getYesBtn() {
        return alert.getDialogPane().lookupButton(ButtonType.YES);
    }

    public javafx.scene.Node getNoBtn() {
        return alert.getDialogPane().lookupButton(ButtonType.NO);
    }

    public Menu getFileMenu() {
        return fileMenu;
    }

    public javafx.scene.Node getCancelBtn() {
        return alert.getDialogPane().lookupButton(ButtonType.CANCEL);
    }

    // ------------------------- FILE MENU new and new window methods -------------------------------------- //

    // this should clear the text in the text area
    public void onFileNew() {
        if(changesMade) {
            alert = new YesNoCancel();
            alert.setHeaderText("Do you want to save before clearing?");
            alert.showAndWait().ifPresent(response -> {
                if(Objects.equals(response, ButtonType.YES)) {
                    onFileSave();
                    textPane.clear();
                    setSelectedFile(null);
                    selectedFileExtension = "";
                    setTitle("NEW FILE");
                    setNewStatus();
                }
                if (!Objects.equals(response, ButtonType.CANCEL)) {
                    textPane.clear();
                    setSelectedFile(null);
                    selectedFileExtension = "";
                    setTitle("NEW FILE");
                    setNewStatus();
                }
            });
        }
        else {
            textPane.clear();
        }
    }

    // this should open a new program window
    public void onFileNewWindow() {
        try {
            new EditorLauncher().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    // ---------------------------- FILE MENU close and open methods  --------------------------------------- /


    @FXML
    protected void onFileClose() {
        //add test for unsaved
        if(changesMade) {
            alert = new YesNoCancel();
            alert.setHeaderText("Do you want to save before exiting?");
            alert.showAndWait().ifPresent(response -> {
                if(Objects.equals(response, ButtonType.YES)) {
                    onFileSave();
                }
                if(!Objects.equals(response, ButtonType.CANCEL)) {
                    Stage.getWindows().get(Stage.getWindows().size() - 1).hide();
                }
            });
        }
        else {
            Stage.getWindows().get(Stage.getWindows().size() - 1).hide();
        }
    }


    @FXML
    protected void onFileOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plain Text (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("OpenDocument Text (*.odt)", "*.odt"),
                new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"),
                new FileChooser.ExtensionFilter("Java File (*.java)", "*.java"),
                new FileChooser.ExtensionFilter("C++ File (*.cpp)", "*.cpp")
        );
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            setTitle(selectedFile.getName());
            selectedFileExtension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
            if (selectedFileExtension.equals(".odt")) {
                loadTextFromOdtFile(selectedFile);
                setOpenStatus();
                return;
            }
            if (selectedFileExtension.equals(".pdf")) {
                loadTextFromPdfFile(selectedFile);
                setOpenStatus();
                return;
            }
            loadTextFromTxtFile(selectedFile);
            setOpenStatus();
        }
    }

    // ---------------------------- 'loading text from file' methods --------------------------------------- /

    protected void loadTextFromPdfFile(File fileToLoad) {
        try (PDDocument document = PDDocument.load(fileToLoad)) {
            PDFTextStripper extractor = new PDFTextStripper();
            String fileToText = extractor.getText(document);
            if (!fileToText.isBlank()) {
                textPane.replaceText(fileToText);
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
                textPane.replaceText(fileToText.toString());
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
                textPane.replaceText(fileToText.toString());
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    // ---------------------------------------- STATUS BAR Updaters  --------------------------------------- /


    //setChangesMade sets a flag to show changes have been made, and closing without saving will lose changes
    protected void setChangedStatus() {
        this.changesMade = true;

        if(selectedFile == null) {
            this.saveStatus.setText("Not Yet Saved - created " + dateFormatter.format(saveTime));
        } else {
            this.saveStatus.setText("Unsaved Changes - last saved " + dateFormatter.format(saveTime));
        }

    }

    //sets the text on the timer from window open
    protected void setTimerText() {
        elapsedSeconds += 1;
        timer.setText(String.format("%02d:%02d:%02d",(elapsedSeconds/3600),(elapsedSeconds % 3600) / 60,elapsedSeconds % 60));
    }

    //sets flag to show that no changes have been made, and it is safe to close without losing work
    protected void setSavedStatus() {
        setUnchangedFlags();
        this.saveStatus.setText("Saved at " + dateFormatter.format(saveTime));
        setTitle(selectedFile.getName());
    }

    protected void setNewStatus() {
        setUnchangedFlags();
        this.saveStatus.setText("Created at " + dateFormatter.format(saveTime));
    }

    protected void setOpenStatus() {
        setUnchangedFlags();
        this.saveStatus.setText("Opened at " + dateFormatter.format(saveTime));
        setTitle(selectedFile.getName());
    }

    protected void setUnchangedFlags() {
        this.saveTime = new Date();
        this.changesMade = false;
    }

    protected void setWordCountLabel() {
        wordCounts.setText(countWords());
    }

    protected String countWords() {
        return "Words " + textPane.getText().split("\\s+").length + ":" + textPane.getText().length() + " Chars";
    }

    // -------------------------- Syntax Highlighting Updater ---------------- //

    protected void applyHighlighting(Pattern pattern) {
        String text = textPane.getText();
        StyleSpans<Collection<String>> styledText = computeHighlighting(text, pattern);
        textPane.setStyleSpans(0, styledText);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                        matcher.group("FUNCTION") != null ? "function" :
                            matcher.group("CAPWORD") != null ? "capword" :
                                matcher.group("LIBRARY") != null ? "library" :
                                    matcher.group("ANNOTATION") != null ? "annotation" :
                                        matcher.group("NUMBER") != null ? "number" :
                                            matcher.group("PAREN") != null ? "paren" :
                                                matcher.group("BRACE") != null ? "brace" :
                                                    matcher.group("BRACKET") != null ? "bracket" :
                                                        matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                matcher.group("COMMENT") != null ? "comment" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    // ------------------------- EDIT MENU & BUTTON cut/copy/paste/search/datetime methods -------------------------- /

    @FXML
    protected void addTimeStamp() {
        timeStamp = dateFormatter.format(new Date());
        textPane.replaceText(timeStamp + "\n\n" + textPane.getText());
    }

    @FXML
    protected void cutText() {
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


    @FXML
    protected void copyText() {
        ClipboardContent content = new ClipboardContent();
        content.putString(textPane.getSelectedText());
        systemClipboard.setContent(content);
        clipboardText = systemClipboard.getString();
    }

    @FXML
    protected void pasteText() {
        if (!systemClipboard.getString().isBlank()) {
            // fixing paste issue where the caret position keeps moving
            int caretPos = textPane.getCaretPosition(); // storing the caret position
            textPane.insertText(caretPos, systemClipboard.getString());
            onSearchTextChanged(); // this function messes with the caret position
            textPane.displaceCaret(caretPos + systemClipboard.getString().length()); // resetting the caret position
        }
    }

    @FXML
    protected void searchText() {
        searchBar.setManaged(true);
        searchBar.setVisible(true);
    }

    @FXML
    protected void onSearchTextChanged() {
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
        int searchFrom; //removed the initialisation on intelliJ recommendation
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

    @FXML
    protected void searchForNext() {
        selectCount++;
        if (selectCount == searchCount) {
            selectCount = 0;
        }
        if (!selectFrom.isEmpty() && !selectTo.isEmpty()) {
            textPane.selectRange(selectFrom.get(selectCount), selectTo.get(selectCount));
            searchMatches.setText((selectCount + 1) + " of " + searchCount + " matches");
        }
    }

    @FXML
    protected void searchForPrevious() {
        selectCount--;
        if (selectCount == -1) {
            selectCount = searchCount - 1;
        }
        if (!selectFrom.isEmpty() && !selectTo.isEmpty()) {
            textPane.selectRange(selectFrom.get(selectCount), selectTo.get(selectCount));
            searchMatches.setText((selectCount + 1) + " of " + searchCount + " matches");
        }
    }

    @FXML
    protected void closeSearch() {
        searchBar.setManaged(false);
        searchBar.setVisible(false);
    }


    // ---------------------------- FILE MENU save and save as methods ------------------------------------ /

    protected File getSelectedFile() {
        return this.selectedFile;
    }

    protected void setSelectedFile(File file) {
        this.selectedFile = file;
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
            setSavedStatus();
            return;
        }
        setSavedStatus();
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
                setSavedStatus();
                return;
            }
            if (selectedFile.getName().contains(".pdf")) {
                saveTextToPdfFile(selectedFile);
                setSavedStatus();
                return;
            }
            saveTextToTxtFile(selectedFile);
            setSavedStatus();
        }
    }

    // ---------------------------- 'saving text to file' methods --------------------------------------- /

    protected void saveTextToPdfFile(File fileToSave) {
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

    protected void saveTextToOdtFile(File fileToSave) {
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

    protected void saveTextToTxtFile(File fileToSave) {
        //if the text is not blank, then write text to file
        if (!textPane.getText().isBlank()) {
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileToSave))) {
                fileWriter.write(textPane.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // --------------------- File MENU print method ------------------- //
    @FXML
    protected void onFilePrint() {
        TextFlow printArea = new TextFlow(new Text(textPane.getText()));
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob.showPageSetupDialog(textPane.getScene().getWindow())) {
            PageLayout pageLayout = printerJob.getJobSettings().getPageLayout();
            printArea.setMaxWidth(pageLayout.getPrintableWidth());
            Stage printStage = new Stage();
            printStage.setScene(new Scene(new FlowPane(printArea)));
            printStage.show();
            double totalPrintHeight = printStage.getHeight();
            printStage.hide();
            int allPages = 1;
            while (totalPrintHeight > 0) {
                totalPrintHeight -= pageLayout.getPrintableHeight();
                allPages++;
            }
            int numOfPagesToPrint;
            boolean printSuccess = false;
            if (printerJob.showPrintDialog(textPane.getScene().getWindow())) {
                if (printerJob.getJobSettings().getPageRanges() == null) {
                    numOfPagesToPrint = allPages;
                }
                else {
                    numOfPagesToPrint = printerJob.getJobSettings().getPageRanges()[0].getEndPage() + 1;
                }
                for (int i = 1; i < numOfPagesToPrint; i++) {
                    printSuccess = printerJob.printPage(printArea);
                    printArea.setTranslateY(-1 * pageLayout.getPrintableHeight() * i);
                }
                if (printSuccess) {
                    printerJob.endJob();
                }
            }
        }
    }



    // --------------------------------- help MENU about method -------------------------//
    @FXML
    protected void openAbout() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("aboutwindow-layout.fxml"));
        try {
            Parent root = loader.load();
            Stage stageAbout = new Stage();
            stageAbout.setTitle("About This Project");
            stageAbout.setScene(new Scene(root,600,400));
            stageAbout.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // --------------------------------- config application  -------------------------//

    private void setConfigs() {

        textPane.setStyle("-fx-font-size: " + CONFIG.getFontSize() + "pt;");

    }

    public void editSettings(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("configwindow-layout.fxml"));
        try {
            Parent root = loader.load();
            Stage stageAbout = new Stage();
            stageAbout.setTitle("Configuration");
            stageAbout.setScene(new Scene(root,540,160));
            stageAbout.showAndWait();
            setConfigs();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}