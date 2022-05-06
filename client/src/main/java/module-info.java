module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    exports dipada.client.controller;
    opens dipada.client.controller to javafx.fxml;

    exports dipada.client.model;
    opens dipada.client.model to javafx.fxml;

    exports dipada.client;
    opens dipada.client to javafx.fxml;

}