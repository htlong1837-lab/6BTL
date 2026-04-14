package com.auction.user.model;
// full bộ của máy cu đấu giá
package com.auction.user.model;

import com.auction.bid.model.BidTransaction;
import java.util.ArrayList;
import java.util.List;
public class Bidder extends User {
    public Bidder(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);

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