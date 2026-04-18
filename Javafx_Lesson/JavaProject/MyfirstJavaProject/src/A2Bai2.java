

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class A2Bai2 extends Application{
    Stage window;
    Scene scene1;
    Scene scene2;
    public static void main(String[] args) {
        launch(args);
    }
// muốn chuyển đổi giữa màn hình một và màn hình 2, ta làm như thế nào?
    public void start(Stage primaryStage) {
    //scene1
        window = primaryStage;
        Label label = new Label("Xin chào trở lại");
        Button button1 = new Button("Đăng nhập");
        VBox layout1 = new VBox(); // học sau
        layout1.getChildren().addAll(label,button1);
        scene1= new Scene(layout1, 500,400);

// lệnh setOnAction có nhiệm vụ tạo sự kiện, nhiệm vụ cho button
// có 2 kiểu gọi 
        button1.setOnAction(new EventHandler<ActionEvent>(){
            // gọi thông qua anonymous class
            @Override
            public void handle(ActionEvent event){
                window.setScene(scene2);
            }
        });
    //scene2
        Button button2 = new Button("Trở lại");
        StackPane layout2 = new StackPane();
        layout2.getChildren().add(button2);
        scene2 = new Scene(layout2,500,600);

        button2.setOnAction(event -> {// và gọi kiểu lamda expression
                window.setScene(scene1);
            });

        window.setScene(scene1);
        window.show();
    }
// vì AuctionEvent có mỗi một hàm handle nên ta có thể dùng lamda expression để
// override lại hàm handle()
}
