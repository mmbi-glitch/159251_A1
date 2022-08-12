module com.c159251.a1.jtexteditor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.typicons;
    requires odfdom.java;
    requires java.xml;
    opens com.c159251.a1.jtexteditor to javafx.fxml;
    exports com.c159251.a1.jtexteditor;
}