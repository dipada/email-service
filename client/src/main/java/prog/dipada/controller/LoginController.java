package prog.dipada.controller;

import javafx.scene.layout.AnchorPane;
import prog.dipada.ClientApp;
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
    private AnchorPane rootAnchor;
    @FXML
    private TextField emailField;
    @FXML
    private Button btnLogin;

    public void onLoginButtonClick() {
        final String emailDomain = "@dipada.it";

        // empty email field
        if(emailField.getText().length() == 0 || emailField == null) {
            generateErrorAlert("Email field empty", "Insert an email");
            return;
        }

        String emailUserLogin = emailField.getText() + emailDomain;

        // email doesn't respect pattern
        if(!validateEmail(emailUserLogin)){
            generateErrorAlert(emailUserLogin + " is invalid", """
                    Valid email:
                    - should begin with a letter
                    - length need to be almost 4 character
                    - can contain only this special character . _""");
            return;
        }

        // email valid and not empty
        String isUserAccept = clientApp.getConnectionHandler().authUser(emailUserLogin);

        if(isUserAccept != null){
            if(isUserAccept.equals("valid")){
                // user exist
                clientApp.getClient().setUserEmailProperty(emailUserLogin);
                clientApp.getConnectionHandler().setIdConnection(emailUserLogin);
                stage.close();
            }else{
                // user doesn't exist
                generateErrorAlert(emailUserLogin + " invalid account", "Account does not exist");
            }
        }else {
            // server offline
            generateErrorAlert("Server offline", "Server does not respond");
        }
    }

    private boolean validateEmail(String emailUserLogin) {
        // Compiles the given regular expression and attempts to match the given input against it.
        return Pattern.matches("^[a-zA-Z][a-zA-Z\\d._][a-zA-Z\\d._][a-zA-Z\\d._]+@dipada.it",emailUserLogin);
    }

    private void generateErrorAlert(String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error login");

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
