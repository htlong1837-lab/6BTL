

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class B1StackPaneLesson extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage){
        StackPane stack = new StackPane();

        // hinh chu nhat
        Rectangle rect = new Rectangle(200, 100);
        rect.setFill(Color.LAVENDER);

        // Label
        Label label = new Label("Hello StackPane");

        //Button
        Button button = new Button("click me");

        //add vo stack
        stack.getChildren().addAll(rect, label, button);
// Thứ tự xuất hiện của các vật là 
// rect ở dưới cùng
// label ở giữa
// button ở trên cùng 
// nếu ta thêm vật nữa ngay sau button thì nó đè ở trên
       
//Mặc dù mặc định của nó là ở centre nhưng ta có thể
// chỉnh bằng lệnh setAlignment
        StackPane.setAlignment(button, Pos.BASELINE_LEFT);
        StackPane.setMargin(label, new Insets(10,30,200,30));

// set Margin có tác dụng label chính là vật cần set
// new Insets (top,right,bottom,left) là các khoảng cách so với các cạnh
        Scene scene = new Scene(stack, 500, 400);

        stage.setScene(scene);
        stage.setTitle("StackPaneLesson");
        stage.show();
    }
}
