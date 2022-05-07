package prog.dipada.lib;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
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
    private FileManager fileManager;
    private final List<String> userList;

    public Server(int serverPort){
        this.serverPort = serverPort;
        numThreadSession = Runtime.getRuntime().availableProcessors();
        exec = Executors.newFixedThreadPool(numThreadSession);
        this.userList = new LinkedList<>();
        this.fileManager = new FileManager();
        this.runningServer = true;
    }

    public void setUsersList(){ // TODO rivedere per creazione
        userList.add("daniele@dipada.it");
        userList.add("giovanni@dipada.it");
        userList.add("peppino@dipada.it");
    }

    public List<String> getUserList(){return userList;}

    @Override
    public void run() {
        // Il thread del server si mette in attesa "infinita"
        // quando accetta una connessione la passa al thread pool
        System.out.println("Server partito");
        for (String s : getUserList()){
            System.out.println(s);
            fileManager.addUserDir(s);
        }
        listen();
    }

    public void listen(){
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while(runningServer){ // TODO sistemare chiusura
                System.out.println("SERVER in attesa di client");
                socket = serverSocket.accept();
                System.out.println("SERVER Nuova sessione accettata, mando al threadpool");
                exec.execute(new ServerThreadSession(socket,fileManager));
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
