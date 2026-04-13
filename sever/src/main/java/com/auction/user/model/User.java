package com.auction.user.model;
import com.auction.common.model.Entity;

public abstract class User extends Entity {
    protected String name;
    protected String email;
    protected String passwordHash;
    protected double balance;

    public User(String id, String name, String email, String passwordHash) {
        super(id);
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.balance = 0.0;
    }

    // Getters
    public String getName()         { return name; }
    public String getEmail()        { return email; }
    public String getPasswordHash() { return passwordHash; }
    public double getBalance()      { return balance; }

    // Setters
    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String hash)     { this.passwordHash = hash; }

    @Override
    public void printInfo() {
        System.out.println("ID   : " + id);
        System.out.println("Name : " + name);
        System.out.println("Email: " + email);
        System.out.println("Role : " + getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + ", name=" + name + "]";
    }
    // Đăng nhập
    public boolean login(boolean isSuccess) {
        if (isSuccess) {
            System.out.println("User \"" + name + "\" logged in successfully.");
            return true;
        } else {
            System.out.println("Login failed for user \"" + name + "\". Please check your credentials and try again.");
            return false;
        }
    }
}