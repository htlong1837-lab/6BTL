package com.auction.item.model.Product;

import java.lang.module.ModuleDescriptor.Builder;

class Electronics extends Item {
    private String brand;
    private int warrantyMonths;
    public Electronics(Builder builder) {
        super(builder.id, builder.name, builder.des, builder.startPrice, builder.category, builder.sellerId);
        this.brand = builder.brand;
        this.warrantyMonths = builder.warrantyMonths;
    }s

<<<<<<< HEAD
    //getter
=======

>>>>>>> 9667fe2d5aa0b49693bd10022a7d11ca678ffc1f
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

    
  

}