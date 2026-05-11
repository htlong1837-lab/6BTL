
package com.auction.controller;

import com.auction.client.ServerConnection;
import com.auction.client.SessionManager;
import com.auction.client.dto.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class LoginController {

    @FXML private TextField userNameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        // Ẩn label báo lỗi lúc mới mở form.
        // Nếu không:
        // có thể label trống vẫn chiếm chỗ
        // hoặc hiện lỗi cũ
    }


    @FXML
private void handleLogin() {
    String username = userNameField.getText().trim();
    String password = passwordField.getText();

    if (username.isEmpty() || password.isEmpty() || password.length() < 6) {
        showError("Vui lòng nhập đầy đủ thông tin");
        return;// Nếu thông tin không hợp lệ, hiển thị lỗi và dừng xử lý tiếp.
    }

    
    try {
        
        Response res = ServerConnection.getInstance().send("LOGIN", Map.of(
            "username",username ,
            "password",password
        ));

        if (!res.isSuccess()) {
            showError(res.getMessage());
            return;
        }

        // Server trả user object trong data — parse role
        // Tạm dùng Gson để lấy role
        Gson gson = new Gson();
        JsonObject userJson = gson.toJsonTree(res.getData()).getAsJsonObject();
        String role    = userJson.get("role").getAsString(); // "ADMIN" / "SELLER" / "BIDDER"
        String userId  = userJson.get("id").getAsString();
        String name    = userJson.get("username").getAsString();
        double balance = userJson.has("balance") ? userJson.get("balance").getAsDouble() : 0.0;
        SessionManager.getInstance().init(userId, name, role, balance);
        navigateByRole(role);

    } catch (IOException e) {
        showError("Lỗi kết nối server: " + e.getMessage());
    }
}

    private void navigateByRole(String role) {
        String fxml;
        int width, height;
        switch (role) {
            case "ADMIN":
                fxml = "/com/client/view/AdminViewfinal.fxml";
                width = 1000; height = 650;
                break;
            case "SELLER":
                fxml = "/com/client/view/SellerViewfinal.fxml";
                width = 900; height = 600;
                break;
            default: // BIDDER
                fxml = "/com/client/view/AuctionListViewfinal.fxml";
                width = 900; height = 600;
                break;
        }
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) passwordField.getScene().getWindow();
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

    @FXML
    private void goToRegister(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/client/view/RegisterViewfinal.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) passwordField.getScene().getWindow();
            stage.setScene(new Scene(root, 750, 700));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
