package com.auction.controller;

import com.auction.client.model.Auction;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class BiddingController {
    @FXML private Label itemNameLabel;
    @FXML private Label itemDescribeLabel;
    @FXML private Label currentPrice;
    @FXML private Label currentPriceLabel;
    @FXML private Label topBiderLabel;
    @FXML private Label countdownLabel;
    @FXML private TextField bidAmountField;
    @FXML private Button bidButton;
    @FXML private ListView<String> bidHistory;

    // nhận dữ liệu từ AuctionlistControler
    public void setAuction(Auction auction) {

    }

    // hiện thị thông tin lên UI
    private void renderInfo() {}

    // cập nhật giá 
    private void updatePriceUI() {}


    //đặt giá
    @FXML 
    private void handlePlaceBid() {}
    
    @FXML 
    public void handleBack() {}
}
