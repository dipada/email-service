package prog.dipada.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import prog.dipada.ClientApp;
import prog.dipada.model.Email;

import java.util.List;

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
        System.out.println("Finito set email");
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
        System.out.println("Bottone cliccato");
        // TODO controllare indirizzi email
        sendClicked = true;
        stage.close();

    }

    public boolean isSendClicked() {
        return sendClicked;
    }
}
