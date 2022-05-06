package dipada.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Represents connection of client to server
 * */
public class ConnectionHandler extends Thread {

    private String host;
    private int port;

    private Socket socket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    public ConnectionHandler(String host, int port) {
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
        super.run();
        startConnection();
    }
}
