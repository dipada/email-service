module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens dipada.client to javafx.fxml;
    exports dipada.client;
}