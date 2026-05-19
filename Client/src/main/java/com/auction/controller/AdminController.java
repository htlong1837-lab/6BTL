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

public class AdminController {

    @FXML private StackPane contentArea;
    private final Gson gson = new Gson();

    @FXML public void initialize() { showAuctions(); }

    @FXML public void showAuctions() {
        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance().send("LIST_AUCTIONS", Map.of());
                Platform.runLater(() -> {
                    if (!res.isSuccess()) return;
                    ObservableList<JsonObject> data = FXCollections.observableArrayList();
                    for (JsonElement e : gson.toJsonTree(res.getData()).getAsJsonArray())
                        data.add(e.getAsJsonObject());

                    TableView<JsonObject> table = new TableView<>(data);

                    TableColumn<JsonObject, String> cId = new TableColumn<>("Auction ID");
                    cId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("id").getAsString()));
                    cId.setPrefWidth(260);

                    TableColumn<JsonObject, String> cStatus = new TableColumn<>("Trạng thái");
                    cStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("status").getAsString()));

                    TableColumn<JsonObject, String> cPrice = new TableColumn<>("Giá hiện tại");
                    cPrice.setCellValueFactory(d -> new SimpleStringProperty(
                        String.format("%,.0f VND", d.getValue().get("currentPrice").getAsDouble())));

                    TableColumn<JsonObject, String> cDel = new TableColumn<>("Thao tác");
                    cDel.setPrefWidth(100);
                    cDel.setCellFactory(col -> new TableCell<>() {
                        final Button btn = new Button("Xóa");
                        {
                            btn.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;");
                            btn.setOnAction(e -> deleteAuction(
                                getTableView().getItems().get(getIndex()).get("id").getAsString()));
                        }
                        @Override protected void updateItem(String v, boolean empty) {
                            super.updateItem(v, empty);
                            setGraphic(empty ? null : btn);
                        }
                    });

                    table.getColumns().addAll(cId, cStatus, cPrice, cDel);

                    VBox vbox = new VBox(10, new Label("Các phiên đấu giá — " + data.size() + " mục"), table);
                    VBox.setVgrow(table, Priority.ALWAYS);
                    contentArea.getChildren().setAll(vbox);
                });
            } catch (IOException e) {
                Platform.runLater(() -> contentArea.getChildren().setAll(new Label("Lỗi: " + e.getMessage())));
            }
        }).start();
    }

    @FXML public void showUsers() {
    new Thread(() -> {
        try {
            Response res = ServerConnection.getInstance().send("LIST_USERS", Map.of());
            Platform.runLater(() -> {
                if (!res.isSuccess()) {
                    contentArea.getChildren().setAll(
                        new Label("Server chưa hỗ trợ LIST_USERS"));
                    return;
                }
                ObservableList<JsonObject> data = FXCollections.observableArrayList();
                for (JsonElement e : gson.toJsonTree(res.getData()).getAsJsonArray())
                    data.add(e.getAsJsonObject());

                TableView<JsonObject> table = new TableView<>(data);
                table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

                TableColumn<JsonObject, String> cName = new TableColumn<>("Tên đăng nhập");
                cName.setCellValueFactory(d ->
                    new SimpleStringProperty(d.getValue().get("username").getAsString()));

                TableColumn<JsonObject, String> cRole = new TableColumn<>("Vai trò");
                cRole.setCellValueFactory(d ->
                    new SimpleStringProperty(d.getValue().get("role").getAsString()));

                TableColumn<JsonObject, String> cStatus = new TableColumn<>("Trạng thái");
                cStatus.setCellValueFactory(d -> {
                    boolean banned = d.getValue().has("banned")
                                  && d.getValue().get("banned").getAsBoolean();
                    return new SimpleStringProperty(banned ? "Đã khóa" : "Hoạt động");
                });

                TableColumn<JsonObject, String> cAct = new TableColumn<>("Thao tác");
                cAct.setPrefWidth(160);
                cAct.setCellFactory(col -> new TableCell<>() {
                    final Button btnBan   = new Button("Khóa");
                    final Button btnUnban = new Button("Mở khóa");
                    final HBox   box      = new HBox(6, btnBan, btnUnban);
                    {
                        btnBan.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;");
                        btnUnban.setStyle("-fx-background-color:#27ae60;-fx-text-fill:white;");
                        btnBan.setOnAction(e ->
                            banUser(getTableView().getItems()
                                .get(getIndex()).get("id").getAsString(), true));
                        btnUnban.setOnAction(e ->
                            banUser(getTableView().getItems()
                                .get(getIndex()).get("id").getAsString(), false));
                    }
                    @Override protected void updateItem(String v, boolean empty) {
                        super.updateItem(v, empty);
                        setGraphic(empty ? null : box);
                    }
                });

                table.getColumns().addAll(cName, cRole, cStatus, cAct);
                VBox vbox = new VBox(10,
                    new Label("Người dùng — " + data.size() + " tài khoản"), table);
                VBox.setVgrow(table, Priority.ALWAYS);
                contentArea.getChildren().setAll(vbox);
                });
            } catch (IOException e) {
            Platform.runLater(() ->
                contentArea.getChildren().setAll(new Label("Lỗi: " + e.getMessage())));
            }
        }).start();
    }   

    private void banUser(String userId, boolean banned) {
        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance()
                    .send("BAN_USER", Map.of("userId", userId, "banned", banned));
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.INFORMATION,
                        res.getMessage(), ButtonType.OK).showAndWait();
                    showUsers();
                });
            } catch (IOException e) {
                Platform.runLater(() ->
                    new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait());
            }
        }).start();
    }


    private void deleteAuction(String auctionId) {
        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance()
                    .send("DELETE_AUCTION", Map.of("auctionId", auctionId));
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.INFORMATION,
                        res.getMessage(), ButtonType.OK).showAndWait();
                    showAuctions();
                });
            } catch (IOException e) {
                Platform.runLater(() ->
                    new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK).showAndWait());
            }
        }).start();
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

