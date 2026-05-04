package com.auction.controller;

import java.lang.classfile.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import com.auction.client.model.Auction;

public class AuctionListController { // kết nối sang sever Auction nguyên lý sẽ xử lí
    @FXML private TableView<Auction>           auctionTable;
    @FXML private TableColumn<Auction, String> colName;
    @FXML private TableColumn<Auction, String> colPrice;
    @FXML private TableColumn<Auction, String> colStatus;
    @FXML private TableColumn<Auction, String> colEndTime;  
    @FXML private TableColumn<Auction, String> colSeller;   
    @FXML private TextField                    searchField;
    @FXML private Label                        statusLabel;
    @FXML private Label                        usernameLabel;

    // -state
    // observableList : khi thay đỏi tableview tự vẽ lại
    private ObservableList<Auction> auctionList =
    FXCollections.observableArrayList();
    // giữ bản gốc để filter tìm kiếm ko mất data
    private List<Auction> allAuctions;

    //- khởi tại tự động chạy khi màn hình load xong
    @FXML
    public void initialize(){
        setupColumns();
    }

    // bind với từng cột với getter ccuar class auction
    private void setupColums() {
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getItemName()));

        colPrice.setCellValueFactory(data -> new SimpleStringProperty(String.format("%,.0f VND",data.getValue().getCurrentPrice())));

        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        colEndTime.setCellValueFactory(data -> {
            String time = new SimpleDateFormat("HH:mm dd/MM//yyyy")
            .format(new Date(data.getValue().getEndTime()));
            return new SimpleStringProperty(time);
        });

        colSeller.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getSellerName())
        )

        auctionTable.setItems(auctionList);

    }
    @FXML
    public void handleSearch(ActionEvent e) {
        String kw = searchField.getText().trim().toLowerCase();
        // Filter trên allAuctions( bản gốc ) - không filter trên auctionList
        // vì filter trên list đang hiển thị sẽ mất data sau lần tìm thứ 2

        List<Auction> result = kw.isEmpty()
        ? allAuctions
        : allAuction.stream()
            .filter(a -> a.getItemName().toLowerCase().contains(kw))
            .collect(Colectors.toList());
        auctionList.setAll(result);
        updateStatus();
    }

    @FXML 
    private void handleRowClick(MouseEvent e ){
        // chỉ xử lí khi nhấn đúp , nháy đơn chỉ chọn hàng
        if (e.getClickCount()<2 ) return;

        Auction selected = auctionTable
            .getSelectionModel().getSelectedItem();
        if (selected == null) return;

        goToBidding(selected);
    }

    @FXML
    private void handleLogout() {
        goToLogin();
    }

    // chuyển màn

    private void goToBidding(Auction auction) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/client/view/BiddingView.fxml")
            );
            Parent root = loader.load();

            // Truyền auction sang Bidding Controller 

            BiddingController next = load.getController():
            next.setAuction(Auction);

            Stage stage = (Stage) auctionTable.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600))

        }catch (IOException e) {e.printStackTrace();}
    }

    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/client/view/LoginView.fxml")
            );

            Parent root = loader.load();
            Stage stage =(Stage) auctionTable.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 700));

        }catch(IOException e ) {e.printStackTrace();}
    }

    // -- helper
    private void updateStatus() {
        statusLabel.setText(auctionList.size() + "phiên đấu giá");
    }

    // được gọi từ LoginController sau khi đnăg nhập thành công

    public void setUsername(String name) {
        usernameLabel.setText("Xin chào" + name);
    }

    
}
