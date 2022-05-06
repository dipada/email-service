module com.example.server {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    exports dipada.server.controller;
    opens dipada.server.controller to javafx.fxml;

    exports dipada.server;
    opens dipada.server to javafx.fxml;

}