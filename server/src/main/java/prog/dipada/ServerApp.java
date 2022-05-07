package prog.dipada;

import prog.dipada.lib.FileManager;
import prog.dipada.lib.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class ServerApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        URL serverUrl = ServerApp.class.getResource("ServerMainWindow.fxml");
        FXMLLoader serverLoader = new FXMLLoader(serverUrl);
        Scene scene = new Scene(serverLoader.load(), 900, 600);

        stage.setTitle("Server dipada");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Server server = new Server(8989);
        server.setUsersList();
        Thread ts = new Thread(server);
        ts.start();

        launch();
    }
}
