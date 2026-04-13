
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
    // Duyệt Item hợp lệ
    public isApproved() {
        if (this.name != null && !this.name.isEmpty() &&
            this.description != null && !this.description.isEmpty() &&
            this.startPrice > 0) {
            return true;
            System.out.println("[Admin] Item \"" + this.name + "\" has been approved and is ready for auction.");
        }
        return false;
        System.out.println("[Admin] Item \"" + this.name + "\" is invalid and cannot be aution.Please check the details of the item and try again");
    }
    // Chỉnh sửa Item khi duyệt lỗi
    public void editItem(String name, String description, double startPrice) {
        this.name = name;
        this.description = description;
        this.startPrice = startPrice;
        System.out.println("[Admin] Item \"" + this.name + "\" has been updated. Please review it again for approval.");
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
