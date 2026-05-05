package com.auction.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
<<<<<<< HEAD
        try {
            ServerConnection.getInstance().connect();
        } catch (IOException e) {
            System.err.println("[App] Không kết nối được server: " + e.getMessage());
        }

=======
>>>>>>> parent of 542f7ee (0.0'')
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/client/view/LoginView.fxml")
        );
        Scene scene = new Scene(loader.load(), 500, 700);
        stage.setTitle("Hệ thống đấu giá");
        stage.setScene(scene);
        stage.show();
    }
}
