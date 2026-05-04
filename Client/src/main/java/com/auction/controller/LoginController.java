
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
        if (email.isEmpty() || password.isEmpty() || password.length() < 6 ) {
            showError("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if (!email.endsWith("@gmail.com")             // email phải kết thúc bằng "@gmail.com"
            || email.indexOf("@") == 0                                     // email không được bắt đầu bằng "@" 
            || email.contains("..")                                          // email không được chứa ".." liên tiếp
            || email.indexOf("@") != email.lastIndexOf("@")          // email chỉ được chứa 1 ký tự "@"
            || !Character.isLetterOrDigit(email.charAt(0))       // email phải bắt đầu bằng chữ cái hoặc số
            || email.length() > 30                                      // độ dài email không được vượt quá 30 ký tự và ít hơn 6 kí tự
            || email.length() < 6) {        
            showError("Email không hợp lệ.");
            return;
        }
        
        // TODO: Gửi lên Server sau
        System.out.println("Login: " + email);
        showSuccess("Đăng nhập thành công");
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
