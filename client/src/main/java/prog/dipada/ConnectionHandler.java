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
    private Thread checkTh;
    private boolean stop;


    public ConnectionHandler(String host, int port) {
        this.idConnection = null;
        this.socket = null;
        this.inStream = null;
        this.outStream = null;
        this.host = host;
        this.port = port;

        this.stop = false;
    }



    public boolean startConnection(){
        boolean success = false;
        //while(!success && attempts > 1){
            System.out.println("Apro nuova connessioneeee socket vale " + socket);

            try {
                // TODO gestire caso server offline
                socket = new Socket(host,port);

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

    private void openStream(){
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
        System.out.println("Connection closed");
    }

    @Override
    public void run() {
        startConnection();

        //checkTh = new Thread(this::checkConnection);
        //checkTh.start();
        checkConnection();
        closeConnection();

    }

    public void end(){
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
           //connectionIsUp = false;
            return false;
        } catch (IOException e) {
            System.out.println("RequestALL IOException");
            e.printStackTrace();
            //connectionIsUp = false;
            return false;
        }
        return true;
    }

    public void setIdConnection(String emailIdConnection){
        this.idConnection = emailIdConnection;
    }

    public String authUser(String emailUserLogin) {
        String success = null;
        try {
            outStream.writeObject("AUTH");
            outStream.flush();
            outStream.writeObject(emailUserLogin);
            String rx = (String)inStream.readObject();
            System.out.println("Il server ha mandato >> " + rx);
            if(rx.equals("USER_ACCEPTED")){
                System.out.println("User " + emailUserLogin + " accettato dal server");
                success = "valid";
            }else{
                System.out.println("User " + emailUserLogin + " non accettato dal server");
                success = "notvalid";
            }
        }catch (NullPointerException e){
            success = null;
        }catch (IOException e) {
            System.out.println("ERROR AUTH ");
            success = null;
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            success = null;
            e.printStackTrace();
        }
        return success;
    }
}
