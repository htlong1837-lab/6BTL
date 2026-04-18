
import javafx.scene.control.Label;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// giống với Hbox có thêm mỗi setFillWidth là khác
public class B3VBoxLesson extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage){
// Form: Label + TextField + Label + PasswordField + Button
        VBox form = new VBox();
        form.setSpacing(12);// giống Hbox nhưng theo chiều dọc
        form.setPadding(new Insets(24));
        form.setAlignment(Pos.TOP_CENTER);
        form.setFillWidth(true);       // tất cả field rộng bằng nhau
// Đây là điểm khác biệt lớn nhất so với Hbox .
// vì Vbox xếp dọc nên mỗi node có thể rộng hẹp khác nhau
// setFillWidth(boolean) sẽ quyết định các node có giãn đầy
// chiều ngang của VBox không
        Label lblUser  = new Label("Tên đăng nhập");
        TextField tfUser = new TextField();

        Label lblPass  = new Label("Mật khẩu");
        PasswordField pfPass = new PasswordField();

        Button btnLogin = new Button("Đăng nhập");
        VBox.setMargin(btnLogin, new Insets(8, 0, 0, 0)); // thêm khoảng trên nút
        form.getChildren().addAll(lblUser, tfUser, lblPass, pfPass, btnLogin);
    
        Scene scene = new Scene(form,1000,1000);
        stage.setScene(scene);
        stage.show();
    }

    
}
