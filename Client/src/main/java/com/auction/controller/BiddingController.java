package com.auction.controller;

import com.auction.client.model.Auction;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BiddingController {

    @FXML private Label        itemNameLabel;
    @FXML private Label        itemDescLabel;
    @FXML private Label        currentPriceLabel;
    @FXML private Label        topBidderLabel;
    @FXML private Label        countdownLabel;
    @FXML private Label        bidMessageLabel;
    @FXML private TextField    bidAmountField;
    @FXML private Button       bidButton;
    @FXML private ListView<String> bidHistoryList;

    private Auction  auction;
    private Timeline countdownTimer;
    private double   currentPrice;
    private String   topBidder = "";

    private ObservableList<String> historyItems = FXCollections.observableArrayList();

    // nhận dữ liệu từ AuctionListController
    public void setAuction(Auction auction) {
        this.auction      = auction;
        this.currentPrice = auction.getCurrentPrice();
        bidHistoryList.setItems(historyItems);
        renderInfo();
        startCountdown();
    }

    private void renderInfo() {
        itemNameLabel.setText(auction.getItemName());
        itemDescLabel.setText(
            "Giá khởi điểm: " + String.format("%,.0f VND", auction.getCurrentPrice())
        );
        updatePriceUI();
    }

    private void updatePriceUI() {
        currentPriceLabel.setText(String.format("%,.0f VND", currentPrice));
        topBidderLabel.setText(topBidder.isEmpty() ? "(chưa có)" : topBidder);
    }

    private void startCountdown() {
        countdownTimer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> tick())
        );
        countdownTimer.setCycleCount(Timeline.INDEFINITE);
        countdownTimer.play();
    }

    private void tick() {
        long remaining = auction.getEndTime() - System.currentTimeMillis();

        if (remaining <= 0) {
            countdownLabel.setText("Kết thúc");
            countdownLabel.setStyle(
                "-fx-font-size: 28; -fx-font-weight: bold;" +
                "-fx-text-fill: #e74c3c;"
            );
            bidButton.setDisable(true);
            bidAmountField.setDisable(true);
            showMessage("Phiên đấu giá kết thúc", "red");
            countdownTimer.stop();
            return;
        }

        long h = remaining / 3_600_000;
        long m = (remaining % 3_600_000) / 60_000;
        long s = (remaining % 60_000) / 1_000;

        countdownLabel.setText(String.format("%02d:%02d:%02d", h, m, s));

        if (remaining < 60_000) {
            countdownLabel.setStyle(
                "-fx-font-size: 32; -fx-font-weight: bold;" +
                "-fx-text-fill: #e74c3c;"
            );
        } else {
            countdownLabel.setStyle(
                "-fx-font-size: 32; -fx-font-weight: bold;" +
                "-fx-text-fill: #2980b9;"
            );
        }
    }

    @FXML
    private void handlePlaceBid() {
        String input = bidAmountField.getText().trim();

        if (input.isEmpty()) {
            showMessage("Vui lòng nhập số tiền", "red");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(input.replace(",", ""));
        } catch (NumberFormatException e) {
            showMessage("Số tiền không hợp lệ", "red");
            return;
        }

        if (amount <= currentPrice) {
            showMessage("Giá phải cao hơn " + String.format("%,.0f VND", currentPrice), "red");
            return;
        }

        currentPrice = amount;
        topBidder    = "Bạn"; // TODO: lấy username từ server
        updatePriceUI();
        addHistory("Bạn", amount);
        showMessage("Đặt giá thành công!", "green");
        bidAmountField.clear();
        // TODO: gửi BID request lên server
    }

    private void addHistory(String name, double amount) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        historyItems.add(0, String.format("[%s] %s -> %,.0f VND", time, name, amount));
    }

    public void showMessage(String msg, String color) {
        bidMessageLabel.setText(msg);
        bidMessageLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    public void handleBack() {
        if (countdownTimer != null) countdownTimer.stop();
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/client/view/AuctionListView.fxml")
            );
            Parent root  = loader.load();
            Stage  stage = (Stage) bidAmountField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (IOException e) { e.printStackTrace(); }
    }
}
