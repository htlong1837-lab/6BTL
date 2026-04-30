package com.auction.controller;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

public class RegisterController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label errorLabel;
    @FXML private PasswordField confirmPasswordField;

    public void initialize() {
        // Load vai trò vào ComboBox
        roleComboBox.setItems(FXCollections.observableArrayList(
            "Bidder", "Seller"
        ));
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleRegister() {

        errorLabel.setVisible(false);

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role =  roleComboBox.getValue();

        // kiểm tra tính hợp lệ
        if (name.isEmpty() || email.isEmpty() ||
            password.isEmpty() || role == null) {
            showError("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if(!email.contains("@")) {
            showError("Email không hợp lệ!");
            return;
        }

        if (password.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 kí tự");
            return;
        }

        if (!confirmPassword.equals(password)){
            showError("Mật khẩu nhập lại chưa đúng");
            return;
        }

        // TODO: Gửi lên Server sau
        System.out.println("Đăng ký: " + name + " | " + email + " | " + role);
        errorLabel.setStyle("-fx-text-fill: green;");
        errorLabel.setText("Đăng ký thành công!");
        errorLabel.setVisible(true);
    }

    // Hàm hiện lỗi tránh lặp code
    private void showError(String message) {
        errorLabel.setStyle("-fx-text-fill: red;");
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
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


