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

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Brand    : " + brand);
        System.out.println("Warranty : " + warrantyMonths + " months");
    }
}
