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

    @FXML public void showApproval() {
        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance().send("LIST_ITEMS", Map.of());
                Platform.runLater(() -> {
                    if (!res.isSuccess()) return;
                    ObservableList<JsonObject> data = FXCollections.observableArrayList();
                    for (JsonElement e : gson.toJsonTree(res.getData()).getAsJsonArray())
                        data.add(e.getAsJsonObject());

                    TableView<JsonObject> table = new TableView<>(data);

                    TableColumn<JsonObject, String> cName = new TableColumn<>("Sản phẩm");
                    cName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("name").getAsString()));
                    cName.setPrefWidth(180);

                    TableColumn<JsonObject, String> cType = new TableColumn<>("Loại");
                    cType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("category").getAsString()));

                    TableColumn<JsonObject, String> cSeller = new TableColumn<>("Seller");
                    cSeller.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("sellerId").getAsString()));
                    cSeller.setPrefWidth(160);

                    // Cột nút duyệt / từ chối
                    // TODO: Server cần thêm action APPROVE_ITEM vào RequestRouter
                    TableColumn<JsonObject, String> cAct = new TableColumn<>("Thao tác");
                    cAct.setPrefWidth(180);
                    cAct.setCellFactory(col -> new TableCell<>() {
                        final Button btnOk  = new Button("Duyệt");
                        final Button btnNo  = new Button("Từ chối");
                        final HBox   box    = new HBox(5, btnOk, btnNo);
                        {
                            btnOk.setStyle("-fx-background-color:#27ae60;-fx-text-fill:white;");
                            btnNo.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;");
                            btnOk.setOnAction(e ->
                                approveItem(getTableView().getItems().get(getIndex()).get("id").getAsString(), true));
                            btnNo.setOnAction(e ->
                                approveItem(getTableView().getItems().get(getIndex()).get("id").getAsString(), false));
                        }
                        @Override protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            setGraphic(empty ? null : box);
                        }
                    });

                    table.getColumns().addAll(cName, cType, cSeller, cAct);

                    VBox vbox = new VBox(10,
                        new Label("Danh sách sản phẩm — " + data.size() + " mục"), table);
                    VBox.setVgrow(table, Priority.ALWAYS);
                    contentArea.getChildren().setAll(vbox);
                });
            } catch (IOException e) {
                Platform.runLater(() -> contentArea.getChildren().setAll(new Label("Lỗi: " + e.getMessage())));
            }
        }).start();
    }

    private void approveItem(String itemId, boolean approved) {
        new Thread(() -> {
            try {
                Response res = ServerConnection.getInstance()
                    .send("APPROVE_ITEM", Map.of("itemId", itemId, "approved", approved));
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.INFORMATION, res.getMessage(), ButtonType.OK).showAndWait();
                    if (res.isSuccess()) showApproval(); // làm mới bảng sau khi duyệt
                });
            } catch (IOException e) {
                Platform.runLater(() ->
                    new Alert(Alert.AlertType.ERROR, "Lỗi: " + e.getMessage(), ButtonType.OK).showAndWait());
            }
        }).start();
    }

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

                    table.getColumns().addAll(cId, cStatus, cPrice);

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
                Platform.runLater(() ->
                    new Alert(Alert.AlertType.INFORMATION,
                        res.getMessage(), ButtonType.OK).showAndWait());
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

