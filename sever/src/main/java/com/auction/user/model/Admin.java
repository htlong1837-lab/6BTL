package com.auction.user.model;

import java.util.List;

import com.auction.auction.model.Auction;
import com.auction.item.model.Product.Item;

public class Admin extends User {
    public Admin(String id, String name, String passwordHash) {
        super(id, name, passwordHash, "Admin");
    }
    
    @Override
    public void printInfo() {
        super.printInfo();
    }

}
