package com.auction.controller;

import com.auction.client.model.Auction;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class BiddingController {
    @FXML private Label itemNameLabel;
    @FXML private Label itemDescLabel;
    @FXML private Label currentPrice;
    @FXML private Label currentPriceLabel;
    @FXML private Label topBiderLabel;
    @FXML private Label countdownLabel;
    @FXML private TextField bidAmountField;
    @FXML private Button bidButton;
    @FXML private ListView<String> bidHistory;

    private Auction auction;
    private Timeline countdownTimer;
    private double currentPrice;
    prvate String topBidder ="";

    private ObserableList<String> historyItems = FXCollections.observableArrayList();

    // nhận dữ liệu từ AuctionlistControler
    public void setAuction(Auction auction) {

    }

    // hiện thị thông tin lên UI
    private void renderInfo() {
        itemNameLabel.setText(auction.getItemName());
        itemDescLabel.setText(
            "Giá khởi điểm: "
            + String.format("%,.0f VND", auction.getCurrentPrice())
        );
        updatePriceUI();
    }

    private void updatePriceUI() {
        currentPriceLabel.setText(
            String.format("%, 0f VNd", currentPrice )
        );

        topBidderLabel.setText(
            topBidder.isEmpty() ? "(chưa có)" : topBidder
        );
    }

    private void startCountdown() {
        countdownTimer = new Timeline(
            new KeyFarme(Duration.seconds(1) , e -> tick())
        );

        countdownTimer.setCycleCount(Timeline.INDEFINITE);
        countdownTimer.play();
    }

    private void tick() {
        long remaining = auction.getEndTIme() - System.currentTimeMillis();

        // Hết giờ
        if (remaining <= 0) {
            countdownLabel.setText("Kết thúc");
            countdownLabel.setStyle(
                "-fx-font-size: 28 ; -fx-font-weight: bold;" +
                "-fx-text-fill: #e74c3c"
            );
            bidButton.setDisable(true);
            bidAmountField.setDisable(true);
            bidMessageLabel.setText("Phiên đấu giá kết thúc");
            countdownTimer.stop();
            return;

        }
    }
// tính giờ
    long h = remaining / 3_600_000;
    long m = (remaining % 3_600_000) /60_000;
    long s = (remaining % 60_000) / 1_000;

    countdownLabel.setText(String.format("%02d:%02d:%02d",h,m,s));
    // đổi màu đỏ khi còn dưới 60s

    if (remaining <60_000) {
        countdownLabel.setStyle(
            "-fx-font-size : 32; -fx-font-weight: bold;" +
            "-fx-text-fill : #e74c3c;"
        );

    }else {
        countdownLabel.setStyle(
            "-fx-font-size: 32; -fx-font-weight: bold;"
            "-fx-text-fill : #2980b9"
        );
    }
    // cập nhật giá 


    //đặt giá
    @FXML 
    private void handlePlaceBid() {
        String input = bidAmountFIeld.getText().trim();

        if ( input.isEmpty()) {
            showMessage("Vui lòng nhập số tiền", "red");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(input.replace(",",""));

        }catch (NumberFormatException e) {
            showMessage("Số tiền không hợp lệ" ,"red");
            return;
        }

        if (amount <= currentPrice) {
            showMessage("Giá phải cao hơn" + String.format("%,0f,VND",currentPrice),"red");
            return;
        }
        // tất cả validate đã qua cập nhật state

        currentPrice = amount;
        topBidder = "Bạn" //TÔDO : lấy từ sever sang sau
        updatePriceUI();
        addHistory("Bạn",amount);

        showMessage("Đặt giá thành công!" ,"green");
        bidAmountField.clear();
        // TODO gửi BID request lên sever
        // networkService.getInstance().send("PLACE_BID",...)


    }

    // -- add lịch sử bid

    private void addHistory(String name , double amount) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Data());

        historyItems.add(0,
            String.format("[%s] %s -> %, .0f VND",time,name,amount)
        );
    }

    // helper
    public void showMessage(String msg , String color) {
        bidMessageLabel.setText(msg)l
        bidMessageLabel.setStyle("-fx-text-fill:" +color";");
    }
    // quay về trang chính
    @FXML 
    public void handleBack() {
        // phải dừng time chạy nếu ko khi thoát bộ nhỡ sẽ vẫn chạy
        if(countdownTimer !=null) countdownTimer.stop();

        try{
            FXMLLoader loader = new FXML(
                getClass().getResource("/com/client/view/AuctionListView.fxml")
            );

            Parent root = loader.load();
            Stage stage = (Stage) bidAmountField.getScene().getWindow();
            stage.setScene(new Scene(root, 900 ,600));

        } catch(IOException e) {e.printStackTrace();}
    }
}
