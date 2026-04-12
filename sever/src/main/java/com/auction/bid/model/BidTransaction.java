package com.auction.bid.model;

public class BidTransaction {
    private String bidderId;
    private String bidderName;
    private String auctionId;
    private double amount;
    private long timestamp;
    public BidTransaction(String bidderId, String bidderName, String auctionID, double amount, long timestamp) {
        this.bidderId = bidderId;
        this.bidderName = bidderName;
        this.auctionId = auctionID;
        this.amount = amount;
        this.timestamp = timestamp;
    }
    public String getBidderId() {
        return bidderId;
    
    }
    public String getBidderName() {
        return bidderName;
    }
    public String getAuctionId()  { return auctionId; }
    public double getAmount()     { return amount; }
    public long getTimestamp()    { return timestamp; }
    @Override
    public String toString() {
        return "Bid[bidder=" + bidderName + ", amount=" + amount + ", time=" + timestamp + "]";
    }
                
}
