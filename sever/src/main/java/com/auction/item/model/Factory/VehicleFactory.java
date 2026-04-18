package com.auction.item.model.Factory;

import com.auction.item.model.Builder.VehicleBuilder;
import com.auction.item.model.Product.Item;

public class VehicleFactory implements ItemFactory {
    private String id, name, des, category, sellerId, make, model;
    private double startPrice;
    private int year;

    public VehicleFactory(String id, String name, String des, double startPrice, String category, String sellerId, String make, String model, int year) {
        this.id = id;
        this.name = name;
        this.des = des;
        this.startPrice = startPrice;
        this.category = category;
        this.sellerId = sellerId;
        this.make = make;
        this.model = model;
        this.year = year;
    }

    @Override
    public Item createItem() {
        return new VehicleBuilder()
            .setId(id)
            .setName(name)
            .setDes(des)
            .setStartPrice(startPrice)
            .setCategory(category)
            .setSellerId(sellerId)
            .setMake(make)
            .setModel(model)
            .setYear(year)
            .build();
    }
}
