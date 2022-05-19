package prog.dipada;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import prog.dipada.controller.LoginController;
import prog.dipada.controller.MainWindowController;
import prog.dipada.controller.SendWindowController;
import prog.dipada.lib.ConnectionHandler;
import prog.dipada.model.Client;
import prog.dipada.model.Email;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ClientApp extends Application {
    private final Client client;
    private final ConnectionHandler connectionHandler;
    private MainWindowController mainWindowController;


    public ClientApp() {
        connectionHandler = new ConnectionHandler(this);
        client = new Client();
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void start(Stage stage) {

        showLoginLayout(stage);
        showMainWindow(stage);
    }

    @Override
    public void stop(){
        try {
            super.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String YELLOW = "\u001B[33m";
        String RESET = "\u001B[0m";
        System.out.println(YELLOW + "Starting application exiting..." + RESET);
        if(mainWindowController != null) {
            mainWindowController.setStop(true);
        }
        System.out.println("->\tapplication exit successful\n");
        String GREEN = "\u001B[32m";
        System.out.println(GREEN + "Exit completed." + RESET);
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
    private void showLoginLayout(Stage primaryStage) {
        URL loginUrl = ClientApp.class.getResource("Login.fxml");
        FXMLLoader loginLoader = new FXMLLoader(loginUrl);
        try {
            Scene scene = new Scene(loginLoader.load(), 300, 100);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("login_style.css")).toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.initModality(Modality.WINDOW_MODAL); // blocca l'owner stage (primarystage) di questo stage finchè questo non finisce
            stage.initOwner(primaryStage);
            stage.setScene(scene);

            // window event Handler
            stage.setOnCloseRequest(windowEvent -> {
                Platform.exit(); // Causes the JavaFX application to terminare. javaDoc
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
        Platform.runLater(()-> {
            try {
                URL mainWindowsUrl = ClientApp.class.getResource("MainWindow.fxml");
                FXMLLoader mainWindowLoader = new FXMLLoader(mainWindowsUrl);
                Scene scene = new Scene(mainWindowLoader.load(), 900, 700);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("main_window_style.css")).toExternalForm());
                mainWindowController = mainWindowLoader.getController();
                mainWindowController.setMainWindowController(this,primaryStage);
                primaryStage.setTitle(client.getUserEmailProperty().getValue() + " - dipadamail");
                primaryStage.setScene(scene);
                primaryStage.show();

            } catch (IOException e) {
                System.err.println("Main windows loader error");
                e.printStackTrace();
            }
        });
    }

    public boolean showSendEmailWindow(Email email) {
        try {
            URL sendWindowUrl = ClientApp.class.getResource("SendWindow.fxml");
            FXMLLoader sendWindowLoader = new FXMLLoader(sendWindowUrl);
            Stage stage = new Stage();
            Scene scene = new Scene(sendWindowLoader.load(), 600, 400);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("send_window_style.css")).toExternalForm());
            stage.setTitle("Write - dipadamail");
            SendWindowController sendWindowController = sendWindowLoader.getController();
            sendWindowController.setSendWindowController(this, stage);
            sendWindowController.setEmail(email);
            stage.setScene(scene);
            stage.showAndWait();
            return sendWindowController.isEmailSent();
        } catch (IOException e) {
            System.out.println("Send email window loader error");
            e.printStackTrace();
            return false;
        }
    }
}
