package com.auction.controller;

import com.auction.client.model.Auction;
import com.auction.client.model.FakeDataHelper;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AuctionListController {

    @FXML private TableView<Auction>           auctionTable;
    @FXML private TableColumn<Auction, String> colName;
    @FXML private TableColumn<Auction, String> colPrice;
    @FXML private TableColumn<Auction, String> colStatus;
    @FXML private TableColumn<Auction, String> colEndTime;
    @FXML private TableColumn<Auction, String> colSeller;
    @FXML private TextField                    searchField;
    @FXML private Label                        statusLabel;
    @FXML private Label                        usernameLabel;

    private ObservableList<Auction> auctionList = FXCollections.observableArrayList();
    private List<Auction> allAuctions;

    @FXML
    public void initialize() {
        setupColumns();
        loadFakeData(); // TODO: thay bằng loadFromServer()
    }

    private void setupColumns() {
        colName.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getItemName()));

        colPrice.setCellValueFactory(data ->
            new SimpleStringProperty(
                String.format("%,.0f VND", data.getValue().getCurrentPrice())));

        colStatus.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getStatus()));

        colEndTime.setCellValueFactory(data -> {
            String time = new SimpleDateFormat("HH:mm dd/MM/yyyy")
                .format(new Date(data.getValue().getEndTime()));
            return new SimpleStringProperty(time);
        });

        colSeller.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getSellerName()));

        auctionTable.setItems(auctionList);
    }

    private void loadFakeData() {
        auctionList.setAll(FakeDataHelper.makeAuctions());
        allAuctions = List.copyOf(auctionList);
        updateStatus();
    }

    @FXML
    public void handleSearch(ActionEvent e) {
        String kw = searchField.getText().trim().toLowerCase();
        List<Auction> result = kw.isEmpty()
            ? allAuctions
            : allAuctions.stream()
                .filter(a -> a.getItemName().toLowerCase().contains(kw))
                .collect(Collectors.toList());
        auctionList.setAll(result);
        updateStatus();
    }

    @FXML
    private void handleRowClick(MouseEvent e) {
        if (e.getClickCount() < 2) return;
        Auction selected = auctionTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        goToBidding(selected);
    }

    @FXML
    private void handleLogout() {
        goToLogin();
    }

    private void goToBidding(Auction auction) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/client/view/BiddingView.fxml")
            );
            Parent root = loader.load();

            BiddingController next = loader.getController();
            next.setAuction(auction);

            Stage stage = (Stage) auctionTable.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/client/view/LoginView.fxml")
            );
            Parent root  = loader.load();
            Stage  stage = (Stage) auctionTable.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 700));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void updateStatus() {
        statusLabel.setText(auctionList.size() + " phiên đấu giá");
    }

    public void setUsername(String name) {
        usernameLabel.setText("Xin chào " + name);
    }
}
