module com.amg.dinningroom {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;


    opens com.amg.dinningroom to javafx.fxml;
    exports com.amg.dinningroom;
    exports com.amg.dinningroom.request to com.fasterxml.jackson.databind;
    exports com.amg.dinningroom.response to com.fasterxml.jackson.databind;
    exports com.amg.dinningroom.models to com.fasterxml.jackson.databind;


}