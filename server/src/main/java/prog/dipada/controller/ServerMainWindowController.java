package prog.dipada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import prog.dipada.lib.Server;
import prog.dipada.model.Log;

public class ServerMainWindowController{

    @FXML
    private ListView<String> loglst;

    public void initMainViewController(Server server){
        Log log = server.getLog();
        loglst.itemsProperty().bind(log.getLogProperty());
    }
}
