package dipada.client.controller;

import dipada.client.ClientApp;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class LoginController {
    private ClientApp clientApp;
    private Stage stage;

    public void initialize(){
        System.out.println("hello login controller");
    }

    public void onLoginButtonClick(ActionEvent actionEvent) {
        // TODO effettuare login
    }

    public void setLoginController(ClientApp clientApp, Stage stage) {
        this.clientApp = clientApp;
        this.stage = stage;
    }
}
