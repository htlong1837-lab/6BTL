import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
// cũng là 1 tính năng của porperty : tính ràng buộc :binding
public class C4_2Binding {
    public static void main(String[] args) {
        IntegerProperty a = new SimpleIntegerProperty(4);
        IntegerProperty b = new SimpleIntegerProperty();

        System.out.println(b.getValue()); // trước ràng buộc b = 0 vì chưa in ra

        b.bind(a); // khi này b sẽ ràng buộc bằng a khi ta in ra thì ta nhận dc b bằng 4
        System.out.println(b.getValue());

        a.setValue(12);
        System.out.println(b.getValue()); // khi này giá trị in ra sẽ là 12
        
    }
}

