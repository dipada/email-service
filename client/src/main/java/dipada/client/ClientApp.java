package dipada.client;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class ClientApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        //URL clientUrl = ClientApp.class.getResource("MainWindow.fxml");
        //FXMLLoader fxmlLoader = new FXMLLoader(clientUrl);
        //Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        URL clientUrl = ClientApp.class.getResource("Login.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(clientUrl);
        System.out.println("qua");
        Scene scene = new Scene(fxmlLoader.load(), 300, 100); // TODO spec dimensioni

        stage.setTitle("dipadamail");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
