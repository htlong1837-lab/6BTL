package com.auction.user.model;
public class Admin extends User {
    public Admin(String id, String name, String passwordHash) {
        super(id, name, passwordHash, "Admin");
    }
    
    @Override
    public void printInfo() {
        super.printInfo();
    }

}
