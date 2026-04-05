package main.java.com.auction.auction.model;

import java.util.ArrayList;
import java.util.List;

import main.java.com.auction.bid.model.BidTransaction;
import main.java.com.auction.item.model.Item;
import main.java.com.auction.user.model.Seller;
import main.java.com.auction.user.model.User;

public class Auction {

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
        this.item = item;
        this.seller = seller;
        this.currentPrice = item.getStartPrice(); // cần định nghãi hàm getStartPrice
        this.status = AuctionStatus.OPEN;

        this.bidHistory = new ArrayList<>();

        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + durationMillis;

    }

// begin to dau gia 
    public void start() {
        if (status == AuctionStatus.OPEN) {
            status = AuctionStatus.RUNNING;
            System.out.println("Aution started: " + item.getName()); //cần định nghĩa hàm getNAme
        }
    }

// đặt giá 
    public synchronized boolean placeBid(User bidder ,double amount ) {
// neu phiên chưa chạy
        if (status != AuctionStatus.RUNNING) {
            System.out.println(" Phiên đấu giá chưa chạy");
            return false;
        }

// nếu hết thời gian
        if (System.currentTimeMillis() > endTime) {
            endAuction(); // tí định nghĩa
            return false;
        }        

// giá ko hợp lệ 
        if ( amount <= currentPrice){
            System.out.println("bids too low");
            return false;
        }

        currentPrice = amount;
        highestBidder = bidder;

        
        BidTransaction bid = new BidTransaction(bidder , amount , System.currentTimeMillis() );

        bidHistory.add(bid);

        System.out.println(bidder.getName() + " bid " + amount);

        return true;
    }

    public void endAuction(){
        if (status == AuctionStatus.FINISHED) return ;

        status = AuctionStatus.FINISHED ;

        System.out.println("Auction ended: " + item.getName());
        
        if (highestBidder != null){
            System.out.println("Winner: " + highestBidder.getName());
            System.out.println("Final price: " + currentPrice);
        }
        else {
            System.out.println("No bids placed");
        }
    }

    public void checkAndClose() {
        if (status == AuctionStatus.RUNNING && System.currentTimeMillis() > endTime){
            endAuction();
        }
    }

        
    public void User getHighestBidder() {
        return highestBidder;
    }

    public List<BidTransaction> getBidHistory() {
            return bidHistory;
        }

    public AuctionStatus geStatus() {
        return status ;
    }

    public Item getItem() {
        return item;
    }

    public Seller getSeller() {
        return seller;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}