package com.auction.client.model;

public class UserFake{
    protected String name;
    protected String email;
    protected String passwordHash;
    protected double balance;
    private int failedLoginAttempts = 0;
    private boolean isBanned = false;
    private String id;

    public UserFake(String id, String name, String email, String passwordHash) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.balance = 0.0;
        this.failedLoginAttempts = 0;
        this.isBanned = false;
    }

    // Getters
    public String getId()           { return id; }
    public String getName()         { return name; }
    public String getEmail()        { return email; }
    public String getPasswordHash() { return passwordHash; }
    public double getBalance()      { return balance; }
    public boolean isBanned()       { return isBanned; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }

    // Setters
    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String hash)     { this.passwordHash = hash; }
    public void incrementFailedLoginAttempts() { this.failedLoginAttempts ++; }
    public void resetFailedLoginAttempts() { this.failedLoginAttempts = 0; }
    
}
