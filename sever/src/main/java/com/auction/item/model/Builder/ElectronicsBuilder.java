package com.auction.item.model.Builder;

import com.auction.item.model.Product.Electronics;
import com.auction.item.model.Product.Item;

public class ElectronicsBuilder implements ItemBuilder {

    private String id;
    private String name;
    private String des;
    private double startPrice;
    private String category;
    private String sellerId;
    private String brand;
    private int warrantyMonths;
<<<<<<< Updated upstream

    @Override
    public ElectronicsBuilder setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public ElectronicsBuilder setName(String name) {
=======
    //setter
    public ItemBuilder setName(String name) {
>>>>>>> Stashed changes
        this.name = name;
        return this;
    }

    @Override
    public ElectronicsBuilder setDes(String des) {
        this.des = des;
        return this;
    }

    @Override
    public ElectronicsBuilder setStartPrice(double startPrice) {
        this.startPrice = startPrice;
        return this;
    }

    @Override
    public ElectronicsBuilder setCategory(String category) {
        this.category = category;
        return this;
    }

    @Override
    public ElectronicsBuilder setSellerId(String sellerId) {
        this.sellerId = sellerId;
        return this;
    }

    public ElectronicsBuilder setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public ElectronicsBuilder setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
        return this;
    }

    @Override
    public Item build() {
        return new Electronics(id, name, des, startPrice, category, sellerId, brand, warrantyMonths);
    }
}
