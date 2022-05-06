package dipada.client;

import dipada.client.controller.LoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;

public class ClientApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        //URL clientUrl = ClientApp.class.getResource("MainWindow.fxml");
        //FXMLLoader fxmlLoader = new FXMLLoader(clientUrl);
        //Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        URL clientUrl = ClientApp.class.getResource("Login.fxml");

        //FXMLLoader fxmlLoader = new FXMLLoader(clientUrl);
        //System.out.println("qua");
        //Scene scene = new Scene(fxmlLoader.load(), 300, 100); // TODO spec dimensioni

        //stage.setTitle("dipadamail");
        //stage.setScene(scene);
        //stage.show();
        showLoginLayout(stage);
        showMainWindow(stage);
    }

    public static void main(String[] args) {
        launch();
    }

    private void showLoginLayout(Stage primaryStage) {
        URL loginUrl = ClientApp.class.getResource("Login.fxml");
        FXMLLoader loginLoader = new FXMLLoader(loginUrl);
        try {
            Scene scene = new Scene(loginLoader.load(), 300, 100);
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.initModality(Modality.WINDOW_MODAL); // blocca l'owner stage (primarystage) di questo stage finchè questo non finisce
            stage.initOwner(primaryStage);
            stage.setScene(scene);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    Platform.exit(); // Causes the JavaFX application to terminare. javaDoc
                }
            });
            LoginController loginController = loginLoader.getController();
            loginController.setLoginController(this, stage);
            stage.showAndWait();
        } catch (IOException e) {
            System.out.println("Login loader error");
            e.printStackTrace();
        }
    }

    private void showMainWindow(Stage primaryStage) {
        try{
            URL mainWindowsUrl = ClientApp.class.getResource("MainWindow.fxml");
            FXMLLoader mainWindowLoader = new FXMLLoader(mainWindowsUrl);
            Scene scene = new Scene(mainWindowLoader.load(), 900, 600);
            primaryStage.setTitle("dipadamail");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Main windows loader error");
            e.printStackTrace();
        }
    }
}
