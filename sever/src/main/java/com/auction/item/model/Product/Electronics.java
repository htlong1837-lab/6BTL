package com.auction.item.model.Product;
import java.util.List;
public class Electronics extends Item {
    private String brand;
    private int warrantyMonths;

    public Electronics(String id, String name, String des, double startPrice,
                       String category, String sellerId, String brand, int warrantyMonths) {
        super(id, name, des, startPrice, category, sellerId);
        this.brand = brand;
        this.warrantyMonths = warrantyMonths;
    }

    public String getBrand() { return brand; }
    public int getWarrantyMonths() { return warrantyMonths; }

    public void setBrand(String brand) { this.brand = brand; }
    public void setWarrantyMonths(int warrantyMonths) { this.warrantyMonths = warrantyMonths; }
    //duyệt hợp lệ
    @Override
    public boolean isApproved() {
        super.isApproved();
        if (brand == null || brand.isEmpty()) {
            return false;
        }
        if (warrantyMonths <= 0) {
            return false;
        }
        return true;
    }
    //chỉnh sửa khi duyệt lỗi
  
    public void editItemError(Electronics item) {
        super.editItemError(item);
        if (item.brand == null || item.brand.isEmpty()) {
            this.brand = item.brand;
        }
        if (item.warrantyMonths <= 0) {
            this.warrantyMonths = item.warrantyMonths;
        }
    }
 
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Brand    : " + brand);
        System.out.println("Warranty : " + warrantyMonths + " months");
    }
}
