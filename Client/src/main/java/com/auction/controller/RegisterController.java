package com.auction.controller;

import com.auction.client.ServerConnection;
import com.auction.client.dto.Response;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class RegisterController {

    @FXML private TextField     userNameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label         errorLabel;

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("Bidder", "Seller"));
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleRegister() {
        errorLabel.setVisible(false);

        String username        = userNameField.getText().trim();
        String password        = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role            = roleComboBox.getValue();

        // Validate đầu vào
        if (username.isEmpty() || password.isEmpty() || role == null) {
            showError("Vui lòng nhập đầy đủ thông tin");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Mật khẩu xác nhận không khớp");
            return;
        }

        // Gửi lên server
        try {
            String id = UUID.randomUUID().toString();

            Response res = ServerConnection.getInstance().send("REGISTER", Map.of(
                "id",              id,
                "username",        username,
                "password",        password,
                "confirmPassword", confirmPassword,
                "role",            role
            ));

            if (res.isSuccess()) {
                
                showSuccess("Đăng ký thành công! Vui lòng đăng nhập.");

                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/client/view/LoginView.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) userNameField.getScene().getWindow();
                    Scene scene = new Scene(root,500,700) ;
                    stage.setScene(scene);
                    stage.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } 
                });
                pause.play();
                
            } else {
                showError(res.getMessage());
            }

        } catch (IOException e) {
            showError("Lỗi kết nối server: " + e.getMessage());
        }
    } 

    private void showError(String message) {
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        errorLabel.setStyle("-fx-text-fill: green;");
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @FXML
    private void goToLogin(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/client/view/LoginView.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) userNameField.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 700));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 



