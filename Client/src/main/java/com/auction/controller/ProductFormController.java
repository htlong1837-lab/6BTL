package com.auction.controller;

import com.auction.client.model.SellerProduct;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.UUID;

public class ProductFormController {

    @FXML private Label     titleLabel;
    @FXML private TextField nameField;
    @FXML private TextArea  descField;
    @FXML private TextField priceField;
    @FXML private TextField durationField;
    @FXML private Label     messageLabel;
    @FXML private Button    saveButton;

    private SellerProduct    editingProduct;
    private SellerController sellerController;

    public void setProduct(SellerProduct product) {
        this.editingProduct = product;
        if (product == null) {
            titleLabel.setText("Thêm sản phẩm mới");
            saveButton.setText("Thêm");
        } else {
            titleLabel.setText("Sửa sản phẩm");
            saveButton.setText("Cập nhật");
            nameField .setText(product.getName());
            descField .setText(product.getDescription());
            priceField.setText(String.valueOf((long) product.getStartPrice()));

            long remaining = product.getEndTime() - System.currentTimeMillis();
            long hours     = Math.max(1, remaining / 3_600_000);
            durationField.setText(String.valueOf(hours));
        }
    }

    public void setSellerController(SellerController controller) {
        this.sellerController = controller;
    }

    @FXML
    private void handleSave() {
        String name     = nameField    .getText().trim();
        String desc     = descField    .getText().trim();
        String priceStr = priceField   .getText().trim();
        String hourStr  = durationField.getText().trim();

        if (name.isEmpty()) {
            showMessage("Vui lòng nhập tên sản phẩm!", "red"); return;
        }
        if (priceStr.isEmpty()) {
            showMessage("Vui lòng nhập giá khởi điểm!", "red"); return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr.replace(",", ""));
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showMessage("Giá khởi điểm không hợp lệ!", "red"); return;
        }

        if (hourStr.isEmpty()) {
            showMessage("Vui lòng nhập thời gian đấu giá!", "red"); return;
        }

        long hours;
        try {
            hours = Long.parseLong(hourStr);
            if (hours <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showMessage("Thời gian phải là số nguyên dương!", "red"); return;
        }

        long endTime = System.currentTimeMillis() + hours * 3_600_000L;
        SellerProduct product;

        if (editingProduct == null) {
            product = new SellerProduct(UUID.randomUUID().toString(), name, desc, price, endTime);
        } else {
            editingProduct.setName(name);
            editingProduct.setDescription(desc);
            editingProduct.setStartPrice(price);
            editingProduct.setEndTime(endTime);
            product = editingProduct;
        }

        if (sellerController != null) {
            sellerController.onProductSaved(product);
        }

        showMessage("Lưu thành công!", "green");

        new Thread(() -> {
            try { Thread.sleep(800); } catch (Exception ignored) {}
            javafx.application.Platform.runLater(this::goBack);
        }).start();
    }

    @FXML
    private void handleCancel() {
        goBack();
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/client/view/SellerView.fxml")
            );
            Parent root  = loader.load();
            Stage  stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void showMessage(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}
