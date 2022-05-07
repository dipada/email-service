package prog.dipada.lib;

import prog.dipada.model.Email;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;

/**
 *
 * This class represents single server task for the user session
 *
 * */
public class ServerThreadSession implements Runnable {
    private final FileManager fileManager;
    private Socket socket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    public ServerThreadSession(Socket socket, FileManager fileManager) {
        this.socket = socket;
        this.fileManager = fileManager;
    }

    @Override
    public void run() {
        System.out.println("Sessione partita " + socket);

        // TODO apre stream - invia/riceve dati - chiude stream
        openStreams();

        // TODO qui operazioni
        try {
            String req = (String) inStream.readObject();
            switch (req){
                case "sendAll":{
                    String userEmail = (String) inStream.readObject();
                    // TODO verifica esistenza email
                    if(fileManager.checkUserExist(userEmail)){
                        //System.out.println("Spedico email all'utente " + userEmail);
                        outStream.writeObject(fileManager.loadInbox(userEmail));
                        outStream.flush();
                        outStream.writeObject(fileManager.loadOutbox(userEmail));
                        outStream.flush();
                    }else{
                        outStream.writeObject("ERROR_SERVER");
                        // TODO gestire utente non esistente
                    }
                    //outStream.writeObject();
                    //List<Email> inboxList = (List<Email>) inStream.readObject();
                    //List<Email> outboxList = (List<Email>) inStream.readObject();

                }
            }
        } catch (IOException e) {
            // User close streams
            System.out.println("SESSION eccezione lettura object");
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Session eccezione class not found");
            e.printStackTrace();
        }

        // TODO chiude stream
        closeStreams();
    }

    private void openStreams(){
        try {
            outStream = new ObjectOutputStream(socket.getOutputStream());
            outStream.flush();
            inStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("SESSIONE ECCEZIONE APERTURA STREAM");
            e.printStackTrace();
        }
    }

    private void closeStreams(){
        try {
            outStream.close();
            inStream.close();
        } catch (IOException e) {
            System.out.println("SESSIONE CHIUSURA STREAM ECCEZIONE");
            e.printStackTrace();
        }
    }
}
