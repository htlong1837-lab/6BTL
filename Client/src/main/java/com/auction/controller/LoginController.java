
package com.auction.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        // Ẩn label lỗi khi mới mở màn hình
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {

        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        // Validate
        if (email.isEmpty() || password.isEmpty() || password.length() < 6) {
            showError("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if (!email.contains("@")) {
            showError("Vui lòng nhập email");
            return;
        }

        // TODO: Gửi lên Server sau
        System.out.println("Login: " + email);
        showSuccess("Đăng nhapah thhanfh công");
    }

    // Helper message //
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
    private void goToRegister(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/client/view/RegisterView.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 750, 700));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
