package com.auction.user.model;
// full bộ của máy cu đấu giá
public class Bidder extends User {
    public Bidder(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);

<<<<<<< Updated upstream
    }
    public double deposit(double amount) {
        if (amount <=0) {
            throw new IllegalArgumentException("không thể nạo tiền do giá trị nạp nhỏ hơn 0");
        }
        this.balance += amount;
        return this.balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
    public boolean hasSufficientBalance(double amount) {
        return this.balance >= amount;
    }
    public double withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("số tiền rút phải lớn hơn 0");
        }
        if (!hasSufficientBalance(amount)) {
            throw new IllegalArgumentException("số dư không đủ để rút");
        }
        this.balance -= amount;
        return this.balance;
    }
}
=======
package com.auction.user.model;

import com.auction.bid.model.BidTransaction;
import java.util.ArrayList;
import java.util.List;

public class Bidder extends User {

    private List<BidTransaction> myBids;

    public Bidder(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);
        this.myBids = new ArrayList<>();
    }

    // Lịch sử bid của bidder này
    public List<BidTransaction> getMyBids() { return myBids; }

    public void addBidRecord(BidTransaction tx) { myBids.add(tx); }

    // Kiểm tra số dư — hành vi của chính bidder, để ở đây hợp lý
    public boolean hasSufficientBalance(double amount) {
        return balance >= amount;
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Balance     : " + balance + " VND");
        System.out.println("Total bids  : " + myBids.size());
    }
}
>>>>>>> Stashed changes
