package com.auction.client;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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
=======
        try{
            ServerConnection.getInstance().connect();
        }catch(IOException e){
            System.err.println("[App] không thể kết nối server" + e.getMessage());
>>>>>>> 542f7eec30695e805d1e37549bb8f56c44309eb3
        }

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/client/view/LoginView.fxml")
        );
        Scene scene = new Scene(loader.load(), 500, 700);
        stage.setTitle("Hệ thống đấu giá");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> ServerConnection.getInstance().disconnect());
        stage.show();
    }
}
