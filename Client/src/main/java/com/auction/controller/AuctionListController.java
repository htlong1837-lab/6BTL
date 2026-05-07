package com.auction.controller;

import com.auction.client.ServerConnection;
import com.auction.client.SessionManager;
import com.auction.client.dto.Response;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Map;

public class AuctionListController {

    @FXML private Label usernameLabel, balanceLabel, statusLabel;
    @FXML private TableView<JsonObject> auctionTable;
    @FXML private TableColumn<JsonObject, String> colName, colPrice, colLeader, colStatus, colAction;

    private final Gson gson = new Gson();
    private final ObservableList<JsonObject> auctions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        SessionManager s = SessionManager.getInstance();
        usernameLabel.setText("Xin chào, " + s.getUsername());
        balanceLabel.setText("Số dư: " + String.format("%,.0f VND", s.getBalance()));

        colName.setCellValueFactory(d -> {
            JsonElement item = d.getValue().get("item");
            if (item == null || item.isJsonNull()) return new SimpleStringProperty("N/A");
            return new SimpleStringProperty(item.getAsJsonObject().get("name").getAsString());
        });
        colPrice.setCellValueFactory(d ->
            new SimpleStringProperty(String.format("%,.0f", d.getValue().get("currentPrice").getAsDouble())));
        colLeader.setCellValueFactory(d -> {
            JsonElement e = d.getValue().get("highestBidder");
            return new SimpleStringProperty(e == null || e.isJsonNull() ? "Chưa có" : e.getAsString());
        });
        colStatus.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().get("status").getAsString()));

        // Cột nút "Vào phòng"
        colAction.setCellFactory(col -> new TableCell<>() {
            final Button btn = new Button("Vào phòng");
            { btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
              btn.setOnAction(e -> openAuctionRoom(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        auctionTable.setItems(auctions);
        loadAuctions();
    }

    @FXML
    public void loadAuctions() {
        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance().send("LIST_AUCTIONS", Map.of());
                if (res.isSuccess()) {
                    JsonArray arr = gson.toJsonTree(res.getData()).getAsJsonArray();
                    Platform.runLater(() -> {
                        auctions.clear();
                        for (JsonElement e : arr) auctions.add(e.getAsJsonObject());
                    });
                } else {
                    Platform.runLater(() -> statusLabel.setText(res.getMessage()));
                }
            } catch (IOException e) {
                Platform.runLater(() -> statusLabel.setText("Lỗi kết nối: " + e.getMessage()));
            }
        }).start();
    }

    private void openAuctionRoom(JsonObject auction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/client/view/AuctionRoomViewfinal.fxml"));
            Parent root = loader.load();
            BiddingController ctrl = loader.getController();
            ctrl.setAuction(auction);
            Stage stage = new Stage();
            stage.setTitle("Phòng đấu giá — " +
                auction.getAsJsonObject("item").get("name").getAsString());
            stage.setScene(new Scene(root, 700, 520));
            stage.show();
        } catch (Exception e) {
            statusLabel.setText("Không mở được phòng: " + e.getMessage());
        }
    }

    @FXML void showAuctions() { loadAuctions(); }

    @FXML
    void showWallet() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/client/view/WalletViewfinal.fxml"));
            Stage st = new Stage();
            st.setTitle("Ví của tôi");
            st.setScene(new Scene(root, 380, 280));
            st.show();
        } catch (Exception e) { statusLabel.setText("Lỗi: " + e.getMessage()); }
    }

    @FXML
    void handleLogout() {
        SessionManager.getInstance().clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/client/view/LoginViewfinal.fxml"));
            Stage stage = (Stage) auctionTable.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 700));
        } catch (Exception e) { e.printStackTrace(); }
    }
}

