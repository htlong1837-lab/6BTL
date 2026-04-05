import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
 
public class BidTransaction {
    private static int counter = 1;
 
    private final String transactionId;
    private final User bidder;
    private final double amount;
    private final LocalDateTime timestamp;
 
    public BidTransaction(User bidder, double amount) {
        this.transactionId = "TX" + String.format("%04d", counter++);
        this.bidder = bidder;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }
 
    // Getter
    public String getTransactionId() { return transactionId; }
    public User getBidder()          { return bidder; }
    public double getAmount()        { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
 
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        return "[" + transactionId + "] "
                + bidder.getName()
                + " bid " + String.format("%,.0f", amount) + " VND"
                + " at " + timestamp.format(fmt);
    }
}