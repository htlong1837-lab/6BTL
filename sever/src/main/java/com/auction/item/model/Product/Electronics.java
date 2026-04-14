package com.auction.item.model.Product;

import java.lang.module.ModuleDescriptor.Builder;

class Electronics extends Item {
    private String brand;
    private int warrantyMonths;
    public Electronics(Builder builder) {
        super(builder.id, builder.name, builder.des, builder.startPrice, builder.category, builder.sellerId);
        this.brand = builder.brand;
        this.warrantyMonths = builder.warrantyMonths;
    }

    //getter
    public String getBrand() {
        return brand;
    }
    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    //setter
    void setBrand(String a) {
            this.brand = a;
    }
        
    void setWarrantyMonths(int b) {
        this.warrantyMonths = b;
    }

    
    @Override
    public void printInfo() {
        super.printInfo(); 
        System.out.println("Brand :" + brand);
        System.out.println("WarrantyMonths :" + warrantyMonths);
    }
}