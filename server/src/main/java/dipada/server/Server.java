package dipada.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * This class represent server handler
 * It create thread pool, take and dispatch new connection to the server
 * Number of threads active in thread pool depends on machine
 *
 * */
public class Server extends Thread{

    private final int serverPort;
    private int numThreadSession;
    private ExecutorService exec;
    private boolean runningServer; // TODO check variabile
    private Socket socket;

    public Server(int serverPort){
        this.serverPort = serverPort;
        numThreadSession = Runtime.getRuntime().availableProcessors();
        exec = Executors.newFixedThreadPool(numThreadSession);
        this.runningServer = true;
    }

    @Override
    public void run() {
        // Il thread del server si mette in attesa "infinita"
        // quando accetta una connessione la passa al thread pool
        System.out.println("Server partito");
        listen();
    }

    public void listen(){
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while(runningServer){ // TODO sistemare chiusura
                System.out.println("SERVER in attesa di client");
                socket = serverSocket.accept();
                System.out.println("SERVER Nuova sessione accettata, mando al threadpool");
                exec.execute(new ServerThreadSession(socket));
            }
        } catch (IOException e) {
            System.out.println("Server eccezione serverSocket");
            e.printStackTrace();
        } finally {
            // TODO chiusura socket e shutdown threadpool
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("SERVER CHIUSURA SOCKET ECCEZIONE");
                    e.printStackTrace();
                }
            }
            exec.shutdown();
        }

    }
}
