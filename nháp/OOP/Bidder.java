import java.util.ArrayList;
import java.util.List;


public class Bidder extends User {
    private double balance;
    private List<BidTransaction> myBids;

    public Bidder(String id, String name, String email, String password, double balance) {
        super(id, name, email, password);
        this.balance = balance;
        this.myBids = new ArrayList<>();
    }
    public double getBalance() {
        return balance;
    }
    public void setbalance(double balance) {
        this.balance = balance;
    }
    public List<BidTransaction> getMyBids() { return myBids; }
    public void addBidRecord(BidTransaction tx) {
        myBids.add(tx);

    }
    public boolean hasSufficientBalance(double amount) {
        return balance >= amount;
    }
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Balance: " + balance + " VND");
        System.out.println("Total bids placed: " + myBids.size());
    }
}
