package prog.dipada.controller;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import prog.dipada.ClientApp;
import prog.dipada.model.Email;
import prog.dipada.model.Client;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController {

    @FXML
    private Button btnForward;
    @FXML
    private Button btnReplyAll;
    @FXML
    private Button btnREply;
    @FXML
    private Button btnNewEmail;
    @FXML
    private BorderPane pnlReadMessage;
    @FXML
    private Label lblUsername;
    @FXML
    private ListView<Email> lstInboxEmail;
    @FXML
    private ListView<Email> lstOutboxEmail;
    @FXML
    private Label lblFrom;
    @FXML
    private Label lblTo;
    @FXML
    private Label lblSubject;
    @FXML
    private TextArea txtEmailContent;
    @FXML
    private Button btnDelete;
    @FXML
    private Label lblDate;

    private Client model;
    private Email selectedEmail;
    private Email emptyEmail;
    private ClientApp clientApp;

    private void showSelectedEmail(MouseEvent mouseEvent) {
        Email email = lstInboxEmail.getSelectionModel().getSelectedItem();
        selectedEmail = email;
        //updateDetailView(email);
    }

    public void onDeleteButtonClick(MouseEvent mouseEvent) {
        model.deleteEmail(selectedEmail);
        //selectedEmail = null;
        //updateDetailView(emptyEmail);
    }

    private void updateDetailView(Email email){
        if(email != null){
            lblFrom.setText("From: " + email.getSender());
            lblTo.setText("To: " + String.join(", ", email.getReceivers()));
            lblSubject.setText("Subject: " + email.getSubject());
            txtEmailContent.setText(email.getMessageText());
            lblDate.setText("Date: " + email.getDateToString());
        }
    }

    /*
    public void initialize(){
        if(this.model != null){
            throw new IllegalMonitorStateException("Model can only be initialized once");
        }

        // TODO istanzio nuovo client e genero email
        // TODO vanno caricate da server
        //model = new Client("email@dominio.it");
        model = new Client();
        model.generateEmail();

        selectedEmail = null;

        // bindind property tra ListView (lstEmails) dell'interfaccia e ObservableList del model (inboxContent)
        lstInboxEmail.itemsProperty().bind(model.getInboxProperty());
        lstOutboxEmail.itemsProperty().bind(model.getOutboxProperty());

        // al click del mouse effettua questa azione
        lstInboxEmail.setOnMouseClicked(this::showSelectedEmail);
        lstOutboxEmail.setOnMouseClicked(this::showSelectedEmailOutbox);

        lblUsername.textProperty().bind(model.getUserEmailProperty());

        emptyEmail = new Email( "", "", List.of(""), "", null);
        //inboxTitledPane.setExpanded(true);
        //updateDetailView(emptyEmail);
    }
    */

    private void showSelectedEmailOutbox(MouseEvent mouseEvent) {
        Email email = lstOutboxEmail.getSelectionModel().getSelectedItem();
        selectedEmail = email;
        //updateDetailView(email);
    }


    public void setMainWindowController(ClientApp clientApp) {
        this.clientApp = clientApp;
        lstInboxEmail.itemsProperty().bind(clientApp.getClient().getInboxProperty());
        lstOutboxEmail.itemsProperty().bind(clientApp.getClient().getOutboxProperty());

        lblUsername.textProperty().bind(clientApp.getClient().getUserEmailProperty());

        Thread clientThread = new Thread(()->refreshList());
        clientThread.start();
    }

    private void refreshList() {
        //Platform.runLater(()->System.out.println("ciao"));
        while (true) {
            System.out.println("ciao");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            clientApp.getConnectionHandler().requestAll();
        }
    }
}
