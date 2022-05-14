package prog.dipada;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import prog.dipada.controller.LoginController;
import prog.dipada.controller.MainWindowController;
import prog.dipada.controller.SendWindowController;
import prog.dipada.lib.ConnectionHandler;
import prog.dipada.model.Client;
import prog.dipada.model.Email;

import java.io.IOException;
import java.net.URL;

public class ClientApp extends Application {
    private Client client;
    private ConnectionHandler connectionHandler;
    private MainWindowController mainWindowController;
    private Thread mainWinT;


    public ClientApp() {
        connectionHandler = new ConnectionHandler(this);
        client = new Client();
    }
    /*
        @Override
        public void stop(){
            System.out.println("Preparing to application exit...");
            connection.end();
            try {
                connThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Exiting now");
        }
    */

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void start(Stage stage) {
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

        //System.out.println("Main: conn è " + connection);

        showLoginLayout(stage);

        //System.out.println("DOPO login " + connection);
        //System.out.println("Client da main: " + client.getUserEmailProperty());

        mainWinT = new Thread(()->showMainWindow(stage));
        mainWinT.start();
        //showMainWindow(stage);

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Starting application exiting...");
        if(mainWindowController != null) {
            mainWindowController.setStop(true);
            try {
                mainWinT.join();
            } catch (RuntimeException e) {
                throw new RuntimeException();
            }
        }
        System.out.println("->\tapplication exit successful\n");
        System.out.println("Exit completed.");
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
        Platform.runLater(()-> {
            try {
                URL mainWindowsUrl = ClientApp.class.getResource("MainWindow.fxml");
                FXMLLoader mainWindowLoader = new FXMLLoader(mainWindowsUrl);
                Scene scene = new Scene(mainWindowLoader.load(), 900, 700);
                mainWindowController = mainWindowLoader.getController();
                mainWindowController.setMainWindowController(this,primaryStage);
                primaryStage.setTitle("dipadamail");
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
