package prog.dipada.lib;

import prog.dipada.model.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * This class represent server handler
 * It create thread pool, take and dispatch new connection to the server
 * Number of threads active in thread pool depends on machine
 *
 * */
public class Server extends Thread{
    private Log log;
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
        exec = Executors.newFixedThreadPool(1);
        this.userList = new LinkedList<>();
        this.fileManager = new FileManager();
        log = new Log();
        this.runningServer = true;
    }

    public Log getLog(){
        return log;
    }

    public void setUsersList(){ // TODO rivedere per creazione
        userList.add("daniele@dipada.it");
        userList.add("giovanni@dipada.it");
        userList.add("peppino@dipada.it");
    }

    public List<String> getUserList(){return userList;}

    @Override
    public void run() {
        // Il thread del server si mette in attesa
        // quando accetta una connessione la passa al thread pool
        System.out.println("Server partito");
        log.printLogOnScreen("Server started");
        for (String s : getUserList()){
            System.out.println(s);
            fileManager.addUserDirs(s);
        }
        listen();
    }

    public void listen(){
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            log.printLogOnScreen("Server ready for connections. Max simultaneous connections " + numThreadSession);
            while(runningServer){ // TODO sistemare chiusura
                socket = serverSocket.accept();
                log.printLogOnScreen("New connection accepted");
                exec.execute(new ServerThreadSession(socket,fileManager,log));
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
            System.out.println("Server>> shoutdown");
            exec.shutdown();
            //exec.shutdownNow();

            System.out.println("Finish");
        }
    }

    public void end() {
        log.printLogOnScreen("Start exiting...");
        runningServer = false;
    }
}
