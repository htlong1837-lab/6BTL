package com.auction.controller;

import com.auction.client.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
public class WalletController {
    @FXML private Label balanceLabel ,messageLabel;
    @FXML private TextField amountField;

    @FXML public void initialize() {
        balanceLabel.setText("Số dư: " +
            String.format("%,.0f VND", SessionManager.getInstance().getBalance())
        );
    }
}
