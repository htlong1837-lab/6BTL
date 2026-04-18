
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class C3CheckBoxLesson extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage){
        Label label = new Label("Programming Languege");
        CheckBox cb1 = new CheckBox("Python");
        CheckBox cb2 = new CheckBox("Java");
        CheckBox cb3 = new CheckBox("C++");

        Button button = new Button("Submit");
        cb1.setSelected(true);

        button.setOnAction(e -> {
            String message = "Ngôn ngữ bạn dùng" ;
            if (cb1.isSelected()){
                message += cb1.getText();
            }
            if (cb2.isSelected()) {
                message += cb2.getText();
            }
            if(cb3.isSelected()) {
                message += cb3.getText();
            }
// kiểm tra xem cái nào được tích true sẽ trả về
            System.out.println(message); 
        });

        HBox layoutH = new HBox(10);
        layoutH.getChildren().addAll(cb1,cb2,cb3);

        VBox layoutV = new VBox(10);
        layoutV.getChildren().addAll(label,layoutH,button);

        Scene scene = new Scene(layoutV,400,300);
        stage.setScene(scene);
        stage.show();
    }
}
