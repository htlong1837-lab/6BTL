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

        table.getColumns().addAll(cName, cCat, cPrice);

        VBox vbox = new VBox(10, new Label("Sản phẩm của tôi (" + data.size() + ")"), table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return vbox;
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

