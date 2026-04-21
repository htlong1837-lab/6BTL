package com.auction.user.model;
// full bộ của máy cu đấu giá

import com.auction.bid.model.BidTransaction;
import java.util.ArrayList;
import java.util.List;
public class Bidder extends User {
    public Bidder(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);

    }
    public double deposit(double amount) {
        if (amount <=0) {
            throw new IllegalArgumentException("Không thể nạp tiền do giá trị nạp nhỏ hơn 0");
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
            throw new IllegalArgumentException("Số tiền rút phải lớn hơn 0");
        }
        if (!hasSufficientBalance(amount)) {
            throw new IllegalArgumentException("Số dư không đủ để rút");
        }
        this.balance -= amount;
        return this.balance;
    }
}