module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.client to javafx.fxml;
    exports com.example.client;
}