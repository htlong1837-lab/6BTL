package com.auction.item.model.Product;
import com.auction.common.model.Entity;
import java.util.ArrayList;
import java.util.List;
public abstract class Item extends Entity {
    protected String name;
    protected String des;
    protected double startPrice;
    protected String category;
    protected String sellerId;
    public Item(String id, String name, String des, double startPrice, String category, String sellerId) {
        super(id);
        this.name = name;
        this.des = des;
        this.startPrice = startPrice;
        this.category = category;
        this.sellerId = sellerId;
    }

    //getter
    public String getName() {
        return name;
    }
    public String getDes() {
        return des;

    }
    public double getStartPrice() {
        return startPrice;
    }
    public String getCategory() {
        return category;
    }
    public String getSellerId() {
        return sellerId;
    }

    //setter
    public void setName(String name) {
        this.name = name;
    }
    public void setDes(String des) {
        this.des = des;
    }
    
    public void setStartPrice(double startPrice) {
        this.startPrice = startPrice;
    }
        // Duyệt Item hợp lệ
    public boolean isApproved() {
        if (this.name != null && !this.name.isEmpty() &&
            this.des != null && !this.des.isEmpty() &&
            this.startPrice > 0 && this.category != null && !this.category.isEmpty() && this.sellerId != null && !this.sellerId.isEmpty()) {
            System.out.println("[Admin] Item \"" + this.name + "\" has been approved and is ready for auction.");
            return true;
        } else {
             System.out.println("[Admin] Item \"" + this.name + "\" is invalid and cannot be aution.Please check the details of the item and try again");
             return false;
        }
    }
    // Chỉnh sửa Item khi duyệt lỗi
    public void editItem(Item item) {
        if (!item.isApproved()){
            this.name = name;
            this.des = des;
            this.startPrice = startPrice;
            this.category = category;
            this.sellerId = sellerId;
            System.out.println("[Admin] Item \"" + this.name + "\" has been updated. Please review it again for approval.");

        }  
    }
    //Lập List Item để Admin Thêm/sửa/xóa
    public void listAllItems(List<Item> items) {
        if (this.isApproved()){
            items.add(this);
        }   
    }
    //In ra List
    public void printListAllItems(List<Item> items) {
        System.out.println("[Admin] All items (" + items.size() + "):");
        for (Item i : items) {
            System.out.println("  - " + i.getName()
                    + "  Description: " + i.getDes()
                    + "  Start price: " + i.getStartPrice()
                    + "  Category: " + i.getCategory()
                    + "  Seller ID: " + i.getSellerId());
        }
    }
    @Override
    public void printInfo() {
        System.out.println("Sản phẩm:" + name + "mô tả:" + des + "có giá khởi điểm" + startPrice);
    }


    
}
