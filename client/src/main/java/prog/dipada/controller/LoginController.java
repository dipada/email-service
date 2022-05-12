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
    //private ConnectionHandler connection;
    //private Client client;
    private Stage stage;

    @FXML
    private TextField emailField;
    @FXML
    private Button btnLogin;

    public void onLoginButtonClick(ActionEvent actionEvent) {
        // TODO effettuare login
        final String emailDomain = "@dipada.it";
        if(emailField.getText().length()>0 && emailField != null){
            String emailUserLogin = emailField.getText() + emailDomain;
            if(validateEmail(emailUserLogin)){
                System.out.println("Client app da login" + clientApp);
                // Email valida richiede login, se il server è online e l'utente esiste verrà fatto login

                String isUserAccept = clientApp.getConnectionHandler().authUser(emailUserLogin);
                if(isUserAccept != null){
                    if(isUserAccept.equals("valid")){
                        // Utente esistente
                        stage.close();
                        /*
                        if(connection.requestAll()){
                            System.out.println("Richiesta andata a buon fine");
                            stage.close();
                        }else{
                            // Il server è offline
                            System.out.println("Richiesta non andata a buon fine");
                            generateErrorAlert("Error logins", "Server offline", "Server does not respond");
                        }*/

                    }else{
                        // L'utente non esiste
                        generateErrorAlert("Error login", emailUserLogin + " invalid account", "Account does not exist");
                    }
                }else{
                    // Il server è offline
                    System.out.println("il server è offline");
                    generateErrorAlert("Error login", "Server offline", "Server does not respond");
                }

            }else{
                //TODO messaggio di errore email scorretta
                generateErrorAlert("Error login", emailUserLogin + " is invalid", "Valid email:\n" +
                        "- should begin with a letter\n" +
                        "- length need to be almost 4 character\n" +
                        "- can contain only this special character . _");
            }
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
        //this.client = client;
        //this.connection = connection;
    }
}
