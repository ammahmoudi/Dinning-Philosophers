module com.amg.dinningphilosophers {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires com.fasterxml.jackson.databind;


    opens com.amg.dinningphilosophers to javafx.fxml;
    exports com.amg.dinningphilosophers;
    exports com.amg.dinningphilosophers.request to com.fasterxml.jackson.databind;
    exports com.amg.dinningphilosophers.response to com.fasterxml.jackson.databind;
}