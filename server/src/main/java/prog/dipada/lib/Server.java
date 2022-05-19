package prog.dipada.lib;

import prog.dipada.model.Log;

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
    private final Log log;
    private final ExecutorService exec;
    private final FileManager fileManager;
    private final List<String> userList;
    private final int serverPort;
    private boolean serverRunning;
    private ServerSocket serverSocket;
    private Socket socket;
    private final int numThreadSession;

    public Server(int serverPort){
        this.serverPort = serverPort;
        numThreadSession = Runtime.getRuntime().availableProcessors();
        exec = Executors.newFixedThreadPool(numThreadSession);
        this.userList = new LinkedList<>();
        this.fileManager = new FileManager();
        this.serverRunning = true;
        log = new Log();
    }

    public Log getLog(){
        return log;
    }

    public List<String> getUserList(){return userList;}

    public void setUsersList(){
        userList.add("daniele@dipada.it");
        userList.add("giovanni@dipada.it");
        userList.add("peppino@dipada.it");
    }

    @Override
    public void run() {
        log.printLogOnScreen("Server started");

        // When server starts add user dir if doesn't exist
        for (String s : getUserList()){
            System.out.println(s);
            fileManager.addUserDirs(s);
        }
        listen();
    }

    public void listen(){
        try {
            serverSocket = new ServerSocket(serverPort);
            log.printLogOnScreen("Server ready for connections. Max simultaneous connections " + numThreadSession);
            while(serverRunning){
                socket = serverSocket.accept();
                log.printLogOnScreen("New connection accepted");
                exec.execute(new ServerThreadSession(socket,fileManager,log));
            }
        } catch (IOException ignore) {
            // server socket closed
        } finally {
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("client close socket");
                    e.printStackTrace();
                }
            }
            exec.shutdown();
        }
    }


    public void end() {
        String BLUE = "\u001B[34m";
        printColor("Server start exiting...", BLUE);
        String YELLOW = "\u001B[33m";
        printColor("->\tShutdown thread pool", YELLOW);
        serverRunning = false;
        exec.shutdown();
        try{
            printColor("->\tClosing socket", YELLOW);
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        exec.shutdown();
        String GREEN = "\u001B[32m";
        printColor("Server finish exit procedure", GREEN);
    }

    private void printColor(String text, String color) {
        String RESET = "\u001B[0m";
        System.out.println(color + text + RESET);
    }
}
