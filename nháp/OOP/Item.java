
// file item cũ kh dùng đc theo oop

public abstract class Item extends Entity {
    protected String name;
    protected String description;
    protected double startPrice;
 
    public Item(String id, String name, String description, double startPrice) {
        super(id);
        this.name = name;
        this.description = description;
        this.startPrice = startPrice;
    }
 
    // Getter
    public String getName()        { return name; }
    public String getDescription() { return description; }
    public double getStartPrice()  { return startPrice; }
 
    // Setter
    public void setName(String name)               { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setStartPrice(double startPrice)   { this.startPrice = startPrice; }
 
    /** Mỗi loại item override để in thêm thông tin riêng */
    @Override
    public abstract void printInfo();
}
