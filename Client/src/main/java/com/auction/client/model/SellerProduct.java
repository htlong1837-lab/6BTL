package com.auction.client.model;

public class SellerProduct {
    private String id;
    private String name;
    private String description;
    private double startPrice;
    private double currentPrice;
    private String status;
    private String topBidder;
    private long   endTime;

    public SellerProduct(String id, String name, String description,
                         double startPrice, long endTime) {
        this.id           = id;
        this.name         = name;
        this.description  = description;
        this.startPrice   = startPrice;
        this.currentPrice = startPrice;
        this.endTime      = endTime;
        this.status       = "OPEN";
        this.topBidder    = "(chưa có)";
    }

    public String getId()           { return id; }
    public String getName()         { return name; }
    public String getDescription()  { return description; }
    public double getStartPrice()   { return startPrice; }
    public double getCurrentPrice() { return currentPrice; }
    public String getStatus()       { return status; }
    public String getTopBidder()    { return topBidder; }
    public long   getEndTime()      { return endTime; }

    public void setName(String name)              { this.name = name; }
    public void setDescription(String desc)       { this.description = desc; }
    public void setStartPrice(double startPrice)  { this.startPrice = startPrice; }
    public void setCurrentPrice(double price)     { this.currentPrice = price; }
    public void setStatus(String status)          { this.status = status; }
    public void setTopBidder(String topBidder)    { this.topBidder = topBidder; }
    public void setEndTime(long endTime)          { this.endTime = endTime; }
}
