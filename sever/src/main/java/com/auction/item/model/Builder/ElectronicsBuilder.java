package com.auction.item.model.Builder;

import com.auction.item.model.Product.Item;

public class ElectronicsBuilder implements ItemBuilder{
    private Electronics item = new Electronics();
    private String name;
    private String des;
    private double startPrice;
    private String brand;
    private int warrantyMonths;

    public ItemBuilder setName(String name) {
        this.name = name;
        return this;
    }
    public ItemBuilder setDes(String des) {
        this.des = des;
        return this;
    }

    public ItemBuilder setStartPrice(double startPrice) {
        this.startPrice = startPrice;
        return this;
    }

    public ItemBuilder setBrand(String a) {
        this.brand = a;
        return this;
    }
    
    public ItemBuilder setWarrantyMonths(int b) {
        this.warrantyMonths = b;
        return this;
    }

    public Item build(){
        return item;
    }


}