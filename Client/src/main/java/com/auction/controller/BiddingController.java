package com.auction.controller;

import com.auction.client.NotificationPopup;
import com.auction.client.ServerConnection;
import com.auction.client.SessionManager;
import com.auction.client.dto.Response;
import com.google.gson.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Map;

public class BiddingController {

    @FXML private Label productNameLabel, productDescLabel, currentPriceLabel, highestBidderLabel, messageLabel;
    @FXML private Label timerLabel;
    @FXML private ListView<String> bidHistoryList;
    @FXML private TextField bidAmountField;

    private String auctionId;
    private Runnable onBidSuccess;
    private final Gson gson = new Gson();
    private final ObservableList<String> history = FXCollections.observableArrayList();
    private Timeline countdown;
    private Timeline pollTimer;

    public void setOnBidSuccess(Runnable callback) { this.onBidSuccess = callback; }

    @FXML public void initialize() {
        bidHistoryList.setItems(history);
        // Tự động cập nhật giá và lịch sử đặt giá mỗi 3 giây
        pollTimer = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshAuction()));
        pollTimer.setCycleCount(Timeline.INDEFINITE);
        pollTimer.play();
    }

    /** Được gọi từ AuctionListController khi mở phòng */
    public void setAuction(JsonObject auction) {
        this.auctionId = auction.get("id").getAsString();
        updateUI(auction);
    }

    public void stopPolling() {
        if (pollTimer != null) pollTimer.stop();
        if (countdown != null) countdown.stop();
    }

    private void updateUI(JsonObject auction) {
        JsonObject item = auction.getAsJsonObject("item");
        productNameLabel.setText(item.get("name").getAsString());
        productDescLabel.setText(item.has("des") ? item.get("des").getAsString() : "");
        currentPriceLabel.setText("Giá hiện tại: " +
            String.format("%,.0f VND", auction.get("currentPrice").getAsDouble()));
        JsonElement leader = auction.get("highestBidder");
        String leaderName = (leader == null || leader.isJsonNull())
            ? "Chưa có"
            : leader.getAsJsonObject().get("name").getAsString();
        highestBidderLabel.setText("Người dẫn đầu: " + leaderName);

        history.clear();
        JsonElement histArr = auction.get("bidHistory");
        if (histArr != null && histArr.isJsonArray()) {
            for (JsonElement e : histArr.getAsJsonArray()) {
                JsonObject bid = e.getAsJsonObject();
                history.add(0, String.format("%s — %,.0f VND",
                    bid.get("bidderName").getAsString(),
                    bid.get("amount").getAsDouble()));
            }
        }

        JsonElement statusEl = auction.get("status");
        String status = (statusEl != null && !statusEl.isJsonNull()) ? statusEl.getAsString() : "";

        JsonElement endTimeEl = auction.get("endTime");
        if ("FINISHED".equals(status)) {
            if (countdown != null) countdown.stop();
            timerLabel.setText("Đã kết thúc");
            timerLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #ef4444;");
            // Nếu người dùng hiện tại là người thắng, đồng bộ số dư từ server
            JsonElement hb = auction.get("highestBidder");
            if (hb != null && !hb.isJsonNull()) {
                String winnerId = hb.getAsJsonObject().get("id").getAsString();
                if (winnerId.equals(SessionManager.getInstance().getUserId())) {
                    syncBalance();
                }
            }
        } else if (endTimeEl != null && !endTimeEl.isJsonNull()) {
            long endTime = endTimeEl.getAsLong();
            if (endTime > System.currentTimeMillis()) {
                startCountdown(endTime);
            }
        }
    }

    private void startCountdown(long endTime) {
        if (countdown != null) countdown.stop();

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            long remaining = endTime - System.currentTimeMillis();
            if (remaining <= 0) {
                countdown.stop();
                refreshAuction(); // kiểm tra server: có thể đã extend (anti-sniping) hoặc thực sự kết thúc
                return;
            }
            long hours   = remaining / 3_600_000;
            long minutes = (remaining % 3_600_000) / 60_000;
            long seconds = (remaining % 60_000) / 1_000;
            String text  = hours > 0
                ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
            timerLabel.setText(text);

            String color = remaining < 30_000 ? "#ef4444" : "#4ade80";
            timerLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        }));
        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();
    }

    @FXML
    void handlePlaceBid() {
        String text = bidAmountField.getText().trim();
        double amount;
        try { amount = Double.parseDouble(text); }
        catch (NumberFormatException e) { show("Số tiền không hợp lệ", false); return; }

        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance().send("PLACE_BID", Map.of(
                    "bidderId",  SessionManager.getInstance().getUserId(),
                    "auctionId", auctionId,
                    "bidAmount", amount          // gửi kiểu double, không phải String
                ));
                Platform.runLater(() -> {
                    show(res.getMessage(), res.isSuccess());
                    if (res.isSuccess()) {
                        bidAmountField.clear();
                        refreshAuction();
                        if (onBidSuccess != null) onBidSuccess.run();
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> show("Lỗi kết nối: " + e.getMessage(), false));
            }
        }).start();
    }

    @FXML
    void refreshAuction() {
        new Thread(() -> {
            try {
                // Kiểm tra tài khoản có bị khóa không
                Response sessionRes = ServerConnection.getInstance().send("CHECK_SESSION",
                    Map.of("userId", SessionManager.getInstance().getUserId()));
                if (!sessionRes.isSuccess()) {
                    Platform.runLater(() -> {
                        stopPolling();
                        NotificationPopup.showBanned(() -> {
                            Stage stage = (Stage) bidHistoryList.getScene().getWindow();
                            stage.close();
                        });
                    });
                    return;
                }

                // Kiểm tra phiên còn tồn tại không
                Response res = ServerConnection.getInstance().send("LIST_AUCTIONS", Map.of());
                if (!res.isSuccess()) return;
                JsonArray arr = gson.toJsonTree(res.getData()).getAsJsonArray();
                boolean found = false;
                for (JsonElement e : arr) {
                    JsonObject a = e.getAsJsonObject();
                    if (auctionId.equals(a.get("id").getAsString())) {
                        found = true;
                        Platform.runLater(() -> updateUI(a));
                        break;
                    }
                }
                if (!found) {
                    Platform.runLater(() -> {
                        stopPolling();
                        NotificationPopup.showAuctionDeleted(() -> {
                            Stage stage = (Stage) bidHistoryList.getScene().getWindow();
                            stage.close();
                        });
                    });
                }
            } catch (IOException ignored) {}
        }).start();
    }

    private void syncBalance() {
        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance().send("GET_BALANCE",
                    Map.of("userId", SessionManager.getInstance().getUserId()));
                if (res.isSuccess() && res.getData() != null) {
                    double newBalance = gson.toJsonTree(res.getData()).getAsDouble();
                    Platform.runLater(() -> SessionManager.getInstance().setBalance(newBalance));
                }
            } catch (IOException ignored) {}
        }).start();
    }

    private void show(String msg, boolean ok) {
        messageLabel.setStyle("-fx-text-fill: " + (ok ? "#27ae60" : "#e74c3c") + ";");
        messageLabel.setText(msg);
    }
}

