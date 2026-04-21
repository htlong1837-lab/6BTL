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
    //duyệt hợp lệ
    @Override

    public boolean isApproved() {
        super.isApproved();
        if (make == null || make.isEmpty()) {
            return false;
        }
        if (model == null || model.isEmpty()) {
            return false;
        }
        if (year <= 0) {
            return false;
        }
        return true;
    }
    //chỉnh sửa khi duyệt lỗi
    public void editItemError(Vehicle item) {
        super.editItemError(item);
        if (item.make == null || item.make.isEmpty()) {
            this.make = item.make;
        }
        if (item.model == null || item.model.isEmpty()) {
            this.model = item.model;
        }
        if (item.year <= 0) {
            this.year = item.year;
        }
    }
    //Lập List
    public void listAllItems(List<Vehicle> items) {
        super.listAllItems(items);
    }
    //in list
    public void printListItems(List<Vehicle> items) {
        super.printListItems(items);
        for (Vehicle item : items) {
            System.out.println("Make  : " + item.make);
            System.out.println("Model : " + item.model);
            System.out.println("Year  : " + item.year);
        }
    }
    @Override
    public void printInfo() {
        super.printInfo(); 
        System.out.println("Make :" + make);
        System.out.println("Model :" + model);
        System.out.println("Year :" + year);
    }
}