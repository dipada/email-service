package prog.dipada.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import prog.dipada.ClientApp;
import prog.dipada.model.Client;
import prog.dipada.model.Email;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainWindowController {


    @FXML
    private GridPane gridPane;
    @FXML
    private ButtonBar btnBar;
    @FXML
    private HBox bottomHbox;
    @FXML
    private VBox bottomVbox;
    @FXML
    private VBox topVbox;
    @FXML
    private HBox tophbox;
    @FXML
    private Label lblTotInbox;
    @FXML
    private Label lblTotOutbox;
    @FXML
    private Label lblSerStatus;
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

    public void onNewButtonClick(ActionEvent actionEvent) {
        new Thread(() -> Platform.runLater(() -> {
            Email email = new Email(clientApp.getClient().getUserEmailProperty().getValue(), "", null, "", new Date());
            if (clientApp.showSendEmailWindow(email))
                generatePopup("Email sent", "green");
        })).start();
    }

    public void onDeleteButtonClick(MouseEvent mouseEvent) {
        System.out.println("Delete button clicked");
        new Thread(() -> Platform.runLater(() -> {
            if (selectedEmail != null) {
                if (clientApp.getConnectionHandler().deleteEmail(selectedEmail)) {
                    generatePopup("Email correctly deleted", "green");
                    selectedEmail = null;
                    updateDetailView(emptyEmail);
                } else {
                    generatePopup("Can't delete email server offline", "red");
                }
            } else {
                generatePopup("Error deleting. Select an email", "red");
            }
        })).start();
    }

    public void onForwardButtonClick(ActionEvent actionEvent) {

        new Thread(() -> Platform.runLater(() -> {
            if (selectedEmail != null) {
                System.out.println("FORWARD BUTTON CLICK");
                Email email = new Email(clientApp.getClient().getUserEmailProperty().getValue(),
                        "[FWD]: " + selectedEmail.getSubject(),
                        null,
                        "-------- Forwarded Message --------\n" +
                                "Subject: " + selectedEmail.getSubject() + "\n" +
                                "Date: " + selectedEmail.getDateToString() + "\n" +
                                "From: " + selectedEmail.getSender() + "\n" +
                                "To: " + selectedEmail.getReceivers() + "\n",
                        new Date());
                if (clientApp.showSendEmailWindow(email))
                    generatePopup("Email forwarded", "green");
            } else {
                generatePopup("Can't forward. Select an email", "red");
            }
        })).start();
    }

    public void onReplyButtonClick(ActionEvent actionEvent) {
        new Thread(() -> Platform.runLater(() -> {
            if (selectedEmail != null) {
                selectedEmail.setReceivers(Collections.singletonList(selectedEmail.getSender()));
                Email email = new Email(clientApp.getClient().getUserEmailProperty().getValue(),
                        "[RE]: " + selectedEmail.getSubject(),
                        selectedEmail.getReceivers(),
                        "On " + selectedEmail.getDateToString() + ", " +
                                selectedEmail.getSender() + " wrote: \n" +
                                selectedEmail.getMessageText(),
                        new Date());
                if (clientApp.showSendEmailWindow(email))
                    generatePopup("Emails sent", "green");
            } else {
                generatePopup("Can't reply. Select an email", "red");
            }
        })).start();
    }

    public void onReplyAllButtonClick(ActionEvent actionEvent) {
        new Thread(() -> Platform.runLater(() -> {
            if (selectedEmail != null) {
                selectedEmail.removeFromReceivers(clientApp.getClient().getUserEmailProperty().getValue());
                Email email = new Email(clientApp.getClient().getUserEmailProperty().getValue(),
                        "[RE]: " + selectedEmail.getSubject(),
                        selectedEmail.getReceivers(),
                        "On " + selectedEmail.getDateToString() + ", " +
                                selectedEmail.getSender() + " wrote: \n" +
                                selectedEmail.getMessageText(),
                        new Date());
                if (clientApp.showSendEmailWindow(email))
                    generatePopup("Emails sent", "green");
            } else {
                generatePopup("Can't reply-all. Select an email", "red");
            }
        })).start();
    }

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

    private void updateDetailView(Email email) {
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

        lstInboxEmail.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Email>() {
            @Override
            public void changed(ObservableValue<? extends Email> observableValue, Email email, Email t1) {
                btnReplyAll.setDisable(t1 != null && t1.getReceivers().size() < 2);
            }
        });

        lstOutboxEmail.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Email>() {
            @Override
            public void changed(ObservableValue<? extends Email> observableValue, Email email, Email t1) {
                btnReplyAll.setDisable(t1 != null && t1.getReceivers().size() < 2);
            }
        });

        lstInboxEmail.setCellFactory(cell -> new ListCell<>(){
            @Override
            protected void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if(email == null || empty)
                    setText(null);
                else
                    setText("\u2198 From: "+ email.getSender() + " Subject: " + email.getSubject() + " Date: " + email.getDateToString());
            }
        });

        lstOutboxEmail.setCellFactory(cell -> new ListCell<>(){
            @Override
            protected void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if(email == null || empty)
                    setText(null);
                else
                    setText("\u2199 Subject: " + email.getSubject() + " To: " + email.getReceivers() + " Date: " + email.getDateToString());
            }
        });

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
        setLabelStatus();
        clientApp.getConnectionHandler().requestAll();
        while (!stop) {
            Platform.runLater(() -> {
                final int actEmailsInbox = clientApp.getClient().getInboxTotalNumProperty().getValue();
                final int actEmailsOutbox = clientApp.getClient().getOutboxTotalNumProperty().getValue();
                if (clientApp.getConnectionHandler().requestAll()) {
                    setStatusColor(Color.GREEN);
                } else {
                    setStatusColor(Color.RED);
                }
                System.out.println("Ci sono " + actEmailsInbox + " emails");
                System.out.println("Valore " + clientApp.getClient().getInboxTotalNumProperty().getValue() + " act " + actEmailsInbox);
                if (clientApp.getClient().getInboxTotalNumProperty().getValue() > actEmailsInbox) {
                    generatePopup("New email received!", "blue");
                    lstInboxEmail.getSelectionModel().selectPrevious();
                }
            });

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setLabelStatus() {
        lblSerStatus.setText("");
        lblSerStatus.setMinHeight(15);
        lblSerStatus.setMaxHeight(15);
        lblSerStatus.setMinWidth(15);
        lblSerStatus.setMaxWidth(15);
    }

    private void setStatusColor(Color color) {
        CornerRadii corn = new CornerRadii(10);
        Background background = new Background(new BackgroundFill(color, corn, null));
        lblSerStatus.setBackground(background);
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
