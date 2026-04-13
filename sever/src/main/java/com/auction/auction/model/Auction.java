package com.auction.auction.model;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import com.auction.bid.model.BidTransaction;
import com.auction.item.model.Item;
import com.auction.user.model.Seller;
import com.auction.user.model.User;

import com.auction.common.observer.AuctionEvent;
import com.auction.common.observer.AuctionEventManager;
import com.auction.common.observer.EventType;

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
    private List<AutoBid> autoBids = new ArrayList<>();

// contructor
    public Auction(Item item , Seller seller , long durationMillis) {
        this.id = UUID.randomUUID().toString();
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

            AuctionEventManager.getInstance().publish(id,
            new AuctionEvent(EventType.AUCTION_STARTED, id, item.getName(),
                     currentPrice, null));

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

        AuctionEventManager.getInstance().publish(id,
            new AuctionEvent(EventType.BID_PLACED, id, item.getName(),
                    currentPrice, bidder.getName()));


        System.out.println(bidder.getName() + " bid " + amount);

        processAutoBids();

        return true;

        
    }

    public synchronized void endAuction(){
        if (status == AuctionStatus.FINISHED) return ;

        status = AuctionStatus.FINISHED ;

        System.out.println("Auction ended: " + item.getName());
        String winnerName = (highestBidder != null) ? highestBidder.getName() : null;
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
    public long getStartTime() {return startTime;}
    public long getEndTime() {return endTime;}
    public void registerAutoBid(AutoBid autoBid) {autoBids.add(autoBid);}

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

                bidHistory.add(new BidTransaction(
                            auto.getUser().getId(),
                            auto.getUser().getName(),
                            this.id,
                            nextBid,
                            System.currentTimeMillis()));

                System.out.println("[AUTO] "
                        + auto.getUser().getName()
                        + " bid " + nextBid);
                
                AuctionEventManager.getInstance().publish(id,
                new AuctionEvent(EventType.AUTO_BID_PLACED, id, item.getName(),
                     nextBid, auto.getUser().getName()));

                updated = true;
            }
        }

    } while (updated);
}
}