package dipada.server.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class ServerMainWindowController {
    @FXML
    private ListView<String> loglst;

    public void initialize(){
        System.out.println("Server controller");
    }
}
