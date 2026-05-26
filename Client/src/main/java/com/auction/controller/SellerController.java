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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Map;

public class SellerController {

    @FXML private Label usernameLabel;
    @FXML private StackPane contentArea;
    private final Gson gson = new Gson();

    @FXML public void initialize() {
        usernameLabel.setText("Shop: " + SessionManager.getInstance().getUsername());
        showMyItems();
    }

    @FXML public void showMyItems() {
        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance().send("LIST_ITEMS", Map.of());
                Platform.runLater(() -> {
                    if (!res.isSuccess()) return;
                    String myId = SessionManager.getInstance().getUserId();
                    ObservableList<JsonObject> data = FXCollections.observableArrayList();
                    for (JsonElement e : gson.toJsonTree(res.getData()).getAsJsonArray()) {
                        JsonObject o = e.getAsJsonObject();
                        if (myId.equals(o.get("sellerId").getAsString())) data.add(o);
                    }
                    contentArea.getChildren().setAll(buildItemTable(data));
                });
            } catch (IOException e) {
                Platform.runLater(() -> contentArea.getChildren().setAll(new Label("Lỗi tải dữ liệu.")));
            }
        }).start();
    }

    private VBox buildItemTable(ObservableList<JsonObject> data) {
        TableView<JsonObject> table = new TableView<>(data);

        TableColumn<JsonObject, String> cName = new TableColumn<>("Tên sản phẩm");
        cName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("name").getAsString()));
        cName.setPrefWidth(200);

        TableColumn<JsonObject, String> cCat = new TableColumn<>("Loại");
        cCat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("category").getAsString()));

        TableColumn<JsonObject, String> cPrice = new TableColumn<>("Giá khởi điểm");
        cPrice.setCellValueFactory(d -> new SimpleStringProperty(
            String.format("%,.0f VND", d.getValue().get("startPrice").getAsDouble())));

        TableColumn<JsonObject, String> cAct = new TableColumn<>("Thao tác");
        cAct.setPrefWidth(100);
        cAct.setCellFactory(col -> new TableCell<>() {
            final Button btnDelete = new Button("Xóa");
            {
                btnDelete.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-cursor:hand;");
                btnDelete.setOnAction(e -> {
                    JsonObject item = getTableView().getItems().get(getIndex());
                    String itemId   = item.get("id").getAsString();
                    String itemName = item.get("name").getAsString();

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Xóa sản phẩm \"" + itemName + "\"?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.YES) deleteItem(itemId);
                    });
                });
            }
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });

        table.getColumns().addAll(cName, cCat, cPrice, cAct);

        VBox vbox = new VBox(10, new Label("Sản phẩm của tôi (" + data.size() + ")"), table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return vbox;
    }

    private void deleteItem(String itemId) {
        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance()
                    .send("DELETE_ITEM", Map.of("id", itemId));
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.INFORMATION, res.getMessage(), ButtonType.OK).showAndWait();
                    if (res.isSuccess()) showMyItems();
                });
            } catch (IOException e) {
                Platform.runLater(() ->
                    new Alert(Alert.AlertType.ERROR, "Lỗi: " + e.getMessage(), ButtonType.OK).showAndWait());
            }
        }).start();
    }

    // --- state dùng chung cho showMyAuctions và refreshMyAuctions ---
    private final ObservableList<JsonObject> auctionData = FXCollections.observableArrayList();
    private Label auctionBalanceLabel;
    private Label auctionCountLabel;

    @FXML public void showMyAuctions() {
        // Build UI một lần
        TableView<JsonObject> table = new TableView<>(auctionData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<JsonObject, String> cItem = new TableColumn<>("Sản phẩm");
        cItem.setCellValueFactory(d -> {
            JsonObject item = d.getValue().getAsJsonObject("item");
            return new SimpleStringProperty(item != null ? item.get("name").getAsString() : "?");
        });

        TableColumn<JsonObject, String> cStatus = new TableColumn<>("Trạng thái");
        cStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("status").getAsString()));

        TableColumn<JsonObject, String> cPrice = new TableColumn<>("Giá cuối");
        cPrice.setCellValueFactory(d -> new SimpleStringProperty(
            String.format("%,.0f VND", d.getValue().get("currentPrice").getAsDouble())));

        TableColumn<JsonObject, String> cWinner = new TableColumn<>("Người thắng");
        cWinner.setCellValueFactory(d -> {
            JsonElement hb = d.getValue().get("highestBidder");
            if (hb == null || hb.isJsonNull()) return new SimpleStringProperty("Không có");
            return new SimpleStringProperty(hb.getAsJsonObject().get("name").getAsString());
        });

        table.getColumns().addAll(cItem, cStatus, cPrice, cWinner);

        auctionCountLabel  = new Label("Phiên đấu giá của tôi");
        auctionBalanceLabel = new Label("Đang tải...");
        auctionBalanceLabel.setStyle("-fx-font-size:14;-fx-font-weight:bold;-fx-text-fill:#16a34a;");

        Button btnRefresh = new Button("Làm mới");
        btnRefresh.setStyle("-fx-background-color:#3b82f6;-fx-text-fill:white;-fx-cursor:hand;");
        btnRefresh.setOnAction(e -> fetchAuctionData());

        HBox topBar = new HBox(10, auctionCountLabel, btnRefresh);
        topBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox vbox = new VBox(10, topBar, auctionBalanceLabel, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        contentArea.getChildren().setAll(vbox);

        fetchAuctionData();
    }

    private void fetchAuctionData() {
        String myId = SessionManager.getInstance().getUserId();
        new Thread(() -> {
            try {
                Response aRes = ServerConnection.getInstance().send("LIST_AUCTIONS", Map.of());
                Response bRes = ServerConnection.getInstance().send("GET_BALANCE", Map.of("userId", myId));
                Platform.runLater(() -> {
                    if (aRes.isSuccess()) {
                        java.util.List<JsonObject> newData = new java.util.ArrayList<>();
                        for (JsonElement e : gson.toJsonTree(aRes.getData()).getAsJsonArray()) {
                            JsonObject o = e.getAsJsonObject();
                            JsonObject seller = o.has("seller") ? o.getAsJsonObject("seller") : null;
                            if (seller != null && myId.equals(seller.get("id").getAsString()))
                                newData.add(o);
                        }
                        auctionData.setAll(newData);
                        if (auctionCountLabel != null)
                            auctionCountLabel.setText("Phiên đấu giá của tôi (" + newData.size() + ")");
                    }
                    if (auctionBalanceLabel != null && bRes.isSuccess() && bRes.getData() != null) {
                        double balance = gson.toJsonTree(bRes.getData()).getAsDouble();
                        auctionBalanceLabel.setText(String.format("Số dư hiện tại: %,.0f VND", balance));
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    if (auctionBalanceLabel != null) auctionBalanceLabel.setText("Lỗi tải dữ liệu.");
                });
            }
        }).start();
    }

    @FXML public void showAddItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/client/view/ItemFormViewfinal.fxml"));
            Node node = loader.load();
            ProductFormController ctrl = loader.getController();
            ctrl.setOnSuccess(this::showMyItems);
            contentArea.getChildren().setAll(node);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML public void showCreateAuction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/client/view/CreateAuctionViewfinal.fxml"));
            Node node = loader.load();
            CreateAuctionController ctrl = loader.getController();
            ctrl.setOnSuccess(this::showMyAuctions);
            contentArea.getChildren().setAll(node);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadSubView(String path) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(path));
            contentArea.getChildren().setAll(node);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML public void handleLogout() {
        SessionManager.getInstance().clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/client/view/LoginViewfinal.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 700));
        } catch (Exception e) { e.printStackTrace(); }
    }
}

