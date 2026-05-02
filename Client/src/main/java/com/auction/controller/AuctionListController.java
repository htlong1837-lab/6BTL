package com.auction.controller;

import java.lang.classfile.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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
    
    @FXML
    public void handleSearch(ActionEvent e) {

    }
}
