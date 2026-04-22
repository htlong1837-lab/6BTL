package com.auction.item.model.Builder;

import com.auction.item.model.Product.Item;
import com.auction.item.model.Product.Vehicle;

public class VehicleBuilder implements ItemBuilder {

    private String id;
    private String name;
    private String des;
    private double startPrice;
    private String category;
    private String sellerId;
    private String make;
    private String model;
    private int year;

    @Override
    public VehicleBuilder setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public VehicleBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public VehicleBuilder setDes(String des) {
        this.des = des;
        return this;
    }

    @Override
    public VehicleBuilder setStartPrice(double startPrice) {
        this.startPrice = startPrice;
        return this;
    }

    @Override
    public VehicleBuilder setCategory(String category) {
        this.category = category;
        return this;
    }

    @Override
    public VehicleBuilder setSellerId(String sellerId) {
        this.sellerId = sellerId;
        return this;
    }

    public VehicleBuilder setMake(String make) {
        this.make = make;
        return this;
    }
    public VehicleBuilder setModel(String model) {
        this.model = model;
        return this;
    }
    public VehicleBuilder setYear(int year) {
        this.year = year;
        return this;
    }

    @Override
    public Item build() {
        return new Vehicle(id, name, des, startPrice, category, sellerId, make, model, year);
    }
}