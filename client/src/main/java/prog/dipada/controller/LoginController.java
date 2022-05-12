package prog.dipada.controller;

import javafx.application.Platform;
import prog.dipada.ClientApp;
import prog.dipada.ConnectionHandler;
import prog.dipada.model.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class LoginController {
    private ClientApp clientApp;
    private Stage stage;

    @FXML
    private TextField emailField;
    @FXML
    private Button btnLogin;

    public void onLoginButtonClick() {
        final String emailDomain = "@dipada.it";

        // campo email vuoto
        if(emailField.getText().length() == 0 || emailField == null) {
            generateErrorAlert("Error login", "Email field empty", "Insert an email");
            return;
        }

        String emailUserLogin = emailField.getText() + emailDomain;

        // l'email non rispetta il pattern
        if(!validateEmail(emailUserLogin)){
            generateErrorAlert("Error login", emailUserLogin + " is invalid", "Valid email:\n" +
                    "- should begin with a letter\n" +
                    "- length need to be almost 4 character\n" +
                    "- can contain only this special character . _");
            return;
        }

        // Email non vuota e rispetta il pattern
        String isUserAccept = clientApp.getConnectionHandler().authUser(emailUserLogin);

        if(isUserAccept != null){
            if(isUserAccept.equals("valid")){
                // user exist
                clientApp.getClient().setUserEmailProperty(emailUserLogin);
                clientApp.getConnectionHandler().setIdConnection(emailUserLogin);
                stage.close();
            }else{
                // user doesn't exist
                generateErrorAlert("Error login", emailUserLogin + " invalid account", "Account does not exist");
            }
        }else {
            // server offline
            generateErrorAlert("Error login", "Server offline", "Server does not respond");
        }
    }

    private boolean validateEmail(String emailUserLogin) {
        // Compiles the given regular expression and attempts to match the given input against it.
        return Pattern.matches("^[a-zA-Z][a-zA-Z0-9._][a-zA-Z0-9._][a-zA-Z0-9._]+@dipada.it",emailUserLogin);
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
