
package com.auction.controller;

import com.auction.client.model.FakeDataHelper;
import com.auction.client.model.UserItem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.List;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty() || password.length() < 6) {
            showError("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if (!email.endsWith("@gmail.com")
            || email.indexOf("@") == 0
            || email.contains("..")
            || email.indexOf("@") != email.lastIndexOf("@")
            || !Character.isLetterOrDigit(email.charAt(0))
            || email.length() > 30
            || email.length() < 6) {
            showError("Email không hợp lệ.");
            return;
        }

        // TODO: thay bằng gọi server — hiện dùng fake data
        List<UserItem> users = FakeDataHelper.makeUsers();
        UserItem matched = users.stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(email))
            .findFirst()
            .orElse(null);

        if (matched == null) {
            showError("Email không tồn tại!");
            return;
        }

        if ("BANNED".equals(matched.getStatus())) {
            showError("Tài khoản của bạn đã bị khóa!");
            return;
        }

        navigateByRole(matched.getRole());
    }

    private void navigateByRole(String role) {
        String fxml;
        int width, height;
        switch (role) {
            case "Admin":
                fxml = "/com/client/view/AdminView.fxml";
                width = 1000; height = 650;
                break;
            case "Seller":
                fxml = "/com/client/view/SellerView.fxml";
                width = 900; height = 600;
                break;
            default: // Bidder
                fxml = "/com/client/view/AuctionListView.fxml";
                width = 900; height = 600;
                break;
        }
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Không thể mở màn hình: " + e.getMessage());
        }
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
