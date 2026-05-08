package com.auction.controller;

import com.auction.client.ServerConnection;
import com.auction.client.SessionManager;
import com.auction.client.dto.Response;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateAuctionController {

    @FXML private ComboBox<String> itemCombo;
    @FXML private TextField durationField;
    @FXML private Label messageLabel;

    private final Gson gson = new Gson();
    private final Map<String, String> nameToId = new HashMap<>();  // tên → itemId

    @FXML public void initialize() { loadMyItems(); }

    private void loadMyItems() {
        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance().send("LIST_ITEMS", Map.of());
                if (!res.isSuccess()) return;
                String myId = SessionManager.getInstance().getUserId();
                Platform.runLater(() -> {
                    for (JsonElement e : gson.toJsonTree(res.getData()).getAsJsonArray()) {
                        JsonObject o = e.getAsJsonObject();
                        boolean isMyItem   = myId.equals(o.get("sellerId").getAsString());
                        boolean isApproved = o.has("approved") && o.get("approved").getAsBoolean();
                        if (isMyItem && isApproved) {
                            String name = o.get("name").getAsString();
                            nameToId.put(name, o.get("id").getAsString());
                            itemCombo.getItems().add(name);
                        }

                    if (itemCombo.getItems().isEmpty()) {
                        msg("Chưa có sản phẩm nào được admin duyệt.", false);
}

                    }
                
                });
            } catch (IOException e) {
                Platform.runLater(() -> msg("Lỗi tải sản phẩm: " + e.getMessage(), false));
            }
        }).start();
    }

    @FXML void handleCreate() {
        String selected = itemCombo.getValue();
        String durText  = durationField.getText().trim();
        if (selected == null || durText.isEmpty()) {
            msg("Chọn sản phẩm và nhập thời gian.", false); return;
        }
        long durationMillis;
        try { durationMillis = Long.parseLong(durText) * 3600_000L; }
        catch (NumberFormatException e) { msg("Thời gian không hợp lệ.", false); return; }

        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance().send("CREATE_AUCTION", Map.of(
                    "itemId",         nameToId.get(selected),
                    "sellerId",       SessionManager.getInstance().getUserId(),
                    "durationMillis", durationMillis   // long → server parse đúng
                ));
                Platform.runLater(() -> msg(res.getMessage(), res.isSuccess()));
            } catch (IOException e) {
                Platform.runLater(() -> msg("Lỗi: " + e.getMessage(), false));
            }
        }).start();
    }

    private void msg(String m, boolean ok) {
        messageLabel.setStyle("-fx-text-fill:" + (ok ? "#27ae60" : "#e74c3c") + ";");
        messageLabel.setText(m);
    }
}

