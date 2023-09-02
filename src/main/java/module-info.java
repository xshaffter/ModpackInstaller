module com.paramada.modpackinstaller {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.net.http;
    requires org.json;
    requires maven.model;
    requires plexus.utils;

    opens com.paramada.modpackinstaller to javafx.fxml;
    exports com.paramada.modpackinstaller;
}