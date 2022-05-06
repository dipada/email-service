module com.example.server {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.server to javafx.fxml;
    exports com.example.server;
}