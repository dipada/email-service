package prog.dipada.model;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 *
 * This class represents a printable Log
 *
 * */
public class Log {

    private final ListProperty<String> log;
    private final ObservableList<String> logContent; // per binding con listView della view del server

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
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Platform.runLater(()-> logContent.add(0,dateFormatter.format(new Date()) + " - " + messageLog));
    }
}
