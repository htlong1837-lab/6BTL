
import java.util.Optional;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;

public class C1AlertLesson extends Application {
    public static void main(String[] args) {
        launch(args);
    } 
/* Các kiểu alert , có 5 loại:
// 1. INFORMATION — thông báo thông thường, nút OK
Alert info = new Alert(Alert.AlertType.INFORMATION);

// 2. WARNING — cảnh báo, nút OK
Alert warn = new Alert(Alert.AlertType.WARNING);

// 3. ERROR — thông báo lỗi, nút OK
Alert err = new Alert(Alert.AlertType.ERROR);

// 4. CONFIRMATION — xác nhận, nút OK + Cancel
Alert conf = new Alert(Alert.AlertType.CONFIRMATION);

// 5. NONE — không có icon, không có nút mặc định
Alert none = new Alert(Alert.AlertType.NONE);
 */

/* Các lệnh thiết lập nội dung
Alert alert = new Alert(Alert.AlertType.INFORMATION);

alert.setTitle("Tiêu đề cửa sổ");       // tiêu đề trên thanh titlebar
alert.setHeaderText("Tiêu đề nội dung"); // dòng chữ lớn bên trong
alert.setContentText("Nội dung chi tiết"); // mô tả bên dưới

// Ẩn headerText (chỉ hiện contentText)
alert.setHeaderText(null);

// Thay đổi kích thước
alert.getDialogPane().setPrefWidth(400);
alert.getDialogPane().setPrefHeight(200);
 */

/* Các button thông dụng
Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

// Tạo nút tùy chỉnh
ButtonType btnYes    = new ButtonType("Yes",    ButtonBar.ButtonData.YES);
ButtonType btnNo     = new ButtonType("No",     ButtonBar.ButtonData.NO);
ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

// Thay thế toàn bộ nút mặc định
alert.getButtonTypes().setAll(btnYes, btnNo, btnCancel);

// Thêm nút vào alert 
alert.getButtonTypes().add(new ButtonType("Thêm nút")); 
*/

/* Show() vs ShowAndWait()
// show() — KHÔNG block, code tiếp tục chạy ngay
alert.show();

// showAndWait() — BLOCK, chờ người dùng đóng dialog
Optional<ButtonType> result = alert.showAndWait();

 */
    public void start(Stage stage){
        
// bài toán hỏi xác nhận trước khi xóa

        Button button = new Button(" chào em anh đứng đây từ chiều");
            button.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận lại");
            alert.setHeaderText("Xóa dữ liệu");
            alert.setContentText("M nghĩ kĩ chưa");

            ButtonType btnDelete = new ButtonType("Xóa" , ButtonBar.ButtonData.OK_DONE);
            ButtonType btnCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE );

            alert.getButtonTypes().setAll(btnDelete,btnCancel);

            Optional<ButtonType> result = alert.showAndWait();
// Optional là lựa chọn
            if (result.get().getButtonData() == ButtonData.OK_DONE)  {
                System.out.println("Code for Okdone");
            }
            System.out.println("Code for cancel");
        });

        StackPane layout = new StackPane();
        layout.getChildren().add(button);
        Scene scene = new Scene(layout,300,400);
        stage.setScene(scene);
        stage.show();
    }
}
