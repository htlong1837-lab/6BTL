package main.java.com.auction.auction.model;

import java.util.ArrayList;
import java.util.List;

import main.java.com.auction.bid.model.BidTransaction;

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
        }        

    }

    public void endAuction(){}

    public void checkAndClose() {}

    public void User getHighestBidder(){}

    public void 
}
