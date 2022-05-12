package prog.dipada.lib;

import prog.dipada.model.Email;
import prog.dipada.model.Log;

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
    private final Log log;
    private Socket socket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private String idSession;

    public ServerThreadSession(Socket socket, FileManager fileManager, Log log) {
        this.socket = socket;
        this.fileManager = fileManager;
        this.log = log;
    }

    @Override
    public void run() {
        System.out.println("Sessione partita " + socket);
        log.printLogOnScreen("New session started " + socket.getInetAddress());
        // TODO apre stream - invia/riceve dati - chiude stream
        openStreams();
        boolean runSession = true;
        // TODO qui operazioni
            try {
                System.out.println("SERVER ATTENDO OPERAZIONE " );
                String req = (String) inStream.readObject();
                System.out.println("OPERAZIONE RICHIESTA>> " + req);
                switch (req) {

                    case "UP":{
                        outStream.writeObject("YES");
                        outStream.flush();
                        break;
                    }
                    case "AUTH": {
                        System.out.println("Server in auth aspetto email");
                        String userEmail = (String) inStream.readObject();
                        this.idSession = userEmail;
                        if (fileManager.checkUserExist(userEmail)) {
                            log.printLogOnScreen("USER: " + idSession
                                    + " try connect to the server. Connection accepted");
                            outStream.writeObject("USER_ACCEPTED");
                            outStream.flush();
                        } else {
                            log.printLogOnScreen("USER: " + userEmail
                                    + " try connect to the server. Connection refused, unknown user");
                            outStream.writeObject("USER_REFUSE");
                            outStream.flush();
                            //runSession = false;
                        }
                        System.out.println("AUTH finito");
                        break;
                    }

                    case "sendAll": {
                        System.out.println("Server in sendAll aspetto userEmail");
                        String userEmail = (String) inStream.readObject();
                        log.printLogOnScreen("USER: " + userEmail + " has requested inbox and outbox emails");

                        System.out.println("sendall scrivo le liste");

                        outStream.writeObject(fileManager.loadInbox(userEmail));
                        outStream.flush();
                        outStream.writeObject(fileManager.loadOutbox(userEmail));
                        outStream.flush();

                        System.out.println("Sendall finita");
                        break;
                    }
                }
            } catch (IOException e) {
                // User close streams
                System.out.println("SESSION eccezione lettura object");
                runSession = false;
                //e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Session eccezione class not found");
                runSession = false;
                e.printStackTrace();
            }

        System.out.println("server chiude gli stream");
        // TODO chiude stream
        log.printLogOnScreen(idSession + " disconnected" );
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
