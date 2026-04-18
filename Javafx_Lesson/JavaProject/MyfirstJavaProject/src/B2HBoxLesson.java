
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class B2HBoxLesson extends Application {
    
/*[padding][Node1][spacing][Node2][spacing][Node3][padding]
                    6px             6px
                        
*/
    public void start(Stage stage){
        HBox toolBar = new HBox();
        toolBar.setSpacing(6); // khoảng cách giữa các node là 6
/*  setSpacing(double value):  Đây là khoảng cách giữa các node
không phải cạnh containter .
Spacing chỉ xuất hiện giữa các node, không thêm vào đầu hay cuối
*/ 

        toolBar.setPadding(new Insets(8,12,8,12));
/** Insets là khoảng trống bên trong border của Hbox
 * bao quanh toàn bộ nội dung*/
        
        toolBar.setAlignment(Pos.CENTER_RIGHT);
//setAlignment(Pos.Value)
// ảnh hưởng cả trục ngang và trục dọc đây là căn chỉnh tự động

        Button btnBack    = new Button("< Back");
        Button btnForward = new Button("Forward >");
        TextField urlBar  = new TextField("https://...");
        Button btnSearch  = new Button("Search");
        Button btnMenu    = new Button("Menu");

        HBox.setHgrow(urlBar, Priority.ALWAYS);

        toolBar.getChildren().addAll(btnBack,btnForward,urlBar,btnSearch,btnMenu);
        
        Scene scene = new Scene(toolBar,1000,2000);
        
        stage.setScene(scene);
        stage.show();
    }   

    public static void main(String[] args) {
        launch(args);
    }
}