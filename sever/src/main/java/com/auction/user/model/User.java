package com.auction.user.model;
import com.auction.common.model.Entity;

public abstract class User extends Entity {
    protected String name;
    protected String passwordHash;
    protected double balance;
    private boolean isBanned = false;
    private String role;

    public User(String id, String name, String passwordHash, String role) {
        super(id);
        this.name = name;
        this.passwordHash = passwordHash;
        this.balance = 0.0;
        this.isBanned = false;
        this.role = role;
    }

    // Getters
    public String getName()         { return name; }
    public String getPasswordHash() { return passwordHash; }
    public double getBalance()      { return balance; }
    public boolean isBanned()       { return isBanned; }
    public String getRole()         { return role; }
    // Setters
    public void setName(String name)         { this.name = name; }
    public void setPassword(String hash)     { this.passwordHash = hash; }

    // quyền ban của admin
    public void setBanned(boolean banned)    { this.isBanned = banned; }

    // [THÊM] Cần để UserDAOSQLiteImpl khôi phục số dư từ DB khi load user (Seller/Admin không có setBalance riêng)
    public void setBalance(double balance)   { this.balance = balance; }

    @Override
    public void printInfo() {
        System.out.println("ID   : " + id);
        System.out.println("Name : " + name);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + ", name=" + name + "]";
    }

    
}