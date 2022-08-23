package com.c159251.a1.jtexteditor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;


import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class EditorControllerTest {

    EditorController editorController;

    @BeforeEach
    void init() {
        editorController = new EditorController();
    }

    @Test
    void putToClipBoard() {
        //dummy test to make sure I have set up the framework correctly
        assertEquals(1,1);

    }

    // this is just a test to see if it works... and it does seem to
    @Test
    void extractTextFromTxtFile() {
        assertEquals("Hello, my name is Rob.\n", editorController.getTextFromTxtFile(new File("src/test/java/com/c159251/a1/jtexteditor/basic_test.txt")));
    }
}