package com.auction.item.model.Product;

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
    @Override
    public void editItemError(Electronics item) {
        super.editItemError(item);
        if (item.brand == null || item.brand.isEmpty()) {
            this.brand = item.brand;
        }
        if (item.warrantyMonths <= 0) {
            this.warrantyMonths = item.warrantyMonths;
        }
    }
    //Lập List
    @Override
    public void listAllItems(List<Electronics> items) {
        super.listAllItems(items);
    }
    //in list
    @Override
    public void printListItems(List<Electronics> items) {
        super.printListItems(items);
        for (Electronics item : items) {
            System.out.println("Brand    : " + item.brand);
            System.out.println("Warranty : " + item.warrantyMonths + " months");
        }
    }
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Brand    : " + brand);
        System.out.println("Warranty : " + warrantyMonths + " months");
    }
}
