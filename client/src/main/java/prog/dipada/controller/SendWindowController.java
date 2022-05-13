package prog.dipada.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import prog.dipada.ClientApp;
import prog.dipada.model.Email;

import java.util.Collections;
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

    private boolean sendClicked = false;

    public void setSendWindowController(ClientApp clientApp, Stage stage) {
        this.clientApp = clientApp;
        this.stage = stage;
        System.out.println("setSendWindowController");
    }

    public void setEmail(Email email) {
        this.email = email;

        lblUserEmail.setText(this.email.getSender());
        rcvTextField.setText(formatReceiversComma(this.email.getReceivers()));
        //rcvTextField.setText(this.email.getReceivers().toString());
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
        // TODO controllare indirizzi email

        email.setReceivers(clearCommaReceiver(rcvTextField.getText()));
        email.setSubject(subjectTextField.getText());
        email.setMessageText(msgTxtArea.getText());

        if (checkSendEmailFields(email)) {
            sendClicked = true;
            // TODO inviare email
            stage.close();
        }
    }

    private boolean checkSendEmailFields(Email email) {
        String errorContentMsg = "";
        if (rcvTextField.getText() == null || rcvTextField.getText().length() == 0) {
            errorContentMsg += "No receiver written\n";
        }

        for (String rcv : email.getReceivers()) {
            if (!validateEmail(rcv)) {
                errorContentMsg += "Receiver: " + rcv + " is not valid\n";
            }
        }

        if (subjectTextField.getText() == null || subjectTextField.getText().length() == 0) {
            errorContentMsg += "No Subject written\n";
        }

        if (msgTxtArea.getText() == null || msgTxtArea.getText().length() == 0) {
            errorContentMsg += "Message body is empty\n";
        }

        if (errorContentMsg.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Error in fields");
            alert.setHeaderText("There are some wrong things: ");
            alert.setContentText(errorContentMsg);
            alert.showAndWait();
            return false;
        }
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

    public boolean isSendClicked() {
        return sendClicked;
    }
}
