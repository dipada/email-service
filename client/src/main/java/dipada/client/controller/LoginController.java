package dipada.client.controller;

import dipada.client.ClientApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    private ClientApp clientApp;
    private Stage stage;

    @FXML
    private TextField emailField;
    @FXML
    private Button btnLogin;

    public void onLoginButtonClick(ActionEvent actionEvent) {
        // TODO effettuare login
        if(emailField.getText().length()>0 && emailField != null){

            // TODO effettuare controllo email valida
            if((emailField.getText()+"@dipada.com").equals("dan@dipada.com")){

                // TODO controllo sul server dell'email
                System.out.println("Login effettuato correttamente");
                stage.close();
            }else{
                generateErrorAlert("Error login", "Wrong Email", "Email not valid");
            }
        }
    }

    private void generateErrorAlert(String title, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.initOwner(stage);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void setLoginController(ClientApp clientApp, Stage stage) {
        this.clientApp = clientApp;
        this.stage = stage;
    }
}
