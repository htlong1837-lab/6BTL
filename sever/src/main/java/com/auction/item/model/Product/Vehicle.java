package com.auction.item.model.Product;

public class Vehicle extends Item {
    private String make;
    private String model;
    private int year;
    public Vehicle(String id, String name, String des, double startPrice, String category, String sellerId, String make, String model, int year) {
        super(id, name, des, startPrice, category, sellerId);
        this.make = make;
        this.model = model;
        this.year = year;
    }

    //getter
    public String getMake() {
        return make;
    }
    public String getModel() {
        return model;
    }
    public int getYear() {
        return year;
    }

    //setter
    public void setMake(String make) {
        this.make = make;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public void setYear(int year) {
        this.year = year;
    }
    @Override
    public void printInfo() {
        super.printInfo(); 
        System.out.println("Make :" + make);
        System.out.println("Model :" + model);
        System.out.println("Year :" + year);
    }
}