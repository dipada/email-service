module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    exports prog.dipada.controller;
    opens prog.dipada.controller to javafx.fxml;

    exports prog.dipada.model;
    opens prog.dipada.model to javafx.fxml;

    exports prog.dipada;
    opens prog.dipada to javafx.fxml;
    exports prog.dipada.lib;
    opens prog.dipada.lib to javafx.fxml;

}