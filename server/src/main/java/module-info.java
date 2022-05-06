module com.example.server {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens dipada.server to javafx.fxml;
    exports dipada.server;
}