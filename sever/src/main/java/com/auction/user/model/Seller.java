package com.auction.user.model;

public class Seller extends User {
    private String shopName;
    private double revenue;
    public Seller(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);
        this.shopName = name + "'s shop";
        this.revenue = 0.0;
    }
    public String getShopName() {
        return shopName;
    }
    public double getRevenue() {
        return revenue;
    }
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public void addRevenue(double amount) {
        this.revenue += amount;
    }
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("shop :" + shopName);
        System.out.println("có doanh thu :" + revenue +"VND");
    }

    
}
