package prog.dipada;

import prog.dipada.model.Email;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Represents connection of client to server
 * */
public class ConnectionHandler extends Thread {

    private String host;
    private int port;

    private Socket socket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private String idConnection;

    public ConnectionHandler(String host, int port) {
        this.idConnection = null;
        this.socket = null;
        this.inStream = null;
        this.outStream = null;
        this.host = host;
        this.port = port;
    }

    public boolean startConnection(){
        boolean success = false;
        if(socket == null){
            System.out.println("Apro nuova connessione");
            try {
                // TODO gestire caso server offline
                socket = new Socket(host,port);
                openStream();
                success = true;
            } catch (IOException e) {
                System.out.println("startConnection socket error");
                e.printStackTrace();
            }
        }
        return success;
    }

    private void openStream(){
        try {
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Eccezione Errore openStream");
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        if(socket != null){
            try{
                inStream.close();
                outStream.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Eccezione closeConnection");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        /*
        Platform.runLater(()->{startConnection();
            try {
                outStream.writeObject("stringa di provaa");
            } catch (IOException e) {
                System.out.println("Eccezione CONNECTION writeObject");
                e.printStackTrace();
            }
            closeConnection();});
       */
        startConnection();

        //closeConnection();

    }

    /***/
    public boolean requestAll() {
        System.out.println("ConnectionHandler Richiesta ALL partita");
        try {
            outStream.writeObject("sendAll");
            //outStream.writeObject(idConnection);

                System.out.println("Richiesta accettata dal server utente esistente");
                System.out.println("CLIENT ARRIVA QUIA");
                List<Email> inboxList = (List<Email>) inStream.readObject();
                List<Email> outboxList = (List<Email>) inStream.readObject();
                System.out.println("Email inbox " + idConnection);
                System.out.println();
                for(Email em : inboxList){
                    System.out.println(em);
                }
                System.out.println("\n\nInbox finita");

                System.out.println();
                for(Email em : outboxList){
                    System.out.println(em);
                }
                System.out.println("\n\noutbox finita");
        } catch (ClassNotFoundException e){
            System.out.println("RequestALL ClassNotFOundException");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            System.out.println("RequestALL IOException");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void setIdConnection(String emailIdConnection){
        this.idConnection = emailIdConnection;
    }

    public boolean authUser(String emailUserLogin) {
        boolean successAuth = false;
        try {
            outStream.writeObject("AUTH");
            outStream.flush();
            outStream.writeObject(emailUserLogin);
            String rx = (String)inStream.readObject();
            System.out.println("Il server ha mandato >> " + rx);
            if(rx.equals("USERACCEPTED")){
                System.out.println("User " + emailUserLogin + " accettato dal server");
                successAuth = true;
            }else{
                System.out.println("User " + emailUserLogin + " non accettato dal server");
            }
        } catch (IOException e) {
            System.out.println("ERROR AUTH ");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return successAuth;
    }
}
