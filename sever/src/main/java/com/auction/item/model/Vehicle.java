package com.auction.item.model;

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
    public String getMake() {
        return make;
    }
    public String getModel() {
        return model;

    }
    public int getYear() {
        return year;
    }
    public void setMake(String a) {
        this.make = a;
    }
    public void setModel(String b) {
        this.model = b;
    }
    public void setYear(int c) {
        this.year = c;
    }
    @Override
    public void printInfo() {
        super.printInfo(); 
        System.out.println("Make :" + make);
        System.out.println("Model :" + model);
        System.out.println("Year :" + year);
    }
}