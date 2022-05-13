package prog.dipada.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Email implements Serializable {

    private static final long serialVersionUID = 5462223600L;
    private final int id;
    private final String sender;
    private String subject;
    private List<String> receivers;
    private String messageText;
    private final Date date; // TODO finire data
    private boolean isSent;

    public Email(String sender, String subject, List<String> receivers, String messageText, Date date) {
        this.sender = sender;
        this.subject = subject;
        this.receivers = receivers;
        this.messageText = messageText;
        this.date = date;
        this.isSent = false;
        this.id = this.hashCode();
    }

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public String getMessageText() {
        return messageText;
    }

    public Date getDate() {
        return date;
    }

    public String getDateToString() {
        return (this.date == null) ? "" : formatDate(this.date);
    }

    private String formatDate(Date date) {
        // Specificare pattern "dd/MM/yyyy" per cambiare il formato della data
        SimpleDateFormat dataFormatter = new SimpleDateFormat();
        return dataFormatter.format(date);
    }

    @Override
    public String toString() {
        return "ID: " + id + " " + "Sender: " + sender + " - " + "Subject:" + subject + " - " + "Receivers: " + receivers + " - " + "Date: " + getDateToString();
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 31 * hash + (getSender() == null ? 0 : getSender().hashCode());
        hash = 31 * hash + (getSubject() == null ? 0 : getSubject().hashCode());
        hash = 31 * hash + (getReceivers() == null ? 0 : getReceivers().hashCode());
        hash = 31 * hash + (getMessageText() == null ? 0 : getMessageText().hashCode());
        hash = 31 * hash + (getDate() == null ? 0 : getDate().hashCode());

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Email email = (Email) obj;
        return getId() == email.getId();
    }

    public boolean isSent() {
        return isSent;
    }

    public void setIsSent(boolean sent) {
        this.isSent = sent;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}