package prog.dipada.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import prog.dipada.lib.Server;
import prog.dipada.model.Log;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ServerMainWindowController{

    private Log log;
    @FXML
    private ListView<String> loglst;

    public void initMainViewController(Server server){
        this.log = server.getLog();
        loglst.itemsProperty().bind(log.getLogProperty());
    }
}
