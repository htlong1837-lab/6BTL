import java.util.ArrayList;
import java.util.List;

public class Auction{
    private Item item;
    private Seller seller;

    private double currentPrice;
    private User highestBidder;

    private AuctionStatus status;
    private List<BidTransaction> bidHistory
    
    private long endTime;

    public Auction(Item item , Seller seller , long durationMillis){
        this.item = item;
        this.seller = seller;

        this.currentPrice = item.getStartPrice();
        this.status = AuctionStatus.OPEN;

        this.bidHistory = new ArrayList<>();

        this.endTime = System.currentTimeMillis() + durationMillis;

    }

    public void start() {
        if (status == AuctionStatus.OPEN ){
            status == AutionStatus.RUNNING;
            System.out.print("Aution starts");
        }
    }

    public boolean placeBid(User bidder , double amount) {
        if (status != AuctionStatus.RUNNING) {
            System.out.println("Auction is not running");
            return false;
        }

        if (System.currentTimeMillis() > endTime){
            endAuction();
            return false;
        }

        if (amount <= currentPrice){
            System.out.println("del du tien del dc mua");
            return false;
        }
    }

    public void endAuction() {
        if (status == AuctionStatus.FINISHED) return;

        status = AuctionStatus.FINISHED;

        System.out.println("Auction ended");
    }

    if (highestBidder != null) {
        System.out.println( " Winner : " + highestBidder.name);
        System.out.println( " Final price : " + currentPrice);

    }

    else {
        System.out.println( " No bids placed ");
    }

    public void checkAndClose() {
        if ( status == AuctionStatus.RUNNING && System.currentTImeMillis() > endTime ){
           endAuction(); 
        }
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public User getHighestBidder() {
        return highestBidder;
    }

    public List<BidTransaction> getBidHistory(){
        return bidHistory;
    }


}