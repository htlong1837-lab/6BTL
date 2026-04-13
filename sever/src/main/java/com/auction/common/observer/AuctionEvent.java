package com.auction.common.observer;

public class AuctionEvent {
    private final EventType type;
    private final String auctionId;//nvu cho client biết client này thuộc phiên nào
    private final String itemName;
    private final double currentPrice;
    private final String highestBidderName;
    private final long timestamp;

    public AuctionEvent(EventType type,String auctionId , 
        String itemName , double currentPrice, String highestBidderName){
        this.type = type;
        this.auctionId = auctionId;
        this.itemName = itemName;
        this.currentPrice = currentPrice;
        this.highestBidderName = highestBidderName;
        this.timestamp = System.currentTimeMillis();

    }
// các hàm getter thông thuồng
    public EventType getType(){ 
        return type; 
    }

    public String getAuctionId(){
        return auctionId; 
    }
    public String getItemName() {
        return itemName; 
    }
    public double getCurrentPrice() {
        return currentPrice; 
    }
    public String getHighestBidderName()  {
        return highestBidderName; 
    }
    public long getTimestamp(){
        return timestamp; 
    }

    public String toString() {
        return "[" + type + "] " + itemName
                + " | price=" + currentPrice
                + " | leader=" + highestBidderName
                + " | time=" + timestamp; 
    // in ra thông tin event
    }

}