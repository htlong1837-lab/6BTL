package com.auction.controller;

import com.auction.client.model.UserItem;
import com.auction.client.model.FakeDataHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminController {

    // -- FXML binding ------------------
    @FXML private Label totalProductsLabel;
    @FXML private Label totalBidsLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalAuctionsLabel;

    @FXML private TableView<UserItem>           userTable;
    @FXML private TableColumn<UserItem, String> colUserId;
    @FXML private TableColumn<UserItem, String> colUsername;
    @FXML private TableColumn<UserItem, String> colEmail;
    @FXML private TableColumn<UserItem, String> colRole;
    @FXML private TableColumn<UserItem, String> colUserStatus;

    @FXML private TextField searchUserField;
    @FXML private Label     userStatusLabel;

    // -- State ------------------------------------
    private ObservableList<UserItem> userList =
        FXCollections.observableArrayList();
    private List<UserItem> allUsers;

    // -- Khởi tạo ------------------------------ 
    
    @FXML
    public void initialize() {
        setupColumns();
        loadFakeData(); // TODO: thay bằng loadFromServer()
    }

    private void setupColumns() {
        colUserId    .setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername  .setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail     .setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole      .setCellValueFactory(new PropertyValueFactory<>("role"));
        colUserStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Đổi màu dòng theo trạng thái
        // BANNED → đỏ nhạt | ACTIVE → bình thường
        userTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(UserItem user, boolean empty) {
                super.updateItem(user, empty);
                if (user == null || empty) {
                    setStyle("");
                } else if ("BANNED".equals(user.getStatus())) {
                    setStyle("-fx-background-color: #FFEBEE;");
                } else {
                    setStyle("");
                }
            }
        });

        userTable.setItems(userList);
    }

    private void loadFakeData() {
        // TODO: xóa, thay bằng gọi NetworkService
        userList.setAll(FakeDataHelper.makeUsers());
        allUsers = List.copyOf(userList);
        updateStats();
    }

    // -- Sự kiện --------------------------------
    @FXML
    private void handleSearchUser() {
        String kw = searchUserField.getText().trim().toLowerCase();

        // Filter theo cả username lẫn email
        List<UserItem> result = kw.isEmpty()
            ? allUsers
            : allUsers.stream()
                .filter(u -> u.getUsername().toLowerCase().contains(kw)
                          || u.getEmail()   .toLowerCase().contains(kw))
                .collect(Collectors.toList());

        userList.setAll(result);
        userStatusLabel.setText(result.size() + " người dùng");
    }

    @FXML
    private void handleBan() {
        UserItem selected = userTable
            .getSelectionModel().getSelectedItem();

        // Validate 1 — phải chọn user trước
        if (selected == null) {
            showAlert("Chưa chọn user",
                "Vui lòng chọn người dùng muốn ban!");
            return;
        }

        // Validate 2 — không ban người đã bị ban
        if ("BANNED".equals(selected.getStatus())) {
            showAlert("Đã bị ban",
                selected.getUsername() + " đã bị ban rồi!");
            return;
        }

        // Xác nhận trước khi ban
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận ban");
        confirm.setHeaderText("Ban: " + selected.getUsername());
        confirm.setContentText(
            "Người dùng sẽ không thể đăng nhập được nữa!"
        );

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            selected.setStatus("BANNED");
            userTable.refresh(); // ép TableView vẽ lại để đổi màu dòng
            updateStats();
            // TODO: gửi BAN request lên server
        }
    }

    @FXML
    private void handleUnban() {
        UserItem selected = userTable
            .getSelectionModel().getSelectedItem();

        // Validate 1 — phải chọn user trước
        if (selected == null) {
            showAlert("Chưa chọn user",
                "Vui lòng chọn người dùng muốn unban!");
            return;
        }

        // Validate 2 — không unban người đang active
        if ("ACTIVE".equals(selected.getStatus())) {
            showAlert("Đang hoạt động",
                selected.getUsername() + " đang hoạt động bình thường!");
            return;
        }

        selected.setStatus("ACTIVE");
        userTable.refresh(); // ép TableView vẽ lại để xóa màu đỏ
        updateStats();
        // TODO: gửi UNBAN request lên server
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/client/view/LoginView.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) searchUserField.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 700));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // -- Helper ------------------------------
    private void updateStats() {
        long activeCount = userList.stream()
            .filter(u -> "ACTIVE".equals(u.getStatus()))
            .count();
        long bannedCount = userList.stream()
            .filter(u -> "BANNED".equals(u.getStatus()))
            .count();

        totalUsersLabel   .setText(String.valueOf(userList.size()));
        totalProductsLabel.setText("9");  // TODO: lấy từ server
        totalBidsLabel    .setText("23"); // TODO: lấy từ server
        totalAuctionsLabel.setText(String.valueOf(activeCount));

        userStatusLabel.setText(
            userList.size() + " người dùng"
            + "  (" + bannedCount + " bị ban)"
        );
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
