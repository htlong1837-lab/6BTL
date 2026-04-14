public class Fashion extends Item {
    private String size;
    private String color;
    public Fashion(String id, String name, String des, double startPrice, String category, String sellerId, String size, String color) {
        super(id, name, des, startPrice, category, sellerId);
        this.size = size;
        this.color = color;
    }
    public String getSize() {   
        return size;
    }
    public String getColor() {
        return color;
    }
    public void setSize(String a) {
        this.size = a;
    }
    public void setColor(String b) {
        this.color = b;
    }
    @Override
    //In thông tin 
    public void printInfo() {   
        super.printInfo(); 
        System.out.println("Size :" + size);
        System.out.println("Color :" + color);
    }
    // Chỉnh sửa Item khi duyệt lỗi
    public void editItemError(Item item) {
        if (!item.isApproved()){
            super.editItemError(item);
            this.size = size;
            this.color = color;
        }
    }
    // Duyệt Item hợp lệ
    public boolean isApproved() {

        if (super.isApproved() && this.size != null && !this.size.isEmpty() && this.color != null && !this.color.isEmpty()) {
            System.out.println("[Admin] Item \"" + this.getName() + "\" has been approved and is ready for auction.");
            return true;
        } else if (this.size == null || this.size.isEmpty()) {
            System.out.println("[Admin] Item \"" + this.size + " of " + this.getName() + "\" is invalid and cannot be added to the auction. Please provide a valid size.");
        } else if (this.color == null || this.color.isEmpty()) {
            System.out.println("[Admin] Item \"" + this.color + " of " + this.getName() + "\" is invalid and cannot be added to the auction. Please provide a valid color.");
        }
        return false;
    }
}
