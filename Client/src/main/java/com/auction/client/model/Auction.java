package com.auction.client.model;
// để test sau kết nối sang sever sau vì del biết kết nối kiểu gì

public class Auction {
    private String id;
    private String itemName;
    private double currentPrice;
    private String status;
    private long endTime;
    private String sellerName;

    public Auction(String id, String itemName ,double currentPrice , long endTime , String sellerName) {
        this.id = id;
        this.itemName = itemName;
        this.currentPrice = currentPrice;
        this.endTime = endTime;
        this.sellerName = sellerName;
        this.status = "OPEN";
    }
// gettter thông thường


    public String getId() {return id ;}
    public String getItemName() {return itemName;}
    public double getCurrentPrice() {return currentPrice;}
    public String getStatus() {return status;}
    public long getEndTime() { return endTime;}
    public String getSellerName() {return sellerName;}

    public void setCurrentPrice(double p) { this.currentPrice = p ;}
    public void setStatus(String s) { this.status = s;}
}
