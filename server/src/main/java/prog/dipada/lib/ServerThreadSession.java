package prog.dipada.lib;

import prog.dipada.model.Email;
import prog.dipada.model.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * This class represents single server task for the user session
 *
 * */
public class ServerThreadSession implements Runnable {
    private final FileManager fileManager;
    private final Log log;
    private final Socket socket;
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
        log.printLogOnScreen("New session started " + socket.getInetAddress() + socket.getLocalAddress() + socket.getLocalSocketAddress());
        // TODO apre stream - invia/riceve dati - chiude stream
        openStreams();
        // TODO qui operazioni
        if (inStream != null) {
            try {
                ServerRequest req = (ServerRequest) inStream.readObject();

                switch (req) {
                    case AUTH -> {
                        System.out.println("Server in auth aspetto email");
                        String userEmail = (String) inStream.readObject();

                        this.idSession = userEmail;
                        if (fileManager.checkUserExist(userEmail)) {
                            log.printLogOnScreen("USER: " + idSession
                                    + " try connect to the server. Connection accepted");
                            outStream.writeObject(ServerResponse.OK); // user exist and is accepted
                            outStream.flush();
                        } else {
                            log.printLogOnScreen("USER: " + userEmail
                                    + " try connect to the server. Connection refused, unknown user");
                            outStream.writeObject(ServerResponse.USERNOTEXIST);
                            outStream.flush();
                        }
                        System.out.println("AUTH finito");
                    }
                    case SENDALL -> {
                        System.out.println("Server in sendAll aspetto userEmail");
                        String userEmail = (String) inStream.readObject();

                        this.idSession = userEmail;
                        log.printLogOnScreen("USER: " + idSession + " has requested inbox and outbox emails");

                        outStream.writeObject(fileManager.loadInbox(userEmail));
                        outStream.flush();

                        outStream.writeObject(fileManager.loadOutbox(userEmail));
                        outStream.flush();

                        System.out.println("Send all finita");
                    }
                    case SENDEMAIL -> {
                        boolean usersExist = true;
                        Email emailToSend = (Email) inStream.readObject();
                        this.idSession = emailToSend.getSender();
                        List<String> usersToCheck = emailToSend.getReceivers();
                        List<String> usersNonexistent = new LinkedList<>();
                        for (String user : usersToCheck) {
                            if (!fileManager.checkUserExist(user)) {
                                usersExist = false;
                                usersNonexistent.add(user);
                            }
                        }
                        if (usersExist) {
                            // All users exist, sending email
                            log.printLogOnScreen("USER: " + idSession + " send an email correctly");
                            Email newEmail = fileManager.save(emailToSend);
                            outStream.writeObject(ServerResponse.EMAILSENT);
                            outStream.writeObject(newEmail);
                            outStream.flush();

                        } else {
                            // one or more users not exists, send back list of nonexistent users
                            log.printLogOnScreen("USER: " + idSession + " error while sending an email: receivers does not exist");
                            outStream.writeObject(ServerResponse.USERNOTEXIST);
                            outStream.writeObject(usersNonexistent);
                            outStream.flush();
                        }
                    }

                    case DELETEEMAIL -> {
                        System.out.println("Server legge email da cancellare");
                        String user = (String) inStream.readObject();
                        Email emailToDelete = (Email) inStream.readObject();
                        fileManager.deleteEmail(emailToDelete,user);
                        outStream.writeObject(ServerResponse.EMAILDELETED);
                        outStream.flush();
                    }
                }
            } catch (IOException ignore) {
                // User close streams
                System.out.println("SESSION eccezione lettura object");
                //e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Session eccezione class not found");
                e.printStackTrace();
            }

            System.out.println("server chiude gli stream");
            // TODO chiude stream
            log.printLogOnScreen(idSession + " disconnected");
            closeStreams();
        }
    }

    private void openStreams(){
        try {
            outStream = new ObjectOutputStream(socket.getOutputStream());
            outStream.flush();
            inStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("SESSIONE ECCEZIONE APERTURA STREAM");
            //e.printStackTrace();
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
