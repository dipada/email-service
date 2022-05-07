package prog.dipada.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.security.PublicKey;
import java.util.LinkedList;

/**
 *
 * This class represents a printable Log
 *
 * */
public class Log {

    private ListProperty<String> log;
    private ObservableList<String> logContent; // per binding con listView della view del server

    public Log(){
        this.logContent = FXCollections.observableList(new LinkedList<>());
        this.log = new SimpleListProperty<>();
        this.log.set(logContent);
    }

    public ListProperty<String> getLogProperty(){
        return log;
    }

    /**
     *
     * It prints on screen a passed message
     * @param messageLog message to print
     *
     * */
    public void printLogOnScreen(String messageLog){
        logContent.add(messageLog);
    }
}
