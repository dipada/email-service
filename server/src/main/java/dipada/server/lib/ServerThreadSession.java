package dipada.server.lib;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * This class represents single server task for the user session
 *
 * */
public class ServerThreadSession implements Runnable {
    private Socket socket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    public ServerThreadSession(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Sessione partita " + socket);

        // TODO apre stream - invia/riceve dati - chiude stream
        openStreams();

        // TODO qui operazioni
        try {
            String provaricezione = (String) inStream.readObject();
            if(provaricezione.equals("stringa di provaa")){
                System.out.println("BENE ho ricevuto>> " + provaricezione);
            }else{
                System.out.println("MALE ho ricevuto>> " + provaricezione);
            }
        } catch (IOException e) {
            System.out.println("SESSION eccezione lettura object");
            e.printStackTrace();
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
