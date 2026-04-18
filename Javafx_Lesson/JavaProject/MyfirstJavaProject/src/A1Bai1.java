



import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class A1Bai1 extends Application  {
    Button button;
    Button button1;
    Button button2;
    public static void main(String[] args) {
        launch(args);
/**  hàm main sẽ chạy launch và truyền args và chạy App 
 * Khi đó App được kế thừa Application và chạy start
 * mọi code sẽ chạy trong hàm start để thực thi
*/
    }

    @Override
    public void start(Stage primaryStage) {
/** Tham số truyền vào là stage: sân khấu , một màn trình diễn
 * stage có các cutscene , console , họa tiết
 */
        primaryStage.setTitle("Hello World");
// để tiêu đề là hello world
        button = new Button();
        button1 = new Button();
        button2 = new Button();
// tạo nút để bấm
        button.setText("Hãy bấm t đi");
        button1.setText("Tao buồn ngủ");
        button2.setText("Tao muốn đi chơi");
// trên nút đó ghi dòng chữ
        button.setOnAction(new EventHandler<ActionEvent>() { // anonymous class
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello NamChimCam"); // hàm handle sẽ sử lí button
            }
        });
// trong trường hợp nó là button1 vì button2 stack ở trên nên lúc bấm nó sẽ ko trả về gì hết vì ta
// ko set action nào cho button 2 cả
        button2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Baibai NamChimCam"); // hàm handle sẽ sử lí button1
            }
        });
// tạo 1 cái phông và cho nút vào đó
        StackPane layout = new StackPane();
        StackPane layout1 = new StackPane();
        layout.getChildren().add(button);
        layout1.getChildren().addAll(button1,button2);
    //khi này button2 sẽ stack trên button1 nên buttton 2 được in ra 
        Scene scene1 = new Scene(layout1 , 200,300);
        Scene scene = new Scene(layout, 400, 250);

/** //đưa scene vào trong stage
 
        primaryStage.setScene(scene);
        primaryStage.setScene(scene1);// khi chạy scene 1 sẽ đè lên scene 2
// phô diễn stage ra màn hình
        primaryStage.show();  
  
        */

// khi đó ta sẽ dùng tới Class Stage
        Stage stage1 = new Stage();
        stage1.setScene(scene);
        stage1.show();

        Stage stage2 = new Stage();
        stage2.setScene(scene1);
        stage2.show();
    }
// lúc này hàm sẽ trả về 2 màn hình cùng lúc
}