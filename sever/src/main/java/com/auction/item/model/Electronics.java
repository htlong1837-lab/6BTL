package com.auction.item.model;

public class Electronics extends Item {
    private String brand;
    private int warrantyMonths;
    public Electronics(String id, String name, String des, double startPrice, String category, String sellerId, String brand, int warrantyMonths) {
        super(id, name, des, startPrice, category, sellerId);
        this.brand = brand;
        this.warrantyMonths = warrantyMonths;
    }
    public String getBrand() {
        return brand;
    }
    public int getWarrantyMonths() {
        return warrantyMonths;

    }
    public void setBrand(String a) {
        this.brand = a;
    }
    public void setWarrantyMonths(int b) {
        this.warrantyMonths = b;
    }
    @Override
    public void printInfo() {
        super.printInfo(); 
        System.out.println("Brand :" + brand);
        System.out.println("WarrantyMonths :" + warrantyMonths);
    }
}