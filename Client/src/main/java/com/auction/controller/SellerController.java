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

    @FXML public void showMyAuctions() {
        String myId = SessionManager.getInstance().getUserId();
        new Thread(() -> {
            try {
                Response aRes = ServerConnection.getInstance().send("LIST_AUCTIONS", Map.of());
                Response bRes = ServerConnection.getInstance().send("GET_BALANCE", Map.of("userId", myId));
                Platform.runLater(() -> {
                    ObservableList<JsonObject> data = FXCollections.observableArrayList();
                    if (aRes.isSuccess()) {
                        for (JsonElement e : gson.toJsonTree(aRes.getData()).getAsJsonArray()) {
                            JsonObject o = e.getAsJsonObject();
                            JsonObject seller = o.has("seller") ? o.getAsJsonObject("seller") : null;
                            if (seller != null && myId.equals(seller.get("id").getAsString())) {
                                data.add(o);
                            }
                        }
                    }

                    // Bảng phiên đấu giá
                    TableView<JsonObject> table = new TableView<>(data);
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
                        JsonObject bidder = hb.getAsJsonObject();
                        return new SimpleStringProperty(bidder.get("name").getAsString());
                    });

                    table.getColumns().addAll(cItem, cStatus, cPrice, cWinner);

                    double balance = (bRes.isSuccess() && bRes.getData() != null)
                        ? gson.toJsonTree(bRes.getData()).getAsDouble() : 0;
                    Label balLabel = new Label(String.format("Số dư hiện tại: %,.0f VND", balance));
                    balLabel.setStyle("-fx-font-size:14;-fx-font-weight:bold;-fx-text-fill:#16a34a;");

                    VBox vbox = new VBox(10,
                        new Label("Phiên đấu giá của tôi (" + data.size() + ")"),
                        balLabel, table);
                    VBox.setVgrow(table, Priority.ALWAYS);
                    contentArea.getChildren().setAll(vbox);
                });
            } catch (IOException e) {
                Platform.runLater(() -> contentArea.getChildren().setAll(new Label("Lỗi: " + e.getMessage())));
            }
        }).start();
    }

    @FXML public void showAddItem() {
        loadSubView("/com/client/view/ItemFormViewfinal.fxml");
    }

    @FXML public void showCreateAuction() {
        loadSubView("/com/client/view/CreateAuctionViewfinal.fxml");
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

