package com.auction.auction.model;

import java.util.ArrayList;
import java.util.List;

import com.auction.bid.model.BidTransaction;
import com.auction.item.model.Item;
import com.auction.user.model.Seller;
import com.auction.user.model.User;

public class Auction {

    private Item item; 
    private Seller seller;
    private double currentPrice;

    private User highestBidder;
    private AuctionStatus status;
    private List<BidTransaction> bidHistory;

    private long startTime;
    private long endTime;
    private List<AutoBid> autoBids = new ArrayList<>();

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
    public synchronized boolean placeBid(User bidder ,double amount ) {// chặn hành trường hợp 2 thằng cùng đặt giá
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

        
        BidTransaction bid = new BidTransaction(bidder.getId(), bidder.getName(), item.getId(), amount, System.currentTimeMillis());

        bidHistory.add(bid);

        System.out.println(bidder.getName() + " bid " + amount);

        return true;

        processAutoBids();
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

        
    public User getHighestBidder() {
        return highestBidder;
    }

    public List<BidTransaction> getBidHistory() {
            return bidHistory;
        }

    public AuctionStatus getStatus() {
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

    public void registerAutoBid(AutoBid autoBid) {
    autoBids.add(autoBid);
    }

    private void processAutoBids() {

    boolean updated;

    do {
        updated = false;

        for (AutoBid auto : autoBids) {

            // nếu user đang dẫn đầu thì bỏ qua
            if (auto.getUser().equals(highestBidder)) continue;

            double nextBid = currentPrice + auto.getIncrement();

            if (nextBid <= auto.getMaxBid()) {

                currentPrice = nextBid;
                highestBidder = auto.getUser();

                bidHistory.add(new BidTransaction(auto.getUser(),nextBid,System.currentTimeMillis()));

                System.out.println("[AUTO] "
                        + auto.getUser().getName()
                        + " bid " + nextBid);

                updated = true;
            }
        }

    } while (updated);
}
}