import javafx.beans.property.*;
public class C4_0MyNumber {
    private DoubleProperty number = new SimpleDoubleProperty();

    public double getNumber(){
        return number.get();
    }

    public DoubleProperty numberProperty() {
        return number;
    }
    public void setNumber(double number){
        this.number.set(number);
    }
}
/*
Property<T>               ← interface gốc
    ├── StringProperty    ← lưu String
    ├── IntegerProperty   ← lưu int
    ├── DoubleProperty    ← lưu double
    ├── BooleanProperty   ← lưu boolean
    ├── LongProperty      ← lưu long
    ├── FloatProperty     ← lưu float
    └── ObjectProperty<T> ← lưu bất kỳ object nào */