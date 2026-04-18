
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
/* Dialog là lớp cha của Alert
Alert là một nhánh con của Dialog<R>
Alert (= Dialog<ButtonType>) -> kế thừa + icon/nút
TextInputDialog (=Dialof<String>) -> kế thừa trên + thêm TextField
ChoiceDialog<T> (=Dialog<T>) -> Thêm ComboBox

// Điểm khác biệt giữa Dialog<R> và 3 lớp con là setResultConverter
// 3 lớp con setResultCOnverter đã được gọi bên trong constructor

// Còn dialog<R> thuần phải tự gọi Dialog<R>
// nếu ko gọi, showandwait() luôn trả về Optional.empty()
// đây là cách để chuyển đổi dữ liệu đầu vào 
dialog.setResultConverter(btn -> {
    if (btn == okBtn) return myData;
    return null;
});
*/
public class C2DialogLesson extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage){
        Dialog<Pair<String,String>> dialog = new Dialog<>();
        dialog.setTitle("Login dialog");
        dialog.setHeaderText("Sign Up");
/*// Thứ tự từ trái → phải trên ButtonBar (Windows)
ButtonBar.ButtonData.BACK_PREVIOUS   // [< Quay lại]
ButtonBar.ButtonData.NEXT_FORWARD    // [Tiếp theo >]
ButtonBar.ButtonData.FINISH          // [Hoàn tất]
ButtonBar.ButtonData.OK_DONE         // [OK]
ButtonBar.ButtonData.YES             // [Yes]
ButtonBar.ButtonData.NO              // [No]
ButtonBar.ButtonData.CANCEL_CLOSE    // [Hủy]
ButtonBar.ButtonData.APPLY           // [Áp dụng]
ButtonBar.ButtonData.OTHER           // [Khác] (vị trí tùy)
ButtonBar.ButtonData.HELP            // [?] (ngoài cùng phải)
*/
        ButtonType loginButtonType = new ButtonType("Login",ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
    
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(12,12,150,20));
    
       TextField userName = new TextField();  // đứa này là lớp con kế thừa từ lớp abtract TextInputControl
       userName.setPromptText("Tên đăng nhập");
       PasswordField password = new PasswordField();// kế thừa từ lớp TextField
       password.setPromptText("Mật khẩu");
// còn 1 thằng nữa cũng kế thừa từ textInputControl là TextArea là nhập nhiều dòng , tưởng tượng như form đăng kí thông tin chẳng hạn
       grid.add(new Label("Username:"),0,0);
       grid.add(userName,1,0);
       grid.add(new Label("Password"),0,1);
       grid.add(password,1,1);

       Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
       loginButton.setDisable(true);

       userName.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
 // textProperty là đối tượng theo dõi text bài sau học
 // addListener Đăng kí lắng nhe
 // observable là tham số nguồn được chuyền vào của username nó sẽ so sánh với oldvalue và newValue
 //vd : người dùng nhập "a" observable nhận a và nó so sánh vs oldValue đang là null -> push vào newValue ="a"
 //     tiếp dục nhập "b" observale nhận b và nó so sánh với oldvalue là "a" -> push vào newvalue ="ab"

 // 3 thằng này nên đi với nhau để nhập dữ liệu đầu vào ddeerr dễ dàng chỉnh sửa và xóa cho user
        });
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType){
                return new Pair<String,String>(userName.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(userNamepassword -> {
            System.out.println("Username="+userNamepassword.getKey()+", Password="+userNamepassword.getValue());
        });
/* if (result.getButtonData( ) == ButtonData.OK_DONE){
        System.out.println("Username="+userNamepassword.getKey()+", Password="+userNamepassword.getValue());
        });
    }
    reuturn null; // nếu user cancel thì return null;
 */

     
    }
}
