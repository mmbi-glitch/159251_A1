module com.c159251.a1.jtexteditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires odfdom.java;
    requires java.xml;
    requires org.apache.pdfbox;
    requires org.slf4j;
    requires org.slf4j.simple;
    requires kernel;
    requires pdfa;
    requires io;
    requires barcodes;
    requires styled.xml.parser;
    requires layout;
    requires org.apache.commons.lang3;
    requires javafx.web;
    requires org.snakeyaml.engine.v2;

    requires org.fxmisc.richtext;

    opens com.c159251.a1.jtexteditor to javafx.fxml;
    exports com.c159251.a1.jtexteditor;
}