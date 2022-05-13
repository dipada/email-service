package prog.dipada.lib;

import javafx.application.Platform;
import prog.dipada.ClientApp;
import prog.dipada.lib.ServerRequest;
import prog.dipada.lib.ServerResponse;
import prog.dipada.model.Email;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * Represents connection of client to server
 */
public class ConnectionHandler {

    private final String host;
    private final int port;

    private Socket socket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private String idConnection;
    private Thread checkTh;
    private boolean stop;
    private ClientApp clientApp;

    public ConnectionHandler(ClientApp clientApp) {
        this.clientApp = clientApp;
        this.host = "localhost";
        this.port = 8989;
    }
    /*
    public ConnectionHandler(String host, int port) {
        this.idConnection = null;
        this.stop = false;
        this.socket = null;
        this.inStream = null;
        this.outStream = null;
        this.host = host;
        this.port = port;
        System.out.println("Passa di qui");
    }
    */

    private boolean startConnection() {
        boolean success = false;
        //while(!success && attempts > 1){
        System.out.println("start connection Apro nuova connessioneeee socket vale " + socket);

        try {
            // TODO gestire caso server offline
            socket = new Socket(host, port);
            System.out.println("1 socket vale " + socket);
            openStream();
            System.out.println("2");
            success = true;
        } catch (IOException e) {
            System.out.println("startConnection socket error");
            success = false;
            e.printStackTrace();
        }
        //attempts--;
        //}
        return success;
    }

    private void openStream() {
        try {
            outStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("3");
            inStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("4");
        } catch (IOException e) {
            //connectionIsUp = false;
            System.out.println("Eccezione Errore openStream");
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        if (socket != null) {
            try {
                inStream.close();
                outStream.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Eccezione closeConnection");
                e.printStackTrace();
            }
        }
        System.out.println("closeConnection Connection closed");
    }

    /***/
    public boolean requestAll() {
        System.out.println("ConnectionHandler Richiesta ALL partita");
        if (startConnection()) {
            try {
                outStream.writeObject(ServerRequest.SENDALL);
                outStream.writeObject(idConnection);

                List<Email> inboxList = (List<Email>) inStream.readObject();
                List<Email> outboxList = (List<Email>) inStream.readObject();

                if (inboxList != null)
                    clientApp.getClient().setInboxContent(inboxList);

                if (outboxList != null)
                    clientApp.getClient().setOutboxContent(outboxList);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                closeConnection();
            }
            return true;
        }else{
            closeConnection();
            return false;
        }
    }

    public ServerResponse sendEmail(Email email){
        System.out.println("EMAIL " + email + "\nIS SENT " + email.isSent());
        email.setIsSent(false);
        if(startConnection()){
            try {
                outStream.writeObject(ServerRequest.SENDEMAIL);
                outStream.writeObject(email);

                ServerResponse serverResponse = (ServerResponse) inStream.readObject();
                switch (serverResponse){
                    case EMAILSENT -> {
                        System.out.println("Ritornato EMAILSENT dal server");
                        Email em = (Email) inStream.readObject();
                        if(em != null){
                            System.out.println("Email inviata:\n "+ em);
                            email.setIsSent(true);
                            Platform.runLater(()->clientApp.getClient().setOutboxContent(email));
                        }
                       return ServerResponse.EMAILSENT;
                    }

                    case USERNOTEXIST -> {
                        System.out.println("Ritornato USERNOTEXIST dal server");
                        List<String> usersNonexistent = (List<String>) inStream.readObject();

                        email.getReceivers().clear();   // Clear receivers list
                        email.getReceivers().addAll(usersNonexistent); // and add ONLY nonexistent users

                        return ServerResponse.USERNOTEXIST;
                    }
                }
            } catch (IOException e) {
                System.out.println("Send email IO exception");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Send email CLASS exception");
                e.printStackTrace();
            }finally {
                closeConnection();
            }
        }else{
            System.out.println("Email non inviata");
            closeConnection();
        }
        return ServerResponse.ERRCONNECTION;
    }

    public void end() {
        System.out.println("Closing connection...");
        stop = true;
    }

    private void checkConnection() {
        while (!stop) {
            try {
                outStream.writeObject("UP");
                try {
                    String inRead = (String) inStream.readObject();
                    /*
                    if(inRead.equals("YES")){
                        System.out.println("Server up");
                    }*/
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            } catch (Exception e) {
                System.out.println("Server off provo a ripristinare la connessione");
                startConnection();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("THREAD refresh finito");
    }

    public void setIdConnection(String emailIdConnection) {
        this.idConnection = emailIdConnection;
    }

    public String authUser(String emailUserLogin) {
        String success = null;
        if (startConnection()) {
            try {
                outStream.writeObject(ServerRequest.AUTH);
                outStream.flush();

                outStream.writeObject(emailUserLogin);
                ServerResponse serverResponse = (ServerResponse) inStream.readObject();

                switch (serverResponse){
                    case OK -> {
                        System.out.println("User " + emailUserLogin + " accettato dal server");
                        success = "valid";
                    }
                    case USERNOTEXIST -> {
                        System.out.println("User " + emailUserLogin + " non accettato dal server");
                        success = "notvalid";
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("ERROR AUTH ");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        closeConnection();
        return success;
    }
}
