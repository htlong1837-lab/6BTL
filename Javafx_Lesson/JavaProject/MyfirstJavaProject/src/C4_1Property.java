// Đây là 1 tính năng của property : Tính năng lắng nghe
public class C4_1Property {
    public static void main(String[] args) {
        C4_0MyNumber example = new C4_0MyNumber();
        example.numberProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(observable);
            System.out.println(oldValue);
            System.out.println(newValue);
        });
        example.setNumber(10);
        example.setNumber(11);
    }
}
 
