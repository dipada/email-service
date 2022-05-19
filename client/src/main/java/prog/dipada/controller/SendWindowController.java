package prog.dipada.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import prog.dipada.ClientApp;
import prog.dipada.model.Email;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendWindowController {
    @FXML
    private Label lblUserEmail;
    @FXML
    private TextField rcvTextField;
    @FXML
    private TextField subjectTextField;
    @FXML
    private TextArea msgTxtArea;
    @FXML
    private Button sendBtn;
    private ClientApp clientApp;
    private Stage stage;
    private Email email;

    private boolean emailSent = false;

    public void setSendWindowController(ClientApp clientApp, Stage stage) {
        this.clientApp = clientApp;
        this.stage = stage;
    }

    public void setEmail(Email email) {
        this.email = email;

        lblUserEmail.setText(this.email.getSender());
        rcvTextField.setText(formatReceiversComma(this.email.getReceivers()));
        subjectTextField.setText(this.email.getSubject());
        msgTxtArea.setText(this.email.getMessageText());
    }

    private String formatReceiversComma(List<String> receiversEmail) {
        StringBuilder str = new StringBuilder();
        if (receiversEmail == null) {
            return "";
        } else {
            str.append(receiversEmail.get(0));
            if (receiversEmail.size() > 1) {
                for (int i = 1; i < receiversEmail.size(); i++) {
                    str.append(",").append(receiversEmail.get(i));
                }
            }
            return str.toString();
        }
    }

    public void onClickSendBtn(ActionEvent actionEvent) {
        new Thread(() -> Platform.runLater(() -> {
            if (email != null) {
                email.setReceivers(clearCommaReceiver(rcvTextField.getText()));
                email.setSubject(subjectTextField.getText());
                email.setMessageText(msgTxtArea.getText());

                if (checkSendEmailFields(email)) {

                    System.out.println("SendWindow prima di SENDEMAIL ci sono>>");
                    for (String s : email.getReceivers()) {
                        System.out.println(s);
                    }

                    switch (clientApp.getConnectionHandler().sendEmail(email)) {
                        case EMAILSENT -> {
                            System.out.println("Email inviata");
                            emailSent = true;
                            stage.close();
                        }
                        case USERNOTEXIST -> {
                            StringBuilder errContentText = new StringBuilder();
                            for (String userNonexistent : email.getReceivers()) {
                                errContentText.append(userNonexistent).append("\n");
                            }
                            generateErrorAlert("Error sending email", "Error user not exist", "Following users not exist:\n" + errContentText);
                        }
                        case ERRCONNECTION ->
                                generateErrorAlert("Error login", "Server offline", "Server does not respond");
                    }
                }
            }
        })).start();
    }

    private boolean checkSendEmailFields(Email email) {
        boolean isWritten = false;
        StringBuilder errorContentMsg = new StringBuilder();
        if (rcvTextField.getText() == null || rcvTextField.getText().length() == 0) {
            errorContentMsg.append("No receiver written\n");
        }

        for (String rcv : email.getReceivers()) {
            if (!validateEmail(rcv)) {
                if (!isWritten) {
                    errorContentMsg.append("These receivers are not valid: \n");
                    isWritten = true;
                }
                errorContentMsg.append("\t -> ").append(rcv).append("\n");
            }
        }

        if (isWritten) {
            errorContentMsg.append("""
                    Valid email address
                    - should begin with a letter
                    - length need to be almost 4 character
                    - can contain only this special character . _

                    """);
        }

        if (subjectTextField.getText() == null || subjectTextField.getText().length() == 0) {
            errorContentMsg.append("No Subject written\n");
        }

        if (msgTxtArea.getText() == null || msgTxtArea.getText().length() == 0) {
            errorContentMsg.append("Message body is empty\n");
        }

        if (errorContentMsg.length() == 0) {
            return true;
        } else {
            generateErrorAlert("Error in fields", "There are some wrong things: ", errorContentMsg.toString());
            return false;
        }
    }

    private void generateErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.initOwner(stage);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private boolean validateEmail(String emailUserLogin) {
        // Compiles the given regular expression and attempts to match the given input against it.
        return Pattern.matches("^[a-zA-Z][a-zA-Z0-9._][a-zA-Z0-9._][a-zA-Z0-9._]+@dipada.it", emailUserLogin);
    }

    private List<String> clearCommaReceiver(String text) {
        List<String> receivers = new LinkedList<>();

        //Matcher matcher = Pattern.compile("[a-zA-Z][a-zA-Z0-9._][a-zA-Z0-9._][a-zA-Z0-9._]+").matcher(text);
        Matcher matcher = Pattern.compile("[^,\s]+").matcher(text);
        //dan,att ,ssta, dadas
        while (matcher.find()) {
            String group = matcher.group();
            System.out.println("Trovato: " + group);
            receivers.add(group);
        }

        System.out.println("Lunghezza lista " + receivers.size() + " Stampo: ");
        for (String s : receivers)
            System.out.println("\n" + s);
        return receivers;
    }

    public boolean isEmailSent() {
        return emailSent;
    }
}
