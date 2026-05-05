package com.auction.controller;

import com.auction.client.model.SellerProduct;
import com.auction.client.model.FakeDataHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SellerController {

    // ── FXML binding ──────────────────────────────────────────
    @FXML private TableView<SellerProduct>           productTable;
    @FXML private TableColumn<SellerProduct, String> colId;
    @FXML private TableColumn<SellerProduct, String> colName;
    @FXML private TableColumn<SellerProduct, String> colPrice;
    @FXML private TableColumn<SellerProduct, String> colCurrentPrice;
    @FXML private TableColumn<SellerProduct, String> colStatus;
    @FXML private TableColumn<SellerProduct, String> colTopBidder;
    @FXML private TextField                          searchField;
    @FXML private Label                              statusLabel;

    // ── State ─────────────────────────────────────────────────
    private ObservableList<SellerProduct> productList =
        FXCollections.observableArrayList();
    private List<SellerProduct> allProducts;

    // ── Khởi tạo ──────────────────────────────────────────────
    @FXML
    public void initialize() {
        setupColumns();
        loadFakeData(); // TODO: thay bằng loadFromServer()
    }

    private void setupColumns() {
        colId  .setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Format số tiền — cần lambda vì không có getter trả String
        colPrice.setCellValueFactory(data ->
            new SimpleStringProperty(
                String.format("%,.0f VND", data.getValue().getStartPrice())
            )
        );
        colCurrentPrice.setCellValueFactory(data ->
            new SimpleStringProperty(
                String.format("%,.0f VND", data.getValue().getCurrentPrice())
            )
        );
        colStatus   .setCellValueFactory(new PropertyValueFactory<>("status"));
        colTopBidder.setCellValueFactory(new PropertyValueFactory<>("topBidder"));

        productTable.setItems(productList);
    }

    private void loadFakeData() {
        // TODO: xóa, thay bằng gọi NetworkService
        productList.setAll(FakeDataHelper.makeSellerProducts());
        allProducts = List.copyOf(productList);
        updateStatus();
    }

    // ── Sự kiện ───────────────────────────────────────────────
    @FXML
    private void handleSearch() {
        String kw = searchField.getText().trim().toLowerCase();

        List<SellerProduct> result = kw.isEmpty()
            ? allProducts
            : allProducts.stream()
                .filter(p -> p.getName().toLowerCase().contains(kw))
                .collect(Collectors.toList());

        productList.setAll(result);
        updateStatus();
    }

    @FXML
    private void handleRowClick(MouseEvent e) {
        // Single click chỉ chọn hàng, không làm gì thêm
    }

    @FXML
    private void handleAddProduct() {
        goToProductForm(null); // null = thêm mới
    }

    @FXML
    private void handleEdit() {
        SellerProduct selected = productTable
            .getSelectionModel().getSelectedItem();

        // Validate phải chọn sản phẩm trước
        if (selected == null) {
            showAlert("Chưa chọn sản phẩm",
                "Vui lòng click chọn sản phẩm muốn sửa!");
            return;
        }

        goToProductForm(selected); // có data = chế độ sửa
    }

    @FXML
    private void handleDelete() {
        SellerProduct selected = productTable
            .getSelectionModel().getSelectedItem();

        // Validate phải chọn sản phẩm trước
        if (selected == null) {
            showAlert("Chưa chọn sản phẩm",
                "Vui lòng click chọn sản phẩm muốn xóa!");
            return;
        }

        // Hỏi xác nhận trước khi xóa
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Xóa: " + selected.getName());
        confirm.setContentText("Bạn có chắc muốn xóa không?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            productList.remove(selected);
            allProducts = List.copyOf(productList);
            updateStatus();
            // TODO: gửi DELETE request lên server
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/client/view/LoginView.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 700));
        } catch (IOException e) { e.printStackTrace(); }
    }

    //  Navigation
    // null = thêm mới | có data = sửa
    private void goToProductForm(SellerProduct product) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/client/view/ProductFormView.fxml")
            );
            Parent root = loader.load();

            ProductFormController next = loader.getController();
            next.setProduct(product);        // truyền data sang form
            next.setSellerController(this);  // để form callback lại sau khi lưu

            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Callback — được gọi từ ProductFormController sau khi lưu thành công
    public void onProductSaved(SellerProduct product) {
        boolean exists = productList.stream()
            .anyMatch(p -> p.getId().equals(product.getId()));

        if (exists) {
            // Sửa — tìm đúng vị trí và thay thế
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getId().equals(product.getId())) {
                    productList.set(i, product);
                    break;
                }
            }
        } else {
            // Thêm mới — thêm vào đầu list
            productList.add(0, product);
        }

        allProducts = List.copyOf(productList);
        updateStatus();
    }

    // tiện ích
     private void updateStatus() {
        statusLabel.setText(productList.size() + " sản phẩm");
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
