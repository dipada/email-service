package dipada.client.controller;

import dipada.client.ClientApp;
import dipada.client.model.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class LoginController {
    private ClientApp clientApp;
    private Client client;
    private Stage stage;

    @FXML
    private TextField emailField;
    @FXML
    private Button btnLogin;

    public void onLoginButtonClick(ActionEvent actionEvent) {
        // TODO effettuare login
        final String emailDomain = "@dipada.it";
        if(emailField.getText().length()>0 && emailField != null){
            //TODO controllo email
            String emailUserLogin = emailField.getText() + emailDomain;
            if(validateEmail(emailUserLogin)){
                //TODO operazioni
                System.out.println("Valida " + emailUserLogin);
            }else{
                //TODO messaggio di errore email scorretta
                System.out.println("NOn valida " + emailUserLogin);
            }
            Platform.exit();
            //System.out.println("Client email da login " + client.getUserEmailProperty());
            client.setUserEmailProperty(emailField.getText());
            //System.out.println("Client email da login " + client.getUserEmailProperty());
            //ClientApp.client = client;
            //client.setUserEmailProperty(emailField.getText()+"@dipada.it");
            // TODO effettuare controllo email valida
            if((emailField.getText()+"@dipada.it").equals("dan@dipada.it")){

                // TODO controllo sul server dell'email
                System.out.println("Login effettuato correttamente");
                stage.close();
            }else{
                generateErrorAlert("Error login", "Wrong Email", "Email not valid");
            }
        }
    }

    private boolean validateEmail(String emailUserLogin) {
        // Compiles the given regular expression and attempts to match the given input against it.
        return Pattern.matches("[a-zA-Z][a-zA-Z0-9._]+@dipada.it",emailUserLogin);
    }

    private void generateErrorAlert(String title, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.initOwner(stage);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void setLoginController(ClientApp clientApp, Stage stage, Client client) {
        this.clientApp = clientApp;
        this.stage = stage;
        this.client = client;
    }
}
