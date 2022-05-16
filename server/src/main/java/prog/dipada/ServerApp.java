package prog.dipada;

import prog.dipada.controller.ServerMainWindowController;
import prog.dipada.lib.FileManager;
import prog.dipada.lib.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class ServerApp extends Application {
    private Server server;
    private Thread ts;
    @Override
    public void start(Stage stage) throws Exception {
        server = new Server(8989);
        server.setUsersList();
        ts = new Thread(server);
        ts.start();

        URL serverUrl = ServerApp.class.getResource("ServerMainWindow.fxml");
        FXMLLoader serverLoader = new FXMLLoader(serverUrl);
        Scene scene = new Scene(serverLoader.load(), 900, 600);
        scene.getStylesheets().add(getClass().getResource("Server_main_style.css").toExternalForm());
        System.out.println("Parte 2");

        ServerMainWindowController serverMainWindowController = serverLoader.getController();
        serverMainWindowController.initMainViewController(server);

        stage.setTitle("Server dipada");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop(){
        server.end();
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Main Server App shutdown");
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}
