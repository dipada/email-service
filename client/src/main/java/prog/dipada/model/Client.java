package prog.dipada.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class Client {
    private final StringProperty userEmail;
    private final ListProperty<Email> inbox;
    private final ObservableList<Email> inboxContent;
    private final ListProperty<Email> outbox;
    private final ObservableList<Email> outboxContent;
    private final IntegerProperty inboxTotalNum;
    private final IntegerProperty outboxTotalNum;
    public Client(){
        this.userEmail = new SimpleStringProperty(null);
        // Different threads access this resource
        this.inboxContent = FXCollections.observableList(Collections.synchronizedList(new LinkedList<>()));
        this.inbox = new SimpleListProperty<>();
        this.inbox.set(inboxContent);

        // Different threads access this resource
        this.outboxContent = FXCollections.observableList(Collections.synchronizedList(new LinkedList<>()));
        this.outbox = new SimpleListProperty<>();
        this.outbox.set(outboxContent);

        this.inboxTotalNum = new SimpleIntegerProperty(0);
        this.outboxTotalNum = new SimpleIntegerProperty(0);
    }

    public synchronized IntegerProperty getInboxTotalNumProperty(){
        return inboxTotalNum;
    }

    private synchronized void setInboxTotalNumProperty(int numEmail){
        this.inboxTotalNum.set(numEmail);
    }

    public IntegerProperty getOutboxTotalNumProperty(){
        return outboxTotalNum;
    }

    private void setOutboxTotalNumProperty(int numEmail){
        this.outboxTotalNum.set(numEmail);
    }

    public void setUserEmailProperty(String userEmail){
        this.userEmail.set(userEmail);
    }

    public ListProperty<Email> getInboxProperty(){
      return inbox;
    }
    public ListProperty<Email> getOutboxProperty(){
        return outbox;
    }

    public void setInboxContent(List<Email> inboxList){
        for(Email em : inboxList){
            if(!inboxContent.contains(em)){
                inboxContent.add(0,em);
            }
        }
        setInboxTotalNumProperty(inboxContent.size());
    }

    public void setOutboxContent(List<Email> outboxList) {
        for(Email em : outboxList){
            if(!outboxContent.contains(em)){
                System.out.println("Aggiungo email " + em);
                outboxContent.add(0,em);
            }
        }
        setOutboxTotalNumProperty(outboxContent.size());
    }

    public void setOutboxContent(Email email){
        outboxContent.add(0,email);
    }

    // indirizzo email della casella postale
    public StringProperty getUserEmailProperty(){
        return userEmail;
    }

    public void deleteEmail(Email email){
        if(email.isSent())
            outboxContent.remove(email);
        else
            inboxContent.remove(email);
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
