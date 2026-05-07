package com.auction.controller;

import com.auction.client.ServerConnection;
import com.auction.client.SessionManager;
import com.auction.client.dto.Response;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.Map;

public class WalletController {
    @FXML private Label balanceLabel, messageLabel;
    @FXML private TextField amountField;

    @FXML public void initialize() {
        balanceLabel.setText(String.format("%,.0f VND", SessionManager.getInstance().getBalance()));
    }

    @FXML
    private void handleDeposit() {
        String text = amountField.getText().trim();
        if (text.isEmpty()) { msg("Vui lòng nhập số tiền.", false); return; }

        double amount;
        try { amount = Double.parseDouble(text); }
        catch (NumberFormatException e) { msg("Số tiền không hợp lệ.", false); return; }

        if (amount <= 0) { msg("Số tiền phải lớn hơn 0.", false); return; }

        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance().send("DEPOSIT", Map.of(
                    "bidderId", SessionManager.getInstance().getUserId(),
                    "amount",   amount
                ));
                Platform.runLater(() -> {
                    msg(res.getMessage(), res.isSuccess());
                    if (res.isSuccess()) {
                        double newBalance = ((Number) res.getData()).doubleValue();
                        SessionManager.getInstance().setBalance(newBalance);
                        balanceLabel.setText(String.format("%,.0f VND", newBalance));
                        amountField.clear();
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> msg("Lỗi kết nối: " + e.getMessage(), false));
            }
        }).start();
    }

    private void msg(String m, boolean ok) {
        messageLabel.setStyle("-fx-text-fill: " + (ok ? "#27ae60" : "#e74c3c") + ";");
        messageLabel.setText(m);
    }
}
