package prog.dipada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import prog.dipada.lib.Server;
import prog.dipada.model.Log;

public class ServerMainWindowController{

    private Log log;
    @FXML
    private ListView<String> loglst;

    public void initMainViewController(Server server){
        this.log = server.getLog();
        loglst.itemsProperty().bind(log.getLogProperty());
    }
}
