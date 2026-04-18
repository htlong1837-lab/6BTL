
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class B4GridPaneLesson extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
/*Nhóm 1: khoảng cách cơ bản

grid.setHgap(10);                     // khoảng cách ngang giữa các cột
grid.setVgap(10);                     // khoảng cách dọc giữa các hàng
grid.setPadding(new Insets(20));      // lề trong 4 cạnh
grid.setPadding(new Insets(10,20,10,20)); // top, right, bottom, left

grid.setAlignment(Pos.TOP_LEFT);      // vị trí grid trong container cha
grid.setGridLinesVisible(true);       // hiện đường lưới (dùng để debug) 
*/



/* Thêm node vào grid
// Cách 1: add(node, col, row) — chiếm đúng 1 ô
grid.add(label,    0, 0);   // cột 0, hàng 0
grid.add(textField,1, 0);   // cột 1, hàng 0
grid.add(button,   1, 4);   // cột 1, hàng 4

// Cách 2: add(node, col, row, colspan, rowspan) — chiếm nhiều ô
grid.add(header,   0, 0, 3, 1);  // chiếm 3 cột, 1 hàng
grid.add(sidebar,  0, 1, 1, 3);  // chiếm 1 cột, 3 hàng
grid.add(content,  1, 1, 2, 2);  // chiếm 2 cột, 2 hàng

 grid.add(header, 0, 0, 3, 1):     grid.add(sidebar, 0, 1, 1, 3):
┌──────────────────────┐           ┌──────┬──────┬──────┐
│       header         │           │      │      │      │
│  (col=0, span=3)     │           │  sb  │      │      │
└──────────────────────┘           │  sb  │      │      │
                                   │  sb  │      │      │
                                   └──────┴──────┴──────┘
*/


/* nhóm 2 :căn chỉnh từng node riêng lẻ
// Căn ngang node trong ô của nó
GridPane.setHalignment(button, HPos.CENTER);  // căn giữa ngang
GridPane.setHalignment(button, HPos.LEFT);    // căn trái
GridPane.setHalignment(button, HPos.RIGHT);   // căn phải

// Căn dọc node trong ô của nó
GridPane.setValignment(label, VPos.CENTER);   // căn giữa dọc
GridPane.setValignment(label, VPos.TOP);      // căn trên
GridPane.setValignment(label, VPos.BOTTOM);   // căn dưới
GridPane.setValignment(label, VPos.BASELINE); // căn theo baseline text

// Lề riêng từng node
GridPane.setMargin(button, new Insets(5, 0, 5, 0));
 
*/

/* nhóm 3 :ColumnConstraints (kiểm soát cột)Đây là nhóm lệnh quan trọng nhất để kiểm soát kích thước cột:
ColumnConstraints col0 = new ColumnConstraints();
col0.setMinWidth(80);          // chiều rộng tối thiểu
col0.setPrefWidth(120);        // chiều rộng ưu tiên
col0.setMaxWidth(200);         // chiều rộng tối đa
col0.setHgrow(Priority.NEVER); // không giãn khi có thêm chỗ
col0.setHalignment(HPos.LEFT); // căn ngang mặc định cho cả cột
col0.setPercentWidth(30);      // chiếm 30% chiều rộng GridPane

ColumnConstraints col1 = new ColumnConstraints();
col1.setHgrow(Priority.ALWAYS);  // cột này giãn ra chiếm phần còn lại
col1.setPercentWidth(70);        // hoặc chiếm 70%

// Thêm vào grid theo thứ tự cột 0, 1, 2...
grid.getColumnConstraints().addAll(col0, col1);
 */

/* Nhóm 4 : RowConstraints kiểm soát Hàng
RowConstraints row0 = new RowConstraints();
row0.setMinHeight(30);Chiều cao tối thiểu
row0.setPrefHeight(40); // Chiều cao ưu tiên
row0.setMaxHeight(60);// chiều cao tối đa
row0.setVgrow(Priority.NEVER);
row0.setValignment(VPos.CENTER);
row0.setPercentHeight(20);       // chiếm 20% chiều cao GridPane
row0.setFillHeight(true);        // node con giãn đầy ô theo chiều dọc

RowConstraints rowGrow = new RowConstraints();
rowGrow.setVgrow(Priority.ALWAYS); // hàng này giãn chiếm phần dư
grid.getRowConstraints().addAll(row0, rowGrow);
*/

// Ví dụ cụ thể
        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setPadding(new Insets(20));
        form.setAlignment(Pos.TOP_CENTER);

// Cột 0: label cố định 100px , cột 1 : field giãn ra
        ColumnConstraints clabel = new ColumnConstraints();
        ColumnConstraints cfield = new ColumnConstraints();
        cfield.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(clabel,cfield);

// tạo node
        Label lblName = new Label("Họ tên");
        TextField tfName = new TextField();
        Label lblEmail = new Label("Email:");
        TextField tfEmail = new TextField();

        Label lblNote  = new Label("Ghi chú:");
        TextArea taNote = new TextArea();
        taNote.setPrefRowCount(3);

        Button btnSave = new Button("Lưu");
// thêm vô grid 
        form.add(lblName,  0, 0);
        form.add(tfName,   1, 0);
        form.add(lblEmail, 0, 1);
        form.add(tfEmail,  1, 1);
        form.add(lblNote,  0, 2);
        form.add(taNote,   1, 2);
// btnSave chiếm cả 2 cột, căn phải
        form.add(btnSave,  0, 3, 2, 1);  // colspan=2
        GridPane.setHalignment(btnSave, HPos.RIGHT);
        GridPane.setMargin(btnSave, new Insets(8, 0, 0, 0));

        TextArea ta = new TextArea();
        ta.setPrefColumnCount(40);  // rộng ~40 ký tự
        ta.setPrefRowCount(5);      // cao 5 hàng
        ta.setWrapText(true);       // tự xuống dòng khi đầy
        form.add(ta, 0,4);
        
    Scene scene = new Scene(form);
    stage.setScene(scene);
    stage.show();
    }

    
}
