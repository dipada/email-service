package prog.dipada.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import prog.dipada.ClientApp;
import prog.dipada.model.Email;
import prog.dipada.model.Client;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.Date;
import java.util.List;

public class MainWindowController {

    @FXML
    private Label lblTotInbox;
    @FXML
    private Label lblTotOutbox;
    @FXML
    private Button btnForward;
    @FXML
    private Button btnReplyAll;
    @FXML
    private Button btnREply;
    @FXML
    private Button btnNewEmail;
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
    private Thread clientThread;
    private boolean stop;

    private void showSelectedEmailInbox(MouseEvent mouseEvent) {
        Email email = lstInboxEmail.getSelectionModel().getSelectedItem();
        selectedEmail = email;
        updateDetailView(email);
    }

    private void showSelectedEmailOutbox(MouseEvent mouseEvent) {
        Email email = lstOutboxEmail.getSelectionModel().getSelectedItem();
        selectedEmail = email;
        updateDetailView(email);
    }

    public void onDeleteButtonClick(MouseEvent mouseEvent) {
        System.out.println("Sender " + selectedEmail.getSender());
        // TODO ridefinire il metodo deleteEmail per eliminare le email inviate/ricevute
        clientApp.getClient().deleteEmail(selectedEmail);
        selectedEmail = null;
        updateDetailView(emptyEmail);

        //model.deleteEmail(selectedEmail);
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


    public void setMainWindowController(ClientApp clientApp) {
        this.clientApp = clientApp;


        lblUsername.textProperty().bind(clientApp.getClient().getUserEmailProperty());

        lstInboxEmail.itemsProperty().bind(clientApp.getClient().getInboxProperty());
        lstOutboxEmail.itemsProperty().bind(clientApp.getClient().getOutboxProperty());

        lblTotInbox.textProperty().bind(clientApp.getClient().getInboxTotalNumProperty().asString());
        lblTotOutbox.textProperty().bind(clientApp.getClient().getOutboxTotalNumProperty().asString());

        lstInboxEmail.setOnMouseClicked(this::showSelectedEmailInbox);
        lstOutboxEmail.setOnMouseClicked(this::showSelectedEmailOutbox);

        selectedEmail = null;

        emptyEmail = new Email("", "", List.of(""), "", null);
        updateDetailView(emptyEmail);

        stop = false;
        clientThread = new Thread(()->refreshList());
        clientThread.start();
    }

    public void setStop(boolean stop){
        System.out.println("->\tStopping main window");
        this.stop = stop;
        System.out.println("->\tWaiting for refresh thread to end");
        try {
            clientThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("->\tMain window exit successful");
    }

    private void refreshList() {
        while (!stop) {
            // TODO show pop up errore server
            Platform.runLater(()->{clientApp.getConnectionHandler().requestAll();});
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void onNewButtonClick(ActionEvent actionEvent) {
        System.out.println("NEW BUTTON CLICK");
        Email email = new Email(clientApp.getClient().getUserEmailProperty().getValue(), "", null, "", new Date());
        clientApp.showSendEmailWindow(email);
    }
}
