package com.auction.auction.model;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import com.auction.bid.model.BidTransaction;
import com.auction.exception.AutionException.AuctionClosedException;
import com.auction.exception.AutionException.BidTooLowException;
import com.auction.user.model.Seller;
import com.auction.user.model.User;

import com.auction.common.observer.AuctionEvent;
import com.auction.common.observer.AuctionEventManager;
import com.auction.common.observer.EventType;
import com.auction.item.model.Product.Item;

public class Auction {
    private String id;
    private Item item; 
    private Seller seller;
    private double currentPrice;

    private User highestBidder;
    private AuctionStatus status;
    private List<BidTransaction> bidHistory;

    private long startTime;
    private long endTime;

// contructor
    public Auction(Item item , Seller seller , long durationMillis) {
        this.id = UUID.randomUUID().toString();
        this.item = item;
        this.seller = seller;
        this.currentPrice = item.getStartPrice(); // cần định nghĩa hàm getStartPrice
        this.status = AuctionStatus.OPEN;

        this.bidHistory = new ArrayList<>();

        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + durationMillis;

    }

    // Constructor phục hồi trạng thái từ DB — không sinh UUID mới, không gọi start()
    public Auction(String id, Item item, Seller seller, double currentPrice,
                   User highestBidder, AuctionStatus status, long startTime, long endTime) {
        this.id            = id;
        this.item          = item;
        this.seller        = seller;
        this.currentPrice  = currentPrice;
        this.highestBidder = highestBidder;
        this.status        = status;
        this.bidHistory    = new ArrayList<>();
        this.startTime     = startTime;
        this.endTime       = endTime;
    }

// begin to dau gia 
    public void start() {
        if (status == AuctionStatus.OPEN) {
            status = AuctionStatus.RUNNING;

            AuctionEventManager.getInstance().publish(id,
            new AuctionEvent(EventType.AUCTION_STARTED, id, item.getName(),
                     currentPrice, null));

            System.out.println("Aution started: " + item.getName()); //cần định nghĩa hàm getNAme
        }
    }

// đặt giá
    public synchronized void placeBid(User bidder, double amount)
            throws AuctionClosedException, BidTooLowException {
        if (System.currentTimeMillis() > endTime) {
            endAuction();
        }

        if (status != AuctionStatus.RUNNING) {
            throw new AuctionClosedException("Phiên đấu giá đã kết thúc hoặc chưa bắt đầu.");
        }

        if (amount <= currentPrice) {
            throw new BidTooLowException("Giá đặt phải cao hơn giá hiện tại: " + currentPrice + " VND");
        }

        currentPrice = amount;
        highestBidder = bidder;
        
        BidTransaction bid = new BidTransaction(bidder.getId(), bidder.getName(), item.getId(), amount, System.currentTimeMillis());
        
        bidHistory.add(bid);
        
        double remainingTime = endTime - System.currentTimeMillis();
        if (remainingTime < 20000){
            endTime += 60000;
            System.out.println("AntiSniping gia hạn thêm 60s");

            AuctionEventManager.getInstance().publish(id,
                new AuctionEvent(EventType.AUCTION_EXTENDED, id,item.getName(),currentPrice, null));
        }

        

        AuctionEventManager.getInstance().publish(id,
            new AuctionEvent(EventType.BID_PLACED, id, item.getName(),
                    currentPrice, bidder.getName()));


        System.out.println(bidder.getName() + " bid " + amount);
    }

    public synchronized void endAuction(){
        if (status == AuctionStatus.FINISHED) return ;

        status = AuctionStatus.FINISHED ;

        System.out.println("Auction ended: " + item.getName());

        String winnerName;
        if (highestBidder != null) {
            winnerName = highestBidder.getName();
        } else {
            winnerName = null;
        }
        AuctionEventManager.getInstance().publish(id,
            new AuctionEvent(EventType.AUCTION_ENDED, id, item.getName(),
                     currentPrice, winnerName));
        AuctionEventManager.getInstance().clearListeners(id);

        
        if (highestBidder != null){
            System.out.println("Winner: " + highestBidder.getName());
            System.out.println("Final price: " + currentPrice);
        }
        else {
            System.out.println("No bids placed");
        }
    }

    public synchronized void checkAndClose() {
        if (status == AuctionStatus.RUNNING && System.currentTimeMillis() > endTime){
            endAuction();
        }
    }

    public String getId()  { return id; }
    public User getHighestBidder() {return highestBidder;}
    public List<BidTransaction> getBidHistory() {return bidHistory;}
    public AuctionStatus getStatus() {return status ;}
    public Item getItem() {return item;}
    public Seller getSeller() { return seller;}
    public double getCurrentPrice() { return currentPrice; }
    public long getStartTime() {return startTime;}
    public long getEndTime() {return endTime;}
    
}