package com.auction.controller;

import com.auction.client.ServerConnection;
import com.auction.client.SessionManager;
import com.auction.client.dto.Response;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductFormController {

    @FXML private TextField nameField, priceField;
    @FXML private TextArea  descField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private Label messageLabel;
    @FXML private Button submitBtn;

    // Dynamic containers
    @FXML private VBox artFields, vehicleFields, electronicsFields;

    // Art
    @FXML private TextField artistField, mediumField;
    // Vehicle
    @FXML private TextField makeField, modelField, yearField;
    // Electronics
    @FXML private TextField brandField, warrantyField;

    @FXML public void initialize() {
        categoryCombo.getItems().addAll("Art", "Vehicle", "Electronics");
    }

    @FXML void onCategoryChanged() {
        String sel = categoryCombo.getValue();
        show(artFields,         "Art".equals(sel));
        show(vehicleFields,     "Vehicle".equals(sel));
        show(electronicsFields, "Electronics".equals(sel));
    }

    private void show(VBox box, boolean v) { box.setVisible(v); box.setManaged(v); }

    @FXML void handleSubmit() {
        String name     = nameField.getText().trim();
        String des      = descField.getText().trim();
        String priceStr = priceField.getText().trim();
        String category = categoryCombo.getValue();

        if (name.isEmpty() || des.isEmpty() || priceStr.isEmpty() || category == null) {
            msg("Vui lòng điền đầy đủ thông tin cơ bản.", false); return;
        }
        double price;
        try { price = Double.parseDouble(priceStr); }
        catch (NumberFormatException e) { msg("Giá không hợp lệ.", false); return; }

        // Dùng Map<String, Object> để gửi cả String lẫn Number
        Map<String, Object> payload = new HashMap<>(Map.of(
            "id",         UUID.randomUUID().toString(),
            "type",       category.toUpperCase(),
            "name",       name,
            "des",        des,
            "startPrice", price,          // kiểu double → server parse đúng
            "category",   category,
            "sellerId",   SessionManager.getInstance().getUserId()
        ));

        switch (category) {
            case "Art":
                if (artistField.getText().isEmpty() || mediumField.getText().isEmpty()) {
                    msg("Điền đầy đủ thông tin nghệ thuật.", false); return;
                }
                payload.put("artist", artistField.getText());
                payload.put("medium", mediumField.getText());
                break;
            case "Vehicle":
                if (makeField.getText().isEmpty() || modelField.getText().isEmpty() || yearField.getText().isEmpty()) {
                    msg("Điền đầy đủ thông tin xe.", false); return;
                }
                payload.put("make",  makeField.getText());
                payload.put("model", modelField.getText());
                payload.put("year",  Integer.parseInt(yearField.getText()));  // int
                break;
            case "Electronics":
                if (brandField.getText().isEmpty() || warrantyField.getText().isEmpty()) {
                    msg("Điền đầy đủ thông tin điện tử.", false); return;
                }
                payload.put("brand",          brandField.getText());
                payload.put("warrantyMonths", Integer.parseInt(warrantyField.getText()));  // int
                break;
        }

        if (submitBtn != null) submitBtn.setDisable(true);
        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance().send("CREATE_ITEM", payload);
                javafx.application.Platform.runLater(() -> {
                    msg(res.getMessage(), res.isSuccess());
                    if (submitBtn != null) submitBtn.setDisable(false);
                });
            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    msg("Lỗi: " + e.getMessage(), false);
                    if (submitBtn != null) submitBtn.setDisable(false);
                });
            }
        }).start();
    }

    private void msg(String m, boolean ok) {
        messageLabel.setStyle("-fx-text-fill:" + (ok ? "#27ae60" : "#e74c3c") + ";");
        messageLabel.setText(m);
    }
}

