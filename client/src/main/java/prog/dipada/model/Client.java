package prog.dipada.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Client {
    private StringProperty userEmail;
    private final ListProperty<Email> inbox;
    private final ObservableList<Email> inboxContent;
    private final ListProperty<Email> outbox;
    private final ObservableList<Email> outboxContent;
    private final IntegerProperty inboxTotalNum;
    private final IntegerProperty outboxTotalNum;
    public Client(){
        this.userEmail = new SimpleStringProperty(null);
        this.inboxContent = FXCollections.observableList(new LinkedList<>());
        this.inbox = new SimpleListProperty<>();
        this.inbox.set(inboxContent); // setto il valore interno della property con una ObservableList

        this.outboxContent = FXCollections.observableList(new LinkedList<>());
        this.outbox = new SimpleListProperty<>();
        this.outbox.set(outboxContent);

        this.inboxTotalNum = new SimpleIntegerProperty(0);
        this.outboxTotalNum = new SimpleIntegerProperty(0);
    }

    public IntegerProperty getInboxTotalNumProperty(){
        return inboxTotalNum;
    }

    public void setInboxTotalNumProperty(int numEmail){
        System.out.println("INBOXTOT vale " + inboxTotalNum.getValue());
        this.inboxTotalNum.set(numEmail);
        System.out.println("INBOXTOT dopo vale " + inboxTotalNum.getValue());
    }

    public IntegerProperty getOutboxTotalNumProperty(){
        return outboxTotalNum;
    }

    public void setOutboxTotalNumProperty(int numEmail){
        this.outboxTotalNum.set(numEmail);
    }

    public void setUserEmailProperty(String userEmail){
        System.out.println("Da client setto email ora vale " + this.userEmail + " " + userEmail);
        this.userEmail.set(userEmail);
        System.out.println("Da client setto email ora vale " + this.userEmail + " " + userEmail);
    }
    // ritorna una lista di email perchè è ciò che avevo settato nel costruttore
    //
    public ListProperty<Email> getInboxProperty(){
      return inbox;
    }
    public ListProperty<Email> getOutboxProperty(){
        return outbox;
    }

    public void setInboxContent(List<Email> inboxList){
        inboxContent.setAll(inboxList);
    }

    public void setOutboxContent(List<Email> outboxList) {
        outboxContent.setAll(outboxList);
    }

    public void setOutboxContent(Email email){
        outboxContent.add(email);
    }

    // indirizzo email della casella postale
    public StringProperty getUserEmailProperty(){
        return userEmail;
    }

    public void deleteEmail(Email email){
        outboxContent.remove(email);
    }

    public void generateEmail(){
        List<String> receivers = new ArrayList<>();
        receivers.add("email@1");
        receivers.add("email@2");


        for(int i = 0; i < 1000; i++) {
            Email e1 = new Email("jop", "oggetto " + i,receivers, "message body text " + i, new Date());
            inboxContent.add(e1);
        }


        List<String> receivers2 = new ArrayList<>();
        receivers.add("email@5");
        receivers.add("email@6");
        for(int i = 0; i < 1000; i++) {
            Email e2 = new Email("dan", "oggetto uscita" + i,receivers, "message body uscita text " + i, new Date());
            outboxContent.add(e2);
        }
    }
}
