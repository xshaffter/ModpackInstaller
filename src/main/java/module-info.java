module com.paramada.modpackinstaller {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.net.http;
    requires org.json;

    opens com.paramada.modpackinstaller to javafx.fxml;
    exports com.paramada.modpackinstaller;
}