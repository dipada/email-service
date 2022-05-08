package prog.dipada.controller;

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
    private ConnectionHandler connection;
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
                //connection = new ConnectionHandler("localhost", 8989);
                //TODO operazioni
                System.out.println("Client è " + client);
                System.out.println("Connection è " + connection);
                client.setUserEmailProperty(emailUserLogin);
                connection.setIdConnection(emailUserLogin);
                if(connection.authUser(emailUserLogin)){
                    System.out.println("Utente sul server esistente connessione andata a buon fine");
                    // TODO controllo sul server dell'email
                    if(connection.requestAll()){
                        // TODO verifica utente esiste
                        System.out.println("Richiesta andata a buon fine");
                        stage.close();
                    }else{
                        System.out.println("Richiesta non andata a buon fine");
                    }
                }else{
                    // TODO utente non accettato dal server
                    System.out.println("Utente non accettato dal serverr ");
                    generateErrorAlert("Error login", emailUserLogin + " does not exist", "Insert an existing email");
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

    public void setLoginController(ClientApp clientApp, Stage stage, Client client, ConnectionHandler connection) {
        this.clientApp = clientApp;
        this.stage = stage;
        this.client = client;
        this.connection = connection;
    }
}
