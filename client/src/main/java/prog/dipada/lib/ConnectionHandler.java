package prog.dipada.lib;

import javafx.application.Platform;
import prog.dipada.ClientApp;
import prog.dipada.model.Email;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Comparator;
import java.util.List;

/**
 * Represents connection of client to server
 */
public class ConnectionHandler {

    private final String host;
    private final int port;
    private final ClientApp clientApp;
    private Socket socket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private String idConnection;

    public ConnectionHandler(ClientApp clientApp) {
        this.clientApp = clientApp;
        this.socket = null;
        this.inStream = null;
        this.outStream = null;
        this.host = "localhost";
        this.port = 8989;
    }

    public void setIdConnection(String emailIdConnection) {
        this.idConnection = emailIdConnection;
    }

    private void printColor(String text, String color) {
        String RESET = "\u001B[0m";
        System.out.println(color + text + RESET);
    }

    private boolean startConnection() {
        boolean success = false;
        try {
            String YELLOW = "\u001B[33m";
            printColor("Connecting to the server..", YELLOW);
            socket = new Socket(host, port);
            if (openStream()) {
                success = true;
                String GREEN = "\u001B[32m";
                printColor("Connection successful", GREEN);
            }
        } catch (IOException e) {
            System.err.println("Server does not respond");
        }
        return success;
    }

    private boolean openStream() {
        boolean success = false;
        try {
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());
            success = true;
        } catch (IOException e) {
            System.err.println("Fail opening streams, server not respond");
        }
        return success;
    }

    private void closeConnection() {
        if (socket != null) {
            try {
                if (inStream != null)
                    inStream.close();
                if (outStream != null)
                    outStream.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("Fail closing stream");
                e.printStackTrace();
            }
        }
    }

    /***/
    public boolean requestAll() {
        String BLUE = "\u001B[34m";
        printColor("Requested inbox and outbox", BLUE);
        if (startConnection()) {
            try {
                outStream.writeObject(ServerRequest.SENDALL);
                outStream.flush();
                outStream.writeObject(idConnection);
                outStream.flush();

                List<Email> inboxList = (List<Email>) inStream.readObject();
                List<Email> outboxList = (List<Email>) inStream.readObject();

                Comparator<Email> emailComparator = Comparator.comparing(Email::getDate);

                if (inboxList != null && outboxList != null) {
                    inboxList.sort(emailComparator);
                    outboxList.sort(emailComparator);

                    clientApp.getClient().setInboxContent(inboxList);
                    clientApp.getClient().setOutboxContent(outboxList);
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                System.err.println("IOException");
                e.printStackTrace();
                return false;
            } finally {
                closeConnection();
            }
            return true;
        } else {
            closeConnection();
            return false;
        }
    }

    public ServerResponse sendEmail(Email email) {
        email.setIsSent(false);
        if (startConnection()) {
            try {
                outStream.writeObject(ServerRequest.SENDEMAIL);
                outStream.flush();
                outStream.writeObject(email);
                outStream.flush();

                ServerResponse serverResponse = (ServerResponse) inStream.readObject();
                switch (serverResponse) {
                    case EMAILSENT -> {
                        Email em = (Email) inStream.readObject();
                        if (em != null) {
                            email.setIsSent(true);
                            Platform.runLater(() -> clientApp.getClient().setOutboxContent(email));
                        }
                        return ServerResponse.EMAILSENT;
                    }

                    case USERNOTEXIST -> {
                        List<String> usersNonexistent = (List<String>) inStream.readObject();

                        email.getReceivers().clear();   // Clear receivers list
                        email.getReceivers().addAll(usersNonexistent); // and add ONLY nonexistent users

                        return ServerResponse.USERNOTEXIST;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        } else {
            closeConnection();
        }
        return ServerResponse.ERRCONNECTION;
    }

    public String authUser(String emailUserLogin) {
        String success = null;
        if (startConnection()) {
            try {
                outStream.writeObject(ServerRequest.AUTH);
                outStream.flush();

                outStream.writeObject(emailUserLogin);
                outStream.flush();
                ServerResponse serverResponse = (ServerResponse) inStream.readObject();

                switch (serverResponse) {
                    case OK -> success = "valid";
                    case USERNOTEXIST -> success = "notvalid";
                }
            } catch (NullPointerException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }
        closeConnection();
        return success;
    }

    public boolean deleteEmail(Email email) {
        boolean deleted = false;
        if (startConnection()) {
            try {
                outStream.writeObject(ServerRequest.DELETEEMAIL);
                outStream.flush();
                
                outStream.writeObject(clientApp.getClient().getUserEmailProperty().getValue());
                outStream.flush();
                
                outStream.writeObject(email);
                outStream.flush();

                ServerResponse serverResponse = (ServerResponse) inStream.readObject();
                switch (serverResponse) {
                    case EMAILDELETED -> {
                        clientApp.getClient().deleteEmail(email);
                        deleted = true;
                    }
                    case ERRDELETINGEM -> {
                        // delete returns false
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        } else {
            closeConnection();
        }
        return deleted;
    }
}
