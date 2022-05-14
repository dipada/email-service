package prog.dipada.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import prog.dipada.ClientApp;
import prog.dipada.model.Client;
import prog.dipada.model.Email;

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
    private Stage primaryStage;
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
        if(selectedEmail != null) {
            if (clientApp.getConnectionHandler().deleteEmail(selectedEmail)) {
                generatePopup("Email correctly deleted", "green");
                selectedEmail = null;
                updateDetailView(emptyEmail);
            } else {
                generatePopup("Can't delete email", "red");
            }
        }else{
            generatePopup("Error deleting. Select an email", "red");
        }

        //model.deleteEmail(selectedEmail);
        //selectedEmail = null;
        //updateDetailView(emptyEmail);
    }

    private void updateDetailView(Email email){
            if (email != null) {
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


    public void setMainWindowController(ClientApp clientApp, Stage primaryStage) {
        this.clientApp = clientApp;
        this.primaryStage = primaryStage;

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
        clientThread = new Thread(this::refreshList);
        clientThread.start();
    }

    public void setStop(boolean stop) {
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
        clientApp.getConnectionHandler().requestAll();
        while (!stop) {
            // TODO show pop up nuova email
            Platform.runLater(()->{
                final int actEmails = clientApp.getClient().getInboxTotalNumProperty().getValue();
                clientApp.getConnectionHandler().requestAll();
                System.out.println("Ci sono " + actEmails + " emails");
                System.out.println("Valore " + clientApp.getClient().getInboxTotalNumProperty().getValue() + " act " + actEmails);
                if(clientApp.getClient().getInboxTotalNumProperty().getValue() > actEmails){
                    generatePopup("New email received!", "blue");
                }
            });

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onNewButtonClick(ActionEvent actionEvent) {
        System.out.println("NEW BUTTON CLICK");
        Email email = new Email(clientApp.getClient().getUserEmailProperty().getValue(), "", null, "", new Date());
        if (clientApp.showSendEmailWindow(email))
            generatePopup("Email sent", "green");
    }

    private void generatePopup(String contentText, String color) {
        Label label = new Label(contentText);
        Popup popup = new Popup();
        label.setBackground(Background.fill(Color.WHITESMOKE));
        label.setStyle("-fx-border-color: " + color + "; -fx-font-size: 40; -fx-text-alignment: center;-fx-border-width: 4");
        if (color.equals("red"))
            label.setTextFill(Color.RED);
        else if (color.equals("green"))
            label.setTextFill(Color.GREEN);
        else
            label.setTextFill(Color.BLUE);

        popup.getContent().add(label);
        label.setMinWidth(200);
        label.setMinHeight(50);

        popup.setY(primaryStage.getY());
        popup.setX(primaryStage.getX());

        popup.autoHideProperty().setValue(true);
        popup.show(primaryStage);
    }
}
